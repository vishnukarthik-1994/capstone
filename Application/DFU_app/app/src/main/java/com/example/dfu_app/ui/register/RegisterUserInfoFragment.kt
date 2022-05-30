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
import com.example.dfu_app.LoginActivity
import com.example.dfu_app.MainActivity
import com.example.dfu_app.R
import com.example.dfu_app.databinding.FragmentRegisterUserinfoBinding
import com.example.dfu_app.ui.error_message.ErrorMessage.setErrorMessage

class RegisterUserInfoFragment: Fragment() {

    private val viewModel: RegisterViewModel by activityViewModels {
        RegisterViewModelFactory()
    }
    private var _binding: FragmentRegisterUserinfoBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterUserinfoBinding.inflate(inflater, container, false)
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
            submitButton.setOnClickListener { next()}
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
        //check height
        if ( !viewModel.checkInput(binding.heightFeetEditText.text.toString())){
            binding.heightFeet.isErrorEnabled = true
            binding.heightFeet.error = getString(R.string.emptyInput)
            setErrorMessage(requireContext(),getString(R.string.emptyInput))
            return
        }
        else
        {
            binding.heightFeet.isErrorEnabled = false
        }
        if ( !viewModel.checkInput(binding.heightInchEditText.text.toString())){
            binding.heightInch.isErrorEnabled = true
            binding.heightInch.error = getString(R.string.emptyInput)
            setErrorMessage(requireContext(),getString(R.string.emptyInput))
            return
        }
        else
        {
            binding.heightInch.isErrorEnabled = false
        }
        //check weight
        if ( !viewModel.checkInput(binding.weightEditText.text.toString())){
            binding.weight.isErrorEnabled = true
            binding.weight.error = getString(R.string.emptyInput)
            setErrorMessage(requireContext(),getString(R.string.emptyInput))
            return
        }
        else
        {
            binding.weight.isErrorEnabled = false
        }
        //check age
        if ( !viewModel.checkInput(binding.ageEditText.text.toString())){
            binding.age.isErrorEnabled = true
            binding.age.error = getString(R.string.emptyInput)
            setErrorMessage(requireContext(),getString(R.string.emptyInput))
            return
        }
        else
        {
            binding.age.isErrorEnabled = false
        }
        viewModel.setInfo(binding.heightFeetEditText.text.toString()
                        ,binding.heightInchEditText.text.toString()
                        ,binding.weightEditText.text.toString()
                        ,binding.ageEditText.text.toString())
        viewModel.uploadUserToDp()
        closeKeyBoards()
        val intent = Intent().setClass(requireActivity(), MainActivity::class.java)
        intent.putExtra("back",true)
        startActivity(intent)
    }
    private fun back(){
        closeKeyBoards()
        val intent = Intent().setClass(requireActivity(), LoginActivity::class.java)
        intent.putExtra("signOut",true)
        startActivity(intent)
    }
    private fun closeKeyBoards(){
        val manager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (manager.isActive){
            manager.hideSoftInputFromWindow(
                requireActivity().currentFocus!!.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
    }
}
