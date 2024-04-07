package com.example.delivery.common.facenet

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Log
import com.example.delivery.common.Constants
import org.tensorflow.contrib.android.TensorFlowInferenceInterface

class FaceNet(context: Context) {
    private val INPUT_NAME = "input:0"
    private val OUTPUT_NAME = "embeddings:0"
    private val PHASE_NAME = "phase_train:0"
    val outputNames: Array<String> = arrayOf(OUTPUT_NAME)
    private var inferenceInterface: TensorFlowInferenceInterface? = null
    //输入图片大小
    val INPUT_SIZE = 160


    //保存input值
    var floatValues: FloatArray
    //像素值
    var intValues: IntArray

    init {
        //模型
        inferenceInterface = TensorFlowInferenceInterface(context.assets, Constants.MODEL_NAME)
        floatValues = FloatArray(INPUT_SIZE*INPUT_SIZE*3)
        intValues = IntArray(INPUT_SIZE*INPUT_SIZE)
    }

    private fun normalizeImage(bitmap_: Bitmap) {
        //1.bitmap缩放到160*160
        val scale_width = (INPUT_SIZE.toFloat()) / bitmap_.width
        val scale_height = (INPUT_SIZE.toFloat()) / bitmap_.height

        val matrix: Matrix = Matrix()
        matrix.postScale(scale_width, scale_height)
        val bitmap: Bitmap = Bitmap.createBitmap(bitmap_, 0, 0, bitmap_.width, bitmap_.height, matrix, true)
        //2.将像素映射到[-1, 1]区间
        val imageMean = 127.5f
        val imageStd = 128
        bitmap.getPixels(intValues, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
        for (i in 0 until intValues.size) {
            val v = intValues[i]
            floatValues[i*3+0] = (((v shr 16) and 0xFF) - imageMean) / imageStd
            floatValues[i*3+1] = (((v shr 8) and 0xFF) - imageMean) / imageStd
            floatValues[i*3+2] = ((v and 0xFF) - imageMean) / imageStd
        }
    }

    fun recognizeImage(bitmap: Bitmap): FaceFeatures {
        //1.图片预处理,normalize
        normalizeImage(bitmap)
        //2.feed
        try {
            inferenceInterface?.feed(INPUT_NAME, floatValues, 1, INPUT_SIZE.toLong(), INPUT_SIZE.toLong(), 3)
            val phase = BooleanArray(1)
            inferenceInterface?.feed(PHASE_NAME, phase)
        } catch (e: Exception) {
            Log.e("FaceNet", "[*] feed Error\n$e")
//            return null
        }
        //3.run
        try {
            inferenceInterface?.run(outputNames, false)
        } catch (e: Exception) {
            Log.e("FaceNet", "[*] run Error\n$e")
//            return null
        }
        //4.fetch
        val faceFeatures = FaceFeatures()
        val outputs = faceFeatures.getFea();
        try {
            inferenceInterface?.fetch(OUTPUT_NAME, outputs)
        }catch (e: Exception) {
            Log.e("FaceNet", "[*] fetch Error\n$e")
//            return null
        }
        return faceFeatures
    }
}