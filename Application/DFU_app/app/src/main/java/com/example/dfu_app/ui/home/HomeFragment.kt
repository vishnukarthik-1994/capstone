package com.example.dfu_app.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.dfu_app.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bind()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun bind( ) {
        binding.apply {
            dailySurveyButton.setOnClickListener { toSurvey() }
            diagnosisRecordButton.setOnClickListener { toRecord() }
        }
    }
    private fun toRecord(){
        val action = HomeFragmentDirections.actionNavHomeToNavAnalysisRecord()
        this.findNavController().navigate(action)
    }
    private fun toSurvey(){
        val action = HomeFragmentDirections.actionNavHomeToNavDailySurvey()
        this.findNavController().navigate(action)
    }
}