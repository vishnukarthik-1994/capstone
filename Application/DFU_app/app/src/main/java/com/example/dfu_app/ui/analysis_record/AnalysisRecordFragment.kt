package com.example.dfu_app.ui.analysis_record

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.dfu_app.data.DiagnosisPhoto
import com.example.dfu_app.data.RecommendationSource
import com.example.dfu_app.databinding.FragmentAnalysisRecordBinding
import com.example.dfu_app.ui.home.HomeFragmentDirections


class AnalysisRecordFragment: Fragment() {
    private lateinit var viewModel: AnalysisRecordViewModel
    private lateinit var adapter: AnalysisRecordAdapter
    private var _binding: FragmentAnalysisRecordBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Set callback
        val callback = object : OnBackPressedCallback(true /** true means that the callback is enabled */) {
            override fun handleOnBackPressed() {
                val action = AnalysisRecordFragmentDirections.actionNavAnalysisRecordToNavHome()
                requireView().findNavController().navigate(action)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this,callback)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val myDataset = RecommendationSource().loadRecommendations()
        _binding = FragmentAnalysisRecordBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[AnalysisRecordViewModel::class.java]
        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        viewModel.update()
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        adapter = AnalysisRecordAdapter()
        binding.verticalRecyclerView.adapter = AnalysisRecordAdapter()
        return binding.root
    }
}