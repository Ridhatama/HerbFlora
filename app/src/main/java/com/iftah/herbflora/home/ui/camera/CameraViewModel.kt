@file:Suppress("DEPRECATION")

package com.iftah.herbflora.home.ui.camera

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class CameraViewModel(application: Application) : AndroidViewModel(application) {

    private val _predictedLabel = MutableLiveData<String>()
    val predictedLabel: LiveData<String> get() = _predictedLabel

    private val _predictedPercentage = MutableLiveData<String>()
    val predictedPercentage: LiveData<String> get() = _predictedPercentage

    private var tflite: Interpreter
    private var inputBuffer: ByteBuffer
    private var outputBuffer: ByteBuffer

    private val IMAGE_WIDTH = 224
    private val IMAGE_HEIGHT = 224
    private val IMAGE_CHANNELS = 3
    private val IMAGE_SIZE = IMAGE_WIDTH * IMAGE_HEIGHT * IMAGE_CHANNELS
    private val MODEL_FILE_NAME = "model.tflite"
    private val LABELS_FILE_NAME = "labels.txt"

    init {
        // Load the model
        tflite = Interpreter(loadModelFile())

        // Allocate buffers for input and output
        inputBuffer = ByteBuffer.allocateDirect(IMAGE_SIZE * 4).apply {
            order(ByteOrder.nativeOrder())
        }
        outputBuffer = ByteBuffer.allocateDirect(4 * 1 * 1000).apply {
            order(ByteOrder.nativeOrder())
        }
    }

    private fun loadModelFile(): MappedByteBuffer {
        val fileDescriptor = getApplication<Application>().assets.openFd(MODEL_FILE_NAME)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    fun predictImage(bitmap: Bitmap?) {
        val resizedBitmap =
            bitmap?.let { Bitmap.createScaledBitmap(it, IMAGE_WIDTH, IMAGE_HEIGHT, true) }

        // Normalize the pixels
        val pixels = IntArray(IMAGE_WIDTH * IMAGE_HEIGHT)
        resizedBitmap?.getPixels(
            pixels,
            0,
            resizedBitmap.width,
            0,
            0,
            resizedBitmap.width,
            resizedBitmap.height
        )
        inputBuffer.rewind()
        for (pixel in pixels) {
            inputBuffer.putFloat(((pixel shr 16 and 0xFF) - 127.5f) / 127.5f)
            inputBuffer.putFloat(((pixel shr 8 and 0xFF) - 127.5f) / 127.5f)
            inputBuffer.putFloat(((pixel and 0xFF) - 127.5f) / 127.5f)
        }

        // Run inference
        tflite.run(inputBuffer, outputBuffer)

        // Post-processing: get the label with highest probability
        outputBuffer.rewind()
        val probabilities = FloatArray(1000)
        outputBuffer.asFloatBuffer().get(probabilities)
        val highestProbability = probabilities.maxOrNull()
        val indexOfMax = probabilities.indexOfFirst { it == highestProbability }

        // Display the result
        val labels = loadLabels()
        val predictedLabel = labels[indexOfMax]
        val confidencePercentage = highestProbability?.times(100) ?: 0f

        predictResult(predictedLabel, confidencePercentage.toString())

    }

    private fun predictResult(predictLabel: String, confidencePercentage: String){
        _predictedLabel.value = predictLabel
        _predictedPercentage.value = confidencePercentage
    }

    private fun loadLabels(): List<String> {
        val labels = mutableListOf<String>()
        getApplication<Application>().assets.open(LABELS_FILE_NAME).bufferedReader()
            .useLines { lines ->
                lines.forEach { labels.add(it) }
            }
        return labels
    }


}
