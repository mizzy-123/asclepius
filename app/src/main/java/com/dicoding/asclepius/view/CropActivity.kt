package com.dicoding.asclepius.view

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.dicoding.asclepius.R
import com.dicoding.asclepius.databinding.ActivityCropBinding
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.log

class CropActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_IMAGE_URI = "extra_image_uri"
        const val RESULT_CODE = 110
        const val EXTRA_IMAGE_BITMAP = "extra_image_bitmap"
    }

    private lateinit var binding : ActivityCropBinding
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCropBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bundle: Bundle? = intent.extras
        if (bundle != null){
            imageUri = Uri.parse(bundle.getString(EXTRA_IMAGE_URI))
            binding.cropImageView.setImageUriAsync(imageUri)
        }

        binding.toolBar.setNavigationOnClickListener {
            finish()
        }

        binding.toolBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId){
                R.id.rotate -> {
                    binding.cropImageView.rotateImage(90)
                    Log.i("rotate", "masuk")
                    true
                }
                R.id.save -> {
                    val cropResult = binding.cropImageView.getCroppedImage()
                    binding.cropImageView.setImageBitmap(cropResult)
                    Log.d("crop", "sukses")
                    val resultIntent = Intent()
                    resultIntent.putExtra(EXTRA_IMAGE_BITMAP, bitmapToUri(this, cropResult!!))
                    setResult(RESULT_CODE, resultIntent)
                    finish()
                    true
                }
                else -> {
                    false
                }
            }
        }
    }

    private fun bitmapToUri(context: Context, bitmap: Bitmap): Uri? {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName: String = "JPEG_" + timeStamp + "_"
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "$imageFileName.png")
            put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
        }

        val resolver: ContentResolver = context.contentResolver
        val uri: Uri? = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        uri?.let { imageUri ->
            try {
                val outputStream: OutputStream? = resolver.openOutputStream(imageUri)
                if (outputStream != null) {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                }
                outputStream?.close()
            } catch (e: Exception) {
                resolver.delete(imageUri, null, null)
                return null
            }
        }

        return uri
    }
}