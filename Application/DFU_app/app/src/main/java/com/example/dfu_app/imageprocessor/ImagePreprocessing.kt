package com.example.dfu_app.imageprocessor

import android.graphics.*
import java.util.*
import kotlin.math.max
import kotlin.math.min


data class Result(val classIndex: Int,val score: Float,val rect:Rect)
object ImagePreprocessing {
    lateinit var absolutePath: String
//    const val INPUT_WIDTH = 640
//    const val INPUT_HEIGHT = 640
    const val INPUT_WIDTH = 416
    const val INPUT_HEIGHT = 416
    private const val mOutputRow = 10647 // as decided by the YOLOv5 model for input image of size 640*640
    private const val mOutputColumn = 6 // left, top, right, bottom, score and 1 class probability
    private const val mThreshold = 0.30f // score above which a detection is generated
    private const val mNmsLimit = 15
    private val classes = listOf("Both","Infection","Ischemia","None")
    val NO_MEAN_RGB = floatArrayOf(0.0f, 0.0f, 0.0f)
    val NO_STD_RGB = floatArrayOf(1.0f, 1.0f, 1.0f)
    fun loadingImg(imgPath: String): Bitmap {
        val image = Bitmap.createScaledBitmap(BitmapFactory.decodeFile (imgPath), INPUT_WIDTH, INPUT_HEIGHT, true)
        return image!!
    }

    /* d2go version
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
     */

    fun outputsToNMPrediction(outputs:FloatArray):List<Result> {
        val predictions = mutableListOf<Result>()

        for(i in 0 until mOutputRow){
            if (outputs[i* mOutputColumn +4] > mThreshold) {
                // find bounding box
                val x = outputs[i * mOutputColumn]
                val y = outputs[i * mOutputColumn + 1]
                val w = outputs[i * mOutputColumn + 2]
                val h = outputs[i * mOutputColumn + 3]
                val left = x - w/2
                val right = x + w/2
                val top = y - h/2
                val bottom = y + h/2
                val rect = Rect(
                    (left).toInt(), (top).toInt(),
                    (right).toInt(), (bottom).toInt()
                )
                var max = outputs[i * mOutputColumn + 5]
                var cls = 0
                for (j in 0 until mOutputColumn-5) {
                    if (outputs[i* mOutputColumn + 5 + j] > max) {
                        max = outputs[i* mOutputColumn + 5 + j]
                        cls = j
                    }
                }
                val result = Result(
                    cls,
                    outputs[i * mOutputColumn + 4],
                    rect
                )
                predictions.add(result)
            }
        }
        return nonMaxSuppression(predictions.toList())
    }
    // The two methods nonMaxSuppression and IOU below are ported from https://github.com/hollance/YOLO-CoreML-MPSNNGraph/blob/master/Common/Helpers.swift
    /**
     * Removes bounding boxes that overlap too much with other boxes that have
     */
    private fun nonMaxSuppression(predictions:List<Result>):List<Result> {
        // Do an argSort on the confidence scores, from high to low.
        Collections.sort(predictions) { o1, o2 -> o1.score.compareTo(o2.score) }
        val selected = mutableListOf<Result>()
        val active = BooleanArray(predictions.size)
        Arrays.fill(active,true)
        var numActive = active.size
        var done = false
        // The algorithm is simple: Start with the box that has the highest score.
        // Remove any remaining boxes that overlap it more than the given threshold
        // amount. If there are any boxes left (i.e. these did not overlap with any
        // previous boxes), then repeat this procedure, until no more boxes remain
        // or the limit has been reached.
        for (i in predictions.indices) {
            if (done) break
            if (active[i]) {
                selected.add(predictions[i])
                if (selected.size >= mNmsLimit) break
                for (j in i+1 until predictions.size) {
                    if (active[j]) {
                        if (IOU(predictions[i].rect,predictions[j].rect) > mThreshold ) {
                            active[j] = false
                            numActive -= 1
                            if (numActive <= 0) {
                                done = true
                                break
                            }
                        }
                    }
                }
            }
        }
        return selected
    }

    // calculate the IOU score
    fun IOU(a: Rect, b: Rect): Float {
        val areaA = ((a.right - a.left) * (a.bottom - a.top)).toFloat()
        if (areaA <= 0.0) return 0.0f
        val areaB = ((b.right - b.left) * (b.bottom - b.top)).toFloat()
        if (areaB <= 0.0) return 0.0f
        val intersectionMinX = max(a.left, b.left).toFloat()
        val intersectionMinY = max(a.top, b.top).toFloat()
        val intersectionMaxX = min(a.right, b.right).toFloat()
        val intersectionMaxY = min(a.bottom, b.bottom).toFloat()
        val intersectionArea = max(intersectionMaxY - intersectionMinY, 0f) *
                max(intersectionMaxX - intersectionMinX, 0f)
        return intersectionArea / (areaA + areaB - intersectionArea)
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