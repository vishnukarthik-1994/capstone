package com.example.dfu_app.ui.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.dfu_app.R
import com.example.dfu_app.data.UserInfo
import com.example.dfu_app.databinding.FragmentRegisterBinding
import com.example.dfu_app.ui.error_message.ErrorMessage
import java.sql.Types.NULL


class RegisterFragment: Fragment() {
    private val errorMessage = ErrorMessage()
    private val viewModel: RegisterViewModel by activityViewModels {
        RegisterViewModelFactory(
            (activity?.application as RegisterApplication).userInfoDatabase.userInfoDao())
    }
    private var _binding: FragmentRegisterBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getUserName()
        bind()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun bind( ) {
        binding.apply {
            registerSubmitButton.setOnClickListener { submit() }
        }
    }
    private fun isEntryValid(): Boolean {
        return  viewModel.isStringValid(
            binding.registerUserNameEditText.text.toString(),
            binding.registerUserPasswordEditText.text.toString(),
            binding.registerFirstNameEditText.text.toString(),
            binding.registerLastNameEditText.text.toString(),
            binding.registerUserWeightEditText.text.toString(),
            binding.registerUserHeightEditText.text.toString(),
            binding.registerUserAgeEditText.text.toString()
        )
    }
    private fun addAccount(){
        if (isEntryValid())
        {
            if(userNameValid()){
                setErrorUserName(false)
                if (isPasswordValid()){
                    setErrorUserName(false)
                    viewModel.createAccount(
                        binding.registerUserNameEditText.text.toString(),
                        binding.registerUserPasswordEditText.text.toString(),
                        binding.registerFirstNameEditText.text.toString(),
                        binding.registerLastNameEditText.text.toString(),
                        binding.registerUserWeightEditText.text.toString(),
                        binding.registerUserHeightEditText.text.toString(),
                        binding.registerUserAgeEditText.text.toString())
                    val action = RegisterFragmentDirections.actionNavRegisterToNavHome()
                    this.findNavController().navigate(action)
                }
                else{ setErrorConfirm(true) }
            }
            else{ setErrorUserName(true) }
        }
    }
    /*
    * Sets and resets the text field error status.
    */
    private fun setErrorConfirm(error: Boolean) {
        if (error) {
            binding.registerUserPasswordConfirm.isErrorEnabled = true
            binding.registerUserPasswordConfirm.error = getString(R.string.password_mismatch)
            errorMessage.setErrorMessage(requireContext(),getString(R.string.password_mismatch))
        } else {
            binding.registerUserPasswordConfirm.isErrorEnabled = false
            binding.registerUserPasswordConfirmText.text = null
        }
    }
    private fun setErrorUserName(error: Boolean) {
        if (error) {
            binding.registerUserName.isErrorEnabled = true
            binding.registerUserName.error = getString(R.string.account_exist)
            errorMessage.setErrorMessage(requireContext(),getString(R.string.account_exist))
        } else {
            binding.registerUserName.isErrorEnabled = false
            binding.registerUserNameText.text = null
        }
    }
    private fun isPasswordValid(): Boolean{
        if (binding.registerUserPasswordEditText.text.toString().equals(binding.registerUserPasswordConfirmEditText.text.toString(),true)){
            return true
        }
        return false
    }
    private fun userNameValid():Boolean{
        var nonvalid = viewModel.checkUserName(binding.registerUserNameEditText.text.toString())
        if (nonvalid){
            return false
        }
        return true
    }
    private fun submit(){
        addAccount()
    }
}