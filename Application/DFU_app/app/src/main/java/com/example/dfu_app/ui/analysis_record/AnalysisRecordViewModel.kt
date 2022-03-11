package com.example.dfu_app.ui.analysis_record


import android.graphics.Bitmap
import android.os.Parcelable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dfu_app.imageprocessor.ImagePreprocessing
import kotlinx.coroutines.launch
import java.io.File


class AnalysisRecordViewModel(): ViewModel(){
    // Internally, we use a MutableLiveData, because we will be updating the List of MarsPhoto
    // with new values
    private val _photos = MutableLiveData<List<Bitmap>>()

    // The external LiveData interface to the property is immutable, so only this class can modify
    val photos: LiveData<List<Bitmap>> = _photos
    private var absolutePath:String
    init {
        absolutePath = ImagePreprocessing.absolutePath
        getRecords()
    }
    private fun getRecords(){
//        viewModelScope.launch {
//            try {
//                _photos.value = getPhotos()
//            } catch (e: Exception) {
//                _photos.value = listOf()
//            }
//        }
        _photos.value = getPhotos()
    }
    private fun getPhotos():List<Bitmap>{
        val dir  = File(absolutePath)
        val files = dir.listFiles()!!
        val imgList = mutableListOf<Bitmap>()
        for (i in files.indices){
            if(checkImg(files[i].absolutePath)){
                imgList.add(ImagePreprocessing.loadingImg(files[i].absolutePath))
            }
        }
        return imgList.toList()
    }
    private fun checkImg(imgPath:String):Boolean{
        val extension = imgPath.substring(imgPath.lastIndexOf(".")+1,imgPath.length).lowercase()
        return extension == "jpg" || extension == "png"
    }
}