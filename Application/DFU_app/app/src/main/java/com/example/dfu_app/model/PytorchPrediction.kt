package com.example.dfu_app.model

import android.content.res.AssetManager
import android.graphics.Bitmap
import com.example.dfu_app.imageprocessor.ImagePreprocessing.NO_MEAN_RGB
import com.example.dfu_app.imageprocessor.ImagePreprocessing.NO_STD_RGB
import com.example.dfu_app.imageprocessor.ImagePreprocessing.drawBoundingBox
import com.example.dfu_app.imageprocessor.ImagePreprocessing.outputsToNMPrediction
import com.facebook.soloader.nativeloader.NativeLoader
import com.facebook.soloader.nativeloader.SystemDelegate
import org.pytorch.*
import org.pytorch.torchvision.TensorImageUtils

/**
 * for Yolo v5 model
 */
class PytorchPrediction(assetManager: AssetManager) {
    init{
        if (!NativeLoader.isInitialized()) {
            NativeLoader.init( SystemDelegate())
        }
        NativeLoader.loadLibrary("pytorch_jni")
        NativeLoader.loadLibrary("torchvision_ops")
    }
    private val modelName = "best_class4.torchscript"
    private var mModule:Module = PyTorchAndroid.loadModuleFromAsset(assetManager,modelName)
    fun modelPredict(img:Bitmap):Pair<Bitmap,Array<Int>> {
        val floatBuffer = Tensor.allocateFloatBuffer(3 * img.width * img.height)
        //copy data to tensor  buffer
        TensorImageUtils.bitmapToFloatBuffer(img,0,0,img.width,img.height,
            NO_MEAN_RGB,NO_STD_RGB,floatBuffer,0)
        // define tensor instance
        val inputTensor = Tensor.fromBlob(floatBuffer, longArrayOf(1,3, img.height.toLong(), img.width.toLong())) // [1,3,416,416]
        // Get prediction from model
        val outputTuple = mModule.forward(IValue.from(inputTensor)).toTuple()
        // Decode model output
        val outputs = outputTuple[0].toTensor().dataAsFloatArray
        var ulcerCount = IntArray(4)
        val prediction =  outputsToNMPrediction(outputs)
        for (i in prediction.indices) {
            when (prediction[i].classIndex) {
                0 -> ulcerCount[0] += 1 //both
                1 -> ulcerCount[1] += 1 // infection
                2 -> ulcerCount[2] += 1 // ischemia
                else -> ulcerCount[3] += 1 // none
            }
        }
        return drawBoundingBox(img, prediction) to ulcerCount.toTypedArray()
    }
}

/**
 * for d2go model
 */
/**
class PytorchPrediction(assetManager: AssetManager) {
    init{
        if (!NativeLoader.isInitialized()) {
            NativeLoader.init( SystemDelegate())
        }
        NativeLoader.loadLibrary("pytorch_jni")
        NativeLoader.loadLibrary("torchvision_ops")
    }
    private val modelName = "dfumodel.pt"
    private var mModule:Module = PyTorchAndroid.loadModuleFromAsset(assetManager,modelName)
    fun modelPredict(img:Bitmap):Pair<Bitmap,Array<Int>> {
        var predictImg = img
        val floatBuffer = Tensor.allocateFloatBuffer(3 * img.width * img.height)
        //copy data to tensor  buffer
        TensorImageUtils.bitmapToFloatBuffer(img,0,0,img.width,img.height,
                                            NO_MEAN_RGB,NO_STD_RGB,floatBuffer,0)
        // define tensor instance
        val inputTensor = Tensor.fromBlob(floatBuffer, longArrayOf(3, img.height.toLong(), img.width.toLong()))
        // Get prediction from model
        val outputTuple = mModule.forward(IValue.listFrom(inputTensor)).toTuple()
        // Decode model output
        val map = outputTuple[1].toList()[0].toDictStringKey()
        var count = 0
        var ulcerCount = IntArray(4)
        if (map.containsKey("boxes")){
            val boxesData = map["boxes"]!!.toTensor().dataAsFloatArray
            val scoresData = map["scores"]!!.toTensor().dataAsFloatArray
            val labelsData = map["labels"]!!.toTensor().dataAsLongArray
            val outputs =FloatArray(scoresData.size*OUTPUT_COLUMN)
            for ( i in scoresData.indices)
            {
                // determining IOU > 0.5
                if (scoresData[i] < 0.5)
                    continue
                outputs[OUTPUT_COLUMN * count + 0] = boxesData[4 * i + 0]
                outputs[OUTPUT_COLUMN * count + 1] = boxesData[4 * i + 1]
                outputs[OUTPUT_COLUMN * count + 2] = boxesData[4 * i + 2]
                outputs[OUTPUT_COLUMN * count + 3] = boxesData[4 * i + 3]
                outputs[OUTPUT_COLUMN * count + 4] = scoresData[i]
                outputs[OUTPUT_COLUMN * count + 5] = labelsData[i].toFloat() - 1
                count++
                ulcerCount[(labelsData[i].toFloat() - 1).toInt()] += 1
            }
            val prediction = outputsToPredictions(count, outputs)
            predictImg  = drawBoundingBox(img, prediction)
        }
        return predictImg to ulcerCount.toTypedArray()
    }
}
*/