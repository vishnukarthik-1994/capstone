package com.example.dfu_app.ui.analysis_record

import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.dfu_app.R
import com.example.dfu_app.data.RecommendationSource
import com.example.dfu_app.databinding.AnalysisRecordListBinding
import com.example.dfu_app.databinding.FragmentAnalysisRecordBinding
import com.example.dfu_app.ui.home.HomeViewModel
import java.io.File


class AnalysisRecordFragment: Fragment() {
    private lateinit var viewModel: AnalysisRecordViewModel
    private var _binding: FragmentAnalysisRecordBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val myDataset = RecommendationSource().loadRecommendations()
        _binding = FragmentAnalysisRecordBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(AnalysisRecordViewModel::class.java)
        val root: View = binding.root
        binding.verticalRecyclerView.adapter = AnalysisRecordAdapter(this,viewModel.photos)
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}