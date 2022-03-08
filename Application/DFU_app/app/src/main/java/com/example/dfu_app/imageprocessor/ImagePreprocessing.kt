package com.example.dfu_app.imageprocessor

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect

data class Result(val classIndex: Int,val score: Float,val rect:Rect){}
object ImagePreprocessing {
    private const val INPUT_WIDTH = 640;
    private const val INPUT_HEIGHT = 640;
    private const val OUTPUT_COLUMN = 6
    fun loadingImg(path: String): Bitmap {
        val image = Bitmap.createScaledBitmap(BitmapFactory.decodeFile (path), INPUT_WIDTH, INPUT_HEIGHT, true);
        return image!!
    }
    fun outputToPredictions(countResult:Int,outputs:Array<Float>, imgScaleX:Float ,
                             imgScaleY:Float, ivScaleX:Float,  ivScaleY:Float,
                             startX:Float,  startY:Float ):MutableList<Result> {
        val predictions = mutableListOf<Result>()
        for(i in 0 until countResult){
            val left = outputs[i*OUTPUT_COLUMN]*imgScaleX
            val top = outputs[i* OUTPUT_COLUMN +1]*imgScaleY
            val right = outputs[i*OUTPUT_COLUMN+2]*imgScaleX
            val bottom = outputs[i* OUTPUT_COLUMN +3]*imgScaleY
            val rect =  Rect((startX+ivScaleX*left).toInt(), (startY+top*ivScaleY).toInt(),
                                (startX+ivScaleX*right).toInt(), (startY+ivScaleY*bottom).toInt())
            val result = Result(outputs[i* OUTPUT_COLUMN +5].toInt(),outputs[i* OUTPUT_COLUMN +4],rect)
            predictions.add(result)
        }
        return predictions
    }
}