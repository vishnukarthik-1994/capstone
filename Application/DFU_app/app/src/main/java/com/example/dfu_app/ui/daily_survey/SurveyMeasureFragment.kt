package com.example.dfu_app.ui.daily_survey

import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.example.dfu_app.databinding.FragmentSurveyMeasurementBinding
import com.example.dfu_app.ui.error_message.ErrorMessage.setErrorMessage
import java.io.IOException


class SurveyMeasureFragment: Fragment() {

    private val shareViewModel: SurveyViewModel by activityViewModels()
    private var _binding: FragmentSurveyMeasurementBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private lateinit var bluetoothManager: BluetoothManager
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
    private val binding get() = _binding!!
    private val REQUEST_ENABLE_BLUETOOTH = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bluetoothManager = (requireContext().getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager)
        bluetoothAdapter = bluetoothManager.adapter
        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                if (result.resultCode == Activity.RESULT_OK) {
                    if (bluetoothAdapter!!.isEnabled) {
                        setErrorMessage(requireContext(), "Bluetooth has been enabled")
                    } else {
                        setErrorMessage(requireContext(), "Bluetooth has been disabled")
                    }
                } else if (result.resultCode == Activity.RESULT_CANCELED) {
                    setErrorMessage(requireContext(), "Bluetooth enabling has been canceled")
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
            measureButton.setOnClickListener { askBluetoothPermission() }
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
    }
    // sample code to get data from arduino
    private fun readBlueToothDataFromMotherShip(bluetoothSocket: BluetoothSocket) {
        Log.i(ContentValues.TAG, Thread.currentThread().name)
        val bluetoothSocketInputStream = bluetoothSocket.inputStream
        val buffer = ByteArray(1024)
        var bytes: Int
        //Loop to listen for received bluetooth messages
        while (true) {
            try {
                bytes = bluetoothSocketInputStream.read(buffer)
                val readMessage = String(buffer, 0, bytes)
            } catch (e: IOException) {
                e.printStackTrace()
                break
            }
        }
    }
    //Put away keyboard if unnecessary
    private fun closeKeyBoards(){
        val manager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        manager.hideSoftInputFromWindow(
            requireActivity().currentFocus!!.windowToken,
            InputMethodManager.HIDE_NOT_ALWAYS
        )
    }
}