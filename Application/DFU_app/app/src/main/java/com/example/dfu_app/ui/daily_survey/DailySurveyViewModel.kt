package com.example.dfu_app.ui.daily_survey

import android.content.res.AssetManager
import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dfu_app.model.PytorchPrediction
import kotlinx.coroutines.launch
import java.io.FileNotFoundException
import java.io.FileOutputStream

class DailySurveyViewModel: ViewModel() {

    private lateinit var model:PytorchPrediction
    fun loadingModel(assetManager: AssetManager){
        try {
            model = PytorchPrediction(assetManager)
        }
        catch(e:FileNotFoundException){
            e.printStackTrace()
        }
    }
    fun prediction(source:Bitmap,path:String){
        viewModelScope.launch {
            try{
                val predictBitmap = model.modelPredict(source)
                predictBitmap.compress(Bitmap.CompressFormat.JPEG, 100, FileOutputStream(path))
            }
            catch(e: Exception){
                e.printStackTrace()
            }
        }
    }
}