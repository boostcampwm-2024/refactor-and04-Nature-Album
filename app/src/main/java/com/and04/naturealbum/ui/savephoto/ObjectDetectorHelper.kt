package com.and04.naturealbum.ui.savephoto

import android.content.Context
import android.graphics.Bitmap
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.task.vision.detector.Detection
import org.tensorflow.lite.task.vision.detector.ObjectDetector
import java.io.File

class ObjectDetectorHelper(context: Context, modelPath: String) {
    private val detector: ObjectDetector

    init {
        val modelFile = copyAssetToInternalStorage(context, "EfficientDet.tflite", "EfficientDet.tflite")
        val options = ObjectDetector.ObjectDetectorOptions.builder()
            .setMaxResults(5) // 최대 5개의 결과만 반환
            .setScoreThreshold(0.5f) // 50% 이상의 신뢰도만 반환
            .build()
        detector = ObjectDetector.createFromFileAndOptions(modelFile, options)
    }

    fun detectObjects(bitmap: Bitmap): List<Detection> {
        val tensorImage = TensorImage.fromBitmap(bitmap)
        return detector.detect(tensorImage)
    }
}

fun copyAssetToInternalStorage(context: Context, assetFileName: String, outputFileName: String): File {
    val outputFile = File(context.filesDir, outputFileName)
    if (!outputFile.exists()) {
        context.assets.open(assetFileName).use { inputStream ->
            outputFile.outputStream().use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
    }
    return outputFile
}