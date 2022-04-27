package com.example.dfu_app.ui.register

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.dfu_app.LoginActivity
import com.example.dfu_app.R
import com.example.dfu_app.databinding.FragmentRegisterUsernameBinding
import com.example.dfu_app.ui.error_message.ErrorMessage.setErrorMessage
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class RegisterNameFragment: Fragment() {

    private val viewModel: RegisterViewModel by activityViewModels {
        RegisterViewModelFactory()
    }
    private var _binding: FragmentRegisterUsernameBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //remove previous view
        container?.removeAllViews()
        _binding = FragmentRegisterUsernameBinding.inflate(inflater, container, false)
        val user = Firebase.auth.currentUser!!.email
        viewModel.getUser(user!!)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
        //check firstname
        if ( !viewModel.checkInput(binding.firstNameEditText.text.toString())){
            binding.firstName.isErrorEnabled = true
            binding.firstName.error = getString(R.string.emptyInput)
            setErrorMessage(requireContext(),getString(R.string.emptyInput))
            return
        }
        else
        {
            binding.firstName.isErrorEnabled = false
        }
        //check lastname
        if ( !viewModel.checkInput(binding.lastNameEditText.text.toString())){
            binding.lastName.isErrorEnabled = true
            binding.lastName.error = getString(R.string.emptyInput)
            setErrorMessage(requireContext(),getString(R.string.emptyInput))
            return
        }
        else
        {
            binding.lastName.isErrorEnabled = false
        }
        viewModel.setName(
             binding.firstNameEditText.text.toString()
            ,binding.lastNameEditText.text.toString())
        closeKeyBoards()
        val action = RegisterNameFragmentDirections.actionNavRegisterUsernameToNavRegisterUserinfo()
        this.findNavController().navigate(action)
    }
    private fun back(){
        closeKeyBoards()
//        val action = RegisterNameFragmentDirections.actionNavRegisterUsernameToNavLogin2()
//        this.findNavController().navigate(action)
        val intent = Intent().setClass(requireActivity(), LoginActivity::class.java)
        intent.putExtra("signOut",true)
        startActivity(intent)
    }
    private fun closeKeyBoards(){
        val manager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (requireActivity().currentFocus != null){
            manager.hideSoftInputFromWindow(
                requireActivity().currentFocus!!.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
    }
}