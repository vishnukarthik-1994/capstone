package com.example.dfu_app.ui.daily_survey

import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.dfu_app.R
import com.example.dfu_app.databinding.FragmentSurveyMeasurementBinding
import com.example.dfu_app.ui.error_message.ErrorMessage.setErrorMessage
import java.io.IOException


class SurveyMeasureFragment: Fragment() {

    private val shareViewModel: SurveyViewModel by activityViewModels()
    private var _binding: FragmentSurveyMeasurementBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private var  bluetoothModule:Boolean = false
    private var scanning = false
    private lateinit var bluetoothManager: BluetoothManager
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private var bluetoothLeScanner: BluetoothLeScanner? = null
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
    private val binding get() = _binding!!
    private val testName: MutableList<String> = mutableListOf()
    private val deviceName:String = "Nano33BLE"
    private val handler = Handler()
    // Stops scanning after 10 seconds.
    private val SCAN_PERIOD: Long = 10000
    private val REQUEST_ENABLE_BLUETOOTH = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bluetoothManager = (requireContext().getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager)
        if (bluetoothManager.adapter != null) {
            bluetoothModule = true
            bluetoothAdapter = bluetoothManager.adapter
            bluetoothLeScanner = bluetoothAdapter?.bluetoothLeScanner
            resultLauncher =
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
                }
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
        /*
        Create connection bluetooth to sensor
        * */
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bind()
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
            /*
            When measure start should receive data from arduino
            * */
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
            val test = result.device
//            testName.add(test.name)
            if (!test.name.isNullOrEmpty()) {
                Log.d(ContentValues.TAG, test.name)
            }
            binding.measureButton.text = test.name
            //if current device is arduino
            if (test.name == deviceName) {

            }
        }
    }
    @SuppressLint("MissingPermission")
    private fun stopLeScan(): () -> Unit = {
        scanning = false
        bluetoothLeScanner?.stopScan(leScanCallback)
        binding.measureButton.isEnabled = true
    }
    @SuppressLint("MissingPermission")
    private fun scanLeDevice() {
        Log.d(ContentValues.TAG, "start scanning")
        if (!scanning) {
            // Stops scanning after a pre-defined scan period.
//            Handler(Looper.getMainLooper()).postDelayed(stopLeScan(), SCAN_PERIOD)
            handler.postDelayed(stopLeScan(), SCAN_PERIOD)
            // Start the scan
            scanning = true
            bluetoothLeScanner?.startScan(leScanCallback)
        } else {
            // Will hit here if we are already scanning
            scanning = false
            bluetoothLeScanner?.stopScan(leScanCallback)
        }
    }
    // sample code to get data from arduino
//    private fun readBlueToothDataFromMotherShip(bluetoothSocket: BluetoothSocket) {
//        Log.i(ContentValues.TAG, Thread.currentThread().name)
//        val bluetoothSocketInputStream = bluetoothSocket.inputStream
//        val buffer = ByteArray(1024)
//        var bytes: Int
//        //Loop to listen for received bluetooth messages
//        while (true) {
//            try {
//                bytes = bluetoothSocketInputStream.read(buffer)
//                val readMessage = String(buffer, 0, bytes)
//            } catch (e: IOException) {
//                e.printStackTrace()
//                break
//            }
//        }
//    }
}