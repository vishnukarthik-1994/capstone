package com.example.dfu_app.ui.daily_survey

import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.*
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.dfu_app.R
import com.example.dfu_app.databinding.FragmentSurveyMeasurementBinding
import com.example.dfu_app.ui.error_message.ErrorMessage.setErrorMessage
import com.example.dfu_app.ui.error_message.ErrorMessage.setMessage


class SurveyMeasureFragment: Fragment() {
    private val viewModel: SurveyViewModel by activityViewModels()
    private var _binding: FragmentSurveyMeasurementBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private var  bluetoothModule:Boolean = false
    private var scanning = false
    private var found = false
    private lateinit var bluetoothManager: BluetoothManager
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private var bluetoothLeScanner: BluetoothLeScanner? = null
    private var bluetoothGatt: BluetoothGatt? = null
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
    private val binding get() = _binding!!
    private val deviceName:String = "DFU Nano"
    private val handler = Handler()
    // Stops scanning after 10 seconds.
    private val SCAN_PERIOD: Long = 20000
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bluetoothManager = (requireContext().getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager)
        if (bluetoothManager.adapter != null) {
            bluetoothModule = true
            bluetoothAdapter = bluetoothManager.adapter
            bluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                if (result.resultCode == Activity.RESULT_OK) {
                    if (bluetoothAdapter.isEnabled) {
                        setErrorMessage(requireContext(), "Bluetooth has been enabled")
                        scanLeDevice()
                    } else {
                        setErrorMessage(requireContext(), "Bluetooth has been disabled")
                    }
                } else if (result.resultCode == Activity.RESULT_CANCELED) {
                    setErrorMessage(requireContext(), "Bluetooth enabling has been canceled")
                }
            }.also { resultLauncher = it }
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //remove previous view
        container?.removeAllViews()
        _binding = FragmentSurveyMeasurementBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        bind()
        setMessage(requireContext(),getString(R.string.notification))
    }
    @SuppressLint("ClickableViewAccessibility")
    private fun bind( ) {
        binding.apply {
            measureButton.isEnabled = bluetoothModule
            if (!bluetoothModule) {
                submitButton.text = getString(R.string.skip)
                measureButton.text = getString(R.string.no_bluetooth)
            }
            measureButton.setOnClickListener { askBluetoothPermission()
                measureButton.isEnabled = false}
            submitButton.setOnClickListener { next()}
        }
    }
    //ask bluetooth permission
    private fun askBluetoothPermission() {
        if (!bluetoothAdapter.isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            resultLauncher.launch(enableBtIntent)
        }
        else {
            scanLeDevice()
        }
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
                setErrorMessage(requireContext(),"Success connect to $deviceName")
                binding.measureButton.text = getString(R.string.waiting_response)
            }
        }
    }
    @SuppressLint("MissingPermission")
    private fun stopLeScan(): () -> Unit = {
        scanning = false
        bluetoothLeScanner?.stopScan(leScanCallback)
        binding.measureButton.isEnabled = true
        binding.measureButton.text = getString(R.string.measure)
        Log.i(ContentValues.TAG, "Stop service discovery")
    }
    @SuppressLint("MissingPermission")
    private fun scanLeDevice() {
        //avoid the device is already be occupied
        disconnectDevice()
        found = false
        Log.d(ContentValues.TAG, "start scanning")
        if (!scanning) {
            // Stops scanning after a pre-defined scan period.
            handler.postDelayed(stopLeScan(), SCAN_PERIOD)
            binding.measureButton.text = getString(R.string.find_device)
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
    private fun connectToDevice(device:BluetoothDevice) {
        bluetoothAdapter.let { adapter ->
            try {
                // connect to the GATT server on the device
                bluetoothGatt = adapter.getRemoteDevice(device.address).connectGatt(requireContext(), false, bluetoothGattCallback)
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
    @SuppressLint("MissingPermission")
    private fun disconnectDevice() {
        bluetoothGatt?.disconnect()
    }
    private fun next(){
        disconnectDevice()
        val action = SurveyMeasureFragmentDirections.actionNavSurveyMeasureToNavSurveyQuestionnaire()
        this.findNavController().navigate(action)
    }
}