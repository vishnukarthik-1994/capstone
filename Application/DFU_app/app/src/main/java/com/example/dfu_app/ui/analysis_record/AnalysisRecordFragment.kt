package com.example.dfu_app.ui.analysis_record

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.dfu_app.R
import com.example.dfu_app.data.RecommendationSource
import com.example.dfu_app.databinding.AnalysisRecordListBinding
import com.example.dfu_app.databinding.FragmentAnalysisRecordBinding


class AnalysisRecordFragment: Fragment() {
    private lateinit var analysis_record_ViewModel: AnalysisRecordViewModel
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
        analysis_record_ViewModel =
            ViewModelProvider(this).get(AnalysisRecordViewModel::class.java)
        _binding = FragmentAnalysisRecordBinding.inflate(inflater, container, false)
        val root: View = binding.root
        binding.verticalRecyclerView.adapter = AnalysisRecordAdapter(this,myDataset)
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}