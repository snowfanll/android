package com.example.tflitebigwork

import android.content.Context
import android.os.SystemClock
import org.tensorflow.lite.support.label.Category
import org.tensorflow.lite.task.core.BaseOptions
import org.tensorflow.lite.task.text.nlclassifier.BertNLClassifier
import java.util.concurrent.ScheduledThreadPoolExecutor

class TextAnalyzer(
    val context: Context,
    val listener: TextResultsListener,
) {
    private lateinit var bertClassifier: BertNLClassifier

    private lateinit var executor: ScheduledThreadPoolExecutor

    init {
        initClassifier()
    }

    fun initClassifier() {
        val baseOptionsBuilder = BaseOptions.builder()

        val baseOptions = baseOptionsBuilder.build()


        val options = BertNLClassifier.BertNLClassifierOptions
            .builder()
            .setBaseOptions(baseOptions)
            .build()

        bertClassifier = BertNLClassifier.createFromFileAndOptions(
            context,
            MOBILEBERT,
            options)
    }

    fun classify(text: String) {
        executor = ScheduledThreadPoolExecutor(1)     //功能线程池，

        executor.execute {
            val results: List<Category>
            // inferenceTime is the amount of time, in milliseconds, that it takes to
            // classify the input text.
            var inferenceTime  = SystemClock.uptimeMillis()

            results = bertClassifier.classify(text)

            inferenceTime = SystemClock.uptimeMillis() - inferenceTime

            listener.onResult(results, inferenceTime)
        }
    }

    interface TextResultsListener {
        fun onError(error: String)
        fun onResult(results: List<Category>, inferenceTime: Long)
    }

    companion object {    //全局变量，设置模型
        const val MOBILEBERT = "model.tflite"
    }
}