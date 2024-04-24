package com.dicoding.asclepius.view

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.dicoding.asclepius.R
import com.dicoding.asclepius.database.History
import com.dicoding.asclepius.databinding.ActivityResultBinding
import com.dicoding.asclepius.helper.ImageClassifierHelper
import com.dicoding.asclepius.helper.ViewModelFactory
import com.dicoding.asclepius.model.HistoryViewModel
import org.tensorflow.lite.task.vision.classifier.Classifications
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.Date

class ResultActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_IMAGE_URI = "extra_image_uri"
        const val EXTRA_RESULT = "extra_result"
        val history = History()
    }

    private lateinit var binding: ActivityResultBinding
    private lateinit var imageClassifier: ImageClassifierHelper
    private var imageUri: Uri? = null
    private lateinit var historyViewModel: HistoryViewModel

    private var namePredict: String? = null
    private var scorePredict: String? = null
    private var fotoByte: ByteArray? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        historyViewModel = obtainViewModel(this@ResultActivity)

        // TODO: Menampilkan hasil gambar, prediksi, dan confidence score.
        imageUri = Uri.parse(intent.getStringExtra(EXTRA_IMAGE_URI))
        classify()


        binding.toolBar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun obtainViewModel(activity: AppCompatActivity): HistoryViewModel {
        val factory = ViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory).get(HistoryViewModel::class.java)
    }

    private fun classify(){
        imageClassifier = ImageClassifierHelper(
            context = this,
            classifierListener = object : ImageClassifierHelper.ClassifierListener {
                override fun onError(error: String) {
                    runOnUiThread {
                        Toast.makeText(this@ResultActivity, error, Toast.LENGTH_SHORT).show()
                    }
                }

                @SuppressLint("SetTextI18n")
                override fun onResults(results: List<Classifications>?) {
                    runOnUiThread {
                        results?.let {
                            if (it.isNotEmpty() && it[0].categories.isNotEmpty()){
                                println(it)
                                val sortedCategories = it[0].categories.sortedByDescending { it?.score }
                                val displayResult = sortedCategories[0]
                                Log.i("results", sortedCategories.toString())
                                binding.resultImage.setImageURI(imageUri)
                                binding.resultText.text = displayResult.label
                                binding.resultScore.text = displayResult.score.toString()

                                namePredict = displayResult.label
                                fotoByte = imageUri?.let { item -> uriToByteArray(item) }
                                scorePredict = displayResult.score.toString()

                                history.let { f ->
                                    f.name = namePredict
                                    f.score = scorePredict
                                    f.foto = fotoByte
                                    f.createdAt = Date()
                                }
                                historyViewModel.insert(history)
                            }
                        }
                    }
                }

            }
        )

        imageUri?.let {
            binding.resultImage.setImageURI(it)
            imageClassifier.classifyStaticImage(it)
        }
    }

    private fun uriToByteArray(imageUri: Uri): ByteArray? {
        val inputStream = this.contentResolver.openInputStream(imageUri)
        val cursor = this.contentResolver.query(imageUri, null, null, null, null)
        var byteArray: ByteArray? = null
        cursor?.use { c ->
            val nameIndex = c.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (c.moveToFirst()){
                val name = c.getString(nameIndex)
                inputStream?.let { inputStream ->
                    val file = File(this.cacheDir, name)
                    val os = file.outputStream()
                    os.use {
                        inputStream.copyTo(it)
                    }
                    val bitmap = BitmapFactory.decodeFile(file.absolutePath)

                    val byteArrayOutputStream = ByteArrayOutputStream()
                    bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 30, byteArrayOutputStream)
                    val byteArray2 = byteArrayOutputStream.toByteArray()
                    byteArray = byteArray2
                }
            }
        }

        return byteArray
    }


}