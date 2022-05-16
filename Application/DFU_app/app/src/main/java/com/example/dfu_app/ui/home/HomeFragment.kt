package com.example.dfu_app.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.dfu_app.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private var newUser:Boolean = false
    override fun onStart() {
        super.onStart()
        // check first login
        newUser = requireActivity().intent.getBooleanExtra("back",false)
        if (newUser) {
            val action = HomeFragmentDirections.actionNavHomeToNavSurveyStart()
            requireView().findNavController().navigate(action)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val callback = object : OnBackPressedCallback(true /** true means that the callback is enabled */) {
            override fun handleOnBackPressed() {
                val action = HomeFragmentDirections.actionNavHomeSelf()
                requireView().findNavController().navigate(action)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this,callback)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //remove previous view
        container?.removeAllViews()
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        bind()
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun bind( ) {
        binding.apply {
            recordButton.setOnClickListener {
                val action = HomeFragmentDirections.actionNavHomeToNavAnalysisRecord()
                requireView().findNavController().navigate(action)
            }
            dailSurveyButton.setOnClickListener {
                val action = HomeFragmentDirections.actionNavHomeToNavSurveyStart()
                requireView().findNavController().navigate(action)
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}



