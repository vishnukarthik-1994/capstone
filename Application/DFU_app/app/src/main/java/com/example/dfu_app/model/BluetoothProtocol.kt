package com.example.dfu_app.model

import android.annotation.SuppressLint
import android.bluetooth.*
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.ContentValues
import android.content.Context
import android.os.Handler
import android.util.Log
import com.example.dfu_app.R
import com.example.dfu_app.ui.daily_survey.GattAttributes
import com.example.dfu_app.ui.daily_survey.SurveyViewModel
import com.example.dfu_app.ui.error_message.ErrorMessage
class BluetoothProtocol(
    bluetoothManager: BluetoothManager,
    private val viewModel: SurveyViewModel,
    private val context: Context
) {
    private var bluetoothAdapter = bluetoothManager.adapter
    private var bluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner
    private var bluetoothGatt: BluetoothGatt? = null
    private var scanning = false
    private var found = false
    private val deviceName:String = "DFU Nano"
    private val handler = Handler()
    // Stops scanning after 10 seconds.
    private val SCAN_PERIOD: Long = 20000
    @SuppressLint("MissingPermission")
    fun scanLeDevice() {
        //avoid the device is already be occupied
        disconnectDevice()
        found = false
        Log.d(ContentValues.TAG, "start scanning")
        if (!scanning) {
            // Stops scanning after a pre-defined scan period.
            handler.postDelayed(stopLeScan(), SCAN_PERIOD)
            viewModel.setStatus(context.resources.getString(R.string.find_device))
            // Start the scan
            scanning = true
            bluetoothLeScanner?.startScan(leScanCallback)
        } else {
            // Will hit here if we are already scanning
            scanning = false
            bluetoothLeScanner?.stopScan(leScanCallback)
        }
    }
    @SuppressLint("MissingPermission")
    private fun stopLeScan(): () -> Unit = {
        scanning = false
        bluetoothLeScanner?.stopScan(leScanCallback)
        Log.i(ContentValues.TAG, "Stop service discovery")
    }

    @SuppressLint("MissingPermission")
    fun disconnectDevice() {
        bluetoothGatt?.disconnect()
    }

    // Device scan callback.
    private val leScanCallback: ScanCallback = object : ScanCallback() {
        @SuppressLint("MissingPermission")
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            val device = result.device
            if (!device.name.isNullOrEmpty()) {
                Log.d(ContentValues.TAG, device.name)
            }
            //if current device is arduino
            if (device.name == deviceName && !found) {
                connectToDevice(device)
                found = true
                ErrorMessage.setErrorMessage(context, "Success connect to $deviceName")
                viewModel.setStatus(context.resources.getString(R.string.waiting_response))
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun connectToDevice(device:BluetoothDevice) {
        bluetoothAdapter.let { adapter ->
            try {
                // connect to the GATT server on the device
                bluetoothGatt = adapter.getRemoteDevice(device.address).connectGatt(context, false, bluetoothGattCallback)
                bluetoothLeScanner?.stopScan(leScanCallback)
                Log.i(ContentValues.TAG, "Stop sign trigger by connectToDevice func")
            } catch (exception: IllegalArgumentException) {
                Log.w(ContentValues.TAG, "Device not found with provided address.  Unable to connect.")
            }
            return
        }
    }
    private val bluetoothGattCallback = object : BluetoothGattCallback() {
        @SuppressLint("MissingPermission")
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                // successfully connected to the GATT Server
                Log.i(ContentValues.TAG, "Starting service discovery")
                bluetoothGatt?.discoverServices()
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                // disconnected from the GATT Server
                Log.i(ContentValues.TAG, "Disconnecting GATT service ")
            }
        }
        @SuppressLint("MissingPermission")
        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.i(ContentValues.TAG, "Services discovered")
                checkAndConnectToHRM(bluetoothGatt?.services)
            } else {
                Log.w(ContentValues.TAG, "onServicesDiscovered received: $status")
            }
        }

        @SuppressLint("MissingPermission")
        override fun onCharacteristicRead(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?,
            status: Int
        ) {
            super.onCharacteristicRead(gatt, characteristic, status)
            if (status == BluetoothGatt.GATT_SUCCESS) {
                val data = characteristic!!.getStringValue(0)
                viewModel.getFootTemp(data)
                Log.w(ContentValues.TAG, "Foot Temperature: $data")
                viewModel.setMeasureBtn(true)
                viewModel.setStatus(context.resources.getString(R.string.measure))
            }
        }

        @SuppressLint("MissingPermission")
        override fun onCharacteristicChanged(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?
        ) {
            super.onCharacteristicChanged(gatt, characteristic)
            Log.i(ContentValues.TAG, characteristic?.value?.get(1)?.toUByte().toString())
            val data = characteristic!!.getStringValue(0)
            Log.w(ContentValues.TAG, "Updated Foot Temperature: $data")
        }
    }

    @SuppressLint("MissingPermission")
    private fun checkAndConnectToHRM(services: List<BluetoothGattService>?) {
        Log.i(ContentValues.TAG, "Checking for HRM Service")
        services?.forEach { service ->
            if (service.uuid == GattAttributes.TEMPERATURE_SERVICE_UUID){
                Log.i(ContentValues.TAG, "Receive arduino response")
                val characteristic = service.getCharacteristic(GattAttributes.TEMPERATURE_MEASUREMENT_UUID)
                bluetoothGatt?.readCharacteristic(characteristic)
            }
        }
    }
}