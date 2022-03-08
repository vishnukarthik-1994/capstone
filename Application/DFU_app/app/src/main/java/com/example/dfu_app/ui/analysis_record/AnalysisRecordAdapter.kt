package com.example.dfu_app.ui.analysis_record

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dfu_app.R
import com.example.dfu_app.data.RecommendationSource
import com.example.dfu_app.model.Recommendation

class AnalysisRecordAdapter(
    private val context: AnalysisRecordFragment,
    private val dataset: List<Recommendation>)
    :RecyclerView.Adapter<AnalysisRecordAdapter.AnalysisRecordViewHolder>()  {
    class AnalysisRecordViewHolder(view: View?): RecyclerView.ViewHolder(view!!) {
        val textView: TextView = view!!.findViewById(R.id.recommendation_text)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnalysisRecordViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.analysis_record_list, parent, false)
        return AnalysisRecordViewHolder(adapterLayout)
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

    override fun onBindViewHolder(holder: AnalysisRecordViewHolder, position: Int) {
        val recommendationInfo = dataset[position]
        holder.textView.text =  context.resources.getString(recommendationInfo.stringResourceId)
    }
}