package com.example.dfu_app.imageprocessor

import android.graphics.*


data class Result(val classIndex: Int,val score: Float,val rect:Rect){}
object ImagePreprocessing {
    lateinit var absolutePath: String
    const val INPUT_WIDTH = 640
    const val INPUT_HEIGHT = 640
    private const val TEXT_WIDTH = 260
    private const val TEXT_HEIGHT = 10
    const val OUTPUT_COLUMN = 6
    private val classes = listOf("BOth","Infection","Ischamia","None")
    val NO_MEAN_RGB = floatArrayOf(0.0f, 0.0f, 0.0f)
    val NO_STD_RGB = floatArrayOf(1.0f, 1.0f, 1.0f)
    fun loadingImg(path: String): Bitmap {
        val image = Bitmap.createScaledBitmap(BitmapFactory.decodeFile (path), INPUT_WIDTH, INPUT_HEIGHT, true)
        return image!!
    }
    fun outputsToPredictions(countResult:Int,outputs:FloatArray ):List<Result> {
        val predictions = mutableListOf<Result>()
        for(i in 0 until countResult){
            val left = outputs[i*OUTPUT_COLUMN]
            val top = outputs[i* OUTPUT_COLUMN +1]
            val right = outputs[i*OUTPUT_COLUMN+2]
            val bottom = outputs[i* OUTPUT_COLUMN +3]
            val rect =  Rect((left).toInt(), (top).toInt(),
                                (right).toInt(), (bottom).toInt())
            val result = Result(outputs[i* OUTPUT_COLUMN +5].toInt(),outputs[i* OUTPUT_COLUMN +4],rect)
            predictions.add(result)
        }
        return predictions.toList()
    }
    fun drawBoundingBox(bitmap:Bitmap,res:List<Result>):Bitmap{
        val canvas = Canvas(bitmap)
        val mPaintRectangle = Paint()
        val mPaintText = Paint()
        for ((classIndex, score, rect) in res) {
            mPaintRectangle.strokeWidth = 5f
            mPaintRectangle.style = Paint.Style.STROKE
            canvas.drawRect(rect, mPaintRectangle)
            val mPath = Path()
            val mRectF = RectF(
                rect.left.toFloat(),
                rect.top.toFloat(),
                rect.left+(rect.right-rect.left)/2.toFloat(),
                rect.top+(rect.bottom-rect.top)/5.toFloat()
            )
            mPath.addRect(mRectF, Path.Direction.CW)
            mPaintText.color = Color.MAGENTA
            canvas.drawPath(mPath, mPaintText)
            mPaintText.color = Color.WHITE
            mPaintText.strokeWidth = 0f
            mPaintText.style = Paint.Style.FILL
            mPaintText.textSize = 32f
            canvas.drawText(
                java.lang.String.format(
                    "%s %.2f", classes[classIndex],
                    score
                ),
                rect.left.toFloat(),
                rect.top+(rect.bottom-rect.top)/5.toFloat(),
                mPaintText
            )
        }
        return bitmap
    }
}