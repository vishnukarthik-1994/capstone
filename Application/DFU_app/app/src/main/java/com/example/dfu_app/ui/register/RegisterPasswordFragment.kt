package com.example.dfu_app.ui.register

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.dfu_app.R
import com.example.dfu_app.databinding.FragmentRegisterPasswordBinding
import com.example.dfu_app.ui.error_message.ErrorMessage.setErrorMessage


class RegisterPasswordFragment: Fragment() {

    private val viewModel: RegisterViewModel by activityViewModels {
        RegisterViewModelFactory(
            (activity?.application as RegisterApplication).userInfoDatabase.userInfoDao())
    }
    private var _binding: FragmentRegisterPasswordBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegisterPasswordBinding.inflate(inflater, container, false)
        return binding.root
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

    @SuppressLint("ClickableViewAccessibility")
    private fun bind( ) {
        binding.apply {
            nextStepButton.setOnClickListener { next()}
            returnButton.setOnClickListener { back() }
            showPasswordSwitch.setOnCheckedChangeListener { compoundButton, b ->
                if (compoundButton.isChecked){
                    binding.passwordConfirmEditText.transformationMethod = HideReturnsTransformationMethod.getInstance()
                    binding.passwordEditText.transformationMethod = HideReturnsTransformationMethod.getInstance()
                }
                else{
                    binding.passwordConfirmEditText.transformationMethod = PasswordTransformationMethod.getInstance()
                    binding.passwordEditText.transformationMethod = HideReturnsTransformationMethod.getInstance()
                }
            }

            requireView().setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    if (requireActivity().currentFocus != null && requireActivity().currentFocus!!.windowToken != null) {
                        closeKeyBoards()
                    }
                }
                false
            }
        }
    }
    private fun next(){
        //check password
        if ( !viewModel.checkInput(binding.passwordEditText.text.toString())){
            binding.password.isErrorEnabled = true
            binding.password.error = getString(R.string.emptyInput)
            setErrorMessage(requireContext(),getString(R.string.emptyInput))
            return
        }
        else
        {
            binding.password.isErrorEnabled = false
        }
        //check match
        if (binding.passwordEditText.text.toString() != binding.passwordConfirmEditText.text.toString()){
            var password = binding.passwordEditText.text.toString()
            var confirm = binding.passwordConfirmEditText.text.toString()
            binding.password.isErrorEnabled = true
            binding.password.error = getString(R.string.password_mismatch)
            binding.passwordConfirm.isErrorEnabled = true
            binding.passwordConfirm.error = getString(R.string.password_mismatch)
            setErrorMessage(requireContext(),getString(R.string.password_mismatch))
            return
        }
        else
        {
            binding.password.isErrorEnabled = false
            binding.passwordConfirm.isErrorEnabled = false
        }
        viewModel.setPassword(binding.passwordEditText.text.toString())
        closeKeyBoards()
        val action = RegisterPasswordFragmentDirections.actionNavRegisterPasswordToNavRegisterUserinfo()
        this.findNavController().navigate(action)
    }
    private fun back(){
        closeKeyBoards()
        val action = RegisterPasswordFragmentDirections.actionNavRegisterPasswordToNavLogin()
        this.findNavController().navigate(action)
    }
    private fun closeKeyBoards(){
        val manager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        manager.hideSoftInputFromWindow(
            requireActivity().currentFocus!!.windowToken,
            InputMethodManager.HIDE_NOT_ALWAYS
        )
    }
}