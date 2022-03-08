package com.example.dfu_app.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.dfu_app.R
import com.example.dfu_app.databinding.FragmentLoginBinding
import com.example.dfu_app.ui.error_message.ErrorMessage
import com.example.dfu_app.ui.register.RegisterApplication
import com.example.dfu_app.ui.register.RegisterViewModel
import com.example.dfu_app.ui.register.RegisterViewModelFactory

class LoginFragment: Fragment() {
    private val errorMessage = ErrorMessage()
    private var _binding: FragmentLoginBinding? = null
    private val viewModel: RegisterViewModel by activityViewModels {
        RegisterViewModelFactory(
            (activity?.application as RegisterApplication).userInfoDatabase.userInfoDao())
    }
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getUserInfo()
        bind()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun bind( ) {
        binding.apply {
            registerButton.setOnClickListener { registered() }
            signInButton.setOnClickListener{ login() }
        }
    }
    private fun login(){
        if (viewModel.checkLogin(binding.userNameEditText.text.toString(),
                binding.passwordEditText.text.toString()))
        {
            val action = LoginFragmentDirections.actionNavLoginToNavHome()
            this.findNavController().navigate(action)
            setErrorLogin(false)
        }
        else {
            setErrorLogin(true)
        }
    }
    private fun setErrorLogin(error: Boolean){
        if (error) {
            binding.userName.isErrorEnabled = true
            binding.password.isErrorEnabled = true
            binding.userName.error = getString(R.string.login_fail)
            binding.password.error = getString(R.string.login_fail)
            errorMessage.setErrorMessage(requireContext(),getString(R.string.login_fail))
        } else {
            binding.userName.isErrorEnabled = false
            binding.password.isErrorEnabled = false
            binding.userNameEditText.text = null
        }
    }
    private fun registered(){
        val action = LoginFragmentDirections.actionNavLoginToNavRegister()
        this.findNavController().navigate(action)
    }
}

