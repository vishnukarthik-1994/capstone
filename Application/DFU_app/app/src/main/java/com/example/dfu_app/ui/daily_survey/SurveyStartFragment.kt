package com.example.dfu_app.ui.daily_survey

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.example.dfu_app.databinding.FragmentSurveyStartBinding

class SurveyStartFragment: Fragment() {
    private val viewModel: SurveyViewModel by activityViewModels()
    private var _binding: FragmentSurveyStartBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //remove previous view
        container?.removeAllViews()
        _binding = FragmentSurveyStartBinding.inflate(inflater, container, false)
        viewModel.process = true
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
                    val action = SurveyStartFragmentDirections.actionNavSurveyStartToNavSurveyImageAnalysis()
                    requireView().findNavController().navigate(action)
                }
            }
    }
}




