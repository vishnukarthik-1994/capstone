package com.example.dfu_app.ui.analysis_record

import android.net.Uri
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.dfu_app.R
import com.example.dfu_app.data.DiagnosisPhoto


/**
 * Updates the data shown in the [RecyclerView].
 */

@BindingAdapter("listData")
fun bindRecyclerView(recyclerView: RecyclerView, data: List<DiagnosisPhoto>?) {
    val adapter = recyclerView.adapter as AnalysisRecordAdapter
    if (data!!.isNotEmpty()) {
        adapter.submitList(data)
    }
}

/**
 *  Load an image by URL into an [ImageView]
 */

@BindingAdapter("imageUri")
fun bindImage(imgView: ImageView, imgUrl: Uri) {

    imgView.load(imgUrl){
        placeholder(R.drawable.loading_animation)
    }
}

@BindingAdapter("recordStatus")
fun bindStatus(statusImageView: ImageView, status: Boolean) {

    if (status) {
        statusImageView.visibility = View.GONE
    }
    else {
        statusImageView.visibility = View.VISIBLE
        statusImageView.setImageResource(R.drawable.ic_broken_image)
    }

}
@BindingAdapter("textStatus")
fun bindTextStatus(noRecord:TextView, status: Boolean) {

    if (status) {
        noRecord.visibility = View.INVISIBLE
    }
    else {
        noRecord.visibility = View.VISIBLE
    }

}

