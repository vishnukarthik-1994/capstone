package com.example.dfu_app.ui.daily_survey

import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.*
import android.content.Context
import android.content.Intent
import android.os.Bundle
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
import com.example.dfu_app.model.BluetoothProtocol
import com.example.dfu_app.ui.error_message.ErrorMessage.setErrorMessage
import com.example.dfu_app.ui.error_message.ErrorMessage.setMessage


class SurveyMeasureFragment : Fragment() {
    private val viewModel: SurveyViewModel by activityViewModels()
    private var _binding: FragmentSurveyMeasurementBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private var bluetoothModule: Boolean = false
    private lateinit var bluetoothProtocol: BluetoothProtocol
    private lateinit var bluetoothManager: BluetoothManager
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bluetoothManager =
            (requireContext().getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager)
        if (bluetoothManager.adapter != null) {
            bluetoothProtocol = BluetoothProtocol(bluetoothManager, viewModel, requireContext())
            bluetoothModule = true
            bluetoothAdapter = bluetoothManager.adapter
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                if (result.resultCode == Activity.RESULT_OK) {
                    if (bluetoothAdapter.isEnabled) {
                        setErrorMessage(requireContext(), "Bluetooth has been enabled")
                        bluetoothProtocol.scanLeDevice()
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
        setMessage(requireContext(), getString(R.string.notification))
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun bind() {
        binding.apply {
            measureButton.isEnabled = bluetoothModule
            if (!bluetoothModule) {
                submitButton.text = getString(R.string.skip)
                measureButton.text = getString(R.string.no_bluetooth)
            }
            else {
                viewModel!!.setStatus(getString(R.string.measure))
                viewModel!!.setMeasureBtn(true)
            }
            measureButton.setOnClickListener {
                askBluetoothPermission()
                measureButton.isEnabled = false
            }
            submitButton.setOnClickListener { next() }
        }
    }

    //ask bluetooth permission
    private fun askBluetoothPermission() {
        if (!bluetoothAdapter.isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            resultLauncher.launch(enableBtIntent)
        } else {
            bluetoothProtocol.scanLeDevice()
        }
    }

    private fun next() {
        bluetoothProtocol.disconnectDevice()
        val action =
            SurveyMeasureFragmentDirections.actionNavSurveyMeasureToNavSurveyQuestionnaire()
        this.findNavController().navigate(action)
    }
}