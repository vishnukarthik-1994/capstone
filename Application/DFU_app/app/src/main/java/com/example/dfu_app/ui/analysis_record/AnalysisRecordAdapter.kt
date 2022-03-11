package com.example.dfu_app.ui.analysis_record

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.example.dfu_app.R
import com.example.dfu_app.data.RecommendationSource
import com.example.dfu_app.model.Recommendation

class AnalysisRecordAdapter(
    private val context: AnalysisRecordFragment,
    //private val dataset: List<Recommendation>,
    private val imgs: LiveData<List<Bitmap>>
)
    :RecyclerView.Adapter<AnalysisRecordAdapter.AnalysisRecordViewHolder>()  {
    class AnalysisRecordViewHolder(view: View?): RecyclerView.ViewHolder(view!!) {
        val textView: TextView = view!!.findViewById(R.id.recommendation_text)
        val imageView: ImageView = view!!.findViewById(R.id.record_image)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnalysisRecordViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.analysis_record_list, parent, false)
        return AnalysisRecordViewHolder(adapterLayout)
    }

    override fun getItemCount(): Int {
        return imgs.value!!.size
    }

    override fun onBindViewHolder(holder: AnalysisRecordViewHolder, position: Int) {
        //val recommendationInfo = dataset[position]
        val img = imgs.value!![position]
        //holder.textView.text =  context.resources.getString(recommendationInfo.stringResourceId)
        holder.imageView.setImageBitmap(img)
    }
}