package com.example.dfu_app.ui.daily_survey

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.dfu_app.databinding.FragmentSurveyStartBinding

class SurveyStartFragment: Fragment() {
    private lateinit var viewModel: DailySurveyViewModel
    private var _binding: FragmentSurveyStartBinding? = null
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
        ViewModelProvider(this)[DailySurveyViewModel::class.java].also { viewModel = it }
        _binding = FragmentSurveyStartBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bind()
    }
    @SuppressLint("ClickableViewAccessibility")
    private fun bind( ) {
        binding.apply {
                btnStart.setOnClickListener {
                    val action = SurveyStartFragmentDirections.actionNavDailySurveyStartToNavDailySurvey()
                    requireView().findNavController().navigate(action)
                }
            }
    }
}




