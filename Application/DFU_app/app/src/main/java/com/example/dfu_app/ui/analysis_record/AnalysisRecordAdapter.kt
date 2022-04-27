package com.example.dfu_app.ui.analysis_record

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.dfu_app.data.DiagnosisPhoto
import com.example.dfu_app.databinding.AnalysisRecordListBinding

class AnalysisRecordAdapter:
    ListAdapter<DiagnosisPhoto, AnalysisRecordAdapter.RecordsViewHolder>(DiffCallback) {

    class RecordsViewHolder(private var binding: AnalysisRecordListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(diagnosisPhoto: DiagnosisPhoto) {
            // This is important, because it forces the data binding to execute immediately,
            // which allows the RecyclerView to make the correct view size measurements
            binding.photo = diagnosisPhoto
            binding.executePendingBindings()
        }
    }

    /**
     * Allows the RecyclerView to determine which items have changed when the [List] of
     * [DiagnosisPhoto] has been updated.
     */

    companion object DiffCallback : DiffUtil.ItemCallback<DiagnosisPhoto>() {
        override fun areItemsTheSame(oldItem: DiagnosisPhoto, newItem: DiagnosisPhoto): Boolean {
            return oldItem.id == newItem.id
        }
        override fun areContentsTheSame(oldItem: DiagnosisPhoto, newItem: DiagnosisPhoto): Boolean {
            return oldItem.uri == newItem.uri
        }
    }

    /**
     * Create new [RecyclerView] item views (invoked by the layout manager)
     */
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecordsViewHolder {
        return RecordsViewHolder(
            AnalysisRecordListBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        )
    }

    /**
     * Replaces the contents of a view (invoked by the layout manager)
     */
    override fun onBindViewHolder(holder: RecordsViewHolder, position: Int) {
        val diagnosisPhoto = getItem(position)
        holder.bind(diagnosisPhoto)
    }
}
