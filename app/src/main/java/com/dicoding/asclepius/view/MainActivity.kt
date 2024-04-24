package com.dicoding.asclepius.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.dicoding.asclepius.R
import com.dicoding.asclepius.databinding.ActivityMainBinding
import com.dicoding.asclepius.helper.ViewModelFactory
import com.dicoding.asclepius.model.HistoryViewModel

class MainActivity : AppCompatActivity() {

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }

    private lateinit var binding: ActivityMainBinding

    private var currentImageUri: Uri? = null

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ){ isGranted: Boolean ->
        if (isGranted){
            showToast("Permission request granted")
        } else {
            showToast("Permission request denied")
        }
    }

    private fun allPermissionsGranted() = ContextCompat.checkSelfPermission(
        this,
        REQUIRED_PERMISSION
    ) == PackageManager.PERMISSION_GRANTED

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!allPermissionsGranted()){
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }

        binding.galleryButton.setOnClickListener { startGallery() }
        binding.analyzeButton.setOnClickListener {
            analyzeImage()
        }

        binding.cropButton.setOnClickListener {
            cropImage()
        }

        binding.newsButton.setOnClickListener {
            val intent = Intent(this@MainActivity, NewsActivity::class.java)
            startActivity(intent)
        }

        binding.toolBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId){
                R.id.history -> {
                    val intent = Intent(this@MainActivity, HistoryActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }

    private fun cropImage() {
        currentImageUri?.let {
            val intent = Intent(this@MainActivity, CropActivity::class.java)
            intent.putExtra(CropActivity.EXTRA_IMAGE_URI, it.toString())
            resultCropLauncher.launch(intent)
        }
    }

    private val resultCropLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){ result ->
        if (result.resultCode == CropActivity.RESULT_CODE && result.data != null){
            val imageResultBitmap = result.data?.getParcelableExtra<Uri>(CropActivity.EXTRA_IMAGE_BITMAP)
            binding.previewImageView.setImageURI(imageResultBitmap)
            currentImageUri = imageResultBitmap
            Log.d("Uri result", imageResultBitmap.toString())
        }
        Log.d("result code", result.resultCode.toString())
    }

    private fun startGallery() {
        // TODO: Mendapatkan gambar dari Gallery.
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }
    val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null){
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private fun showImage() {
        // TODO: Menampilkan gambar sesuai Gallery yang dipilih.
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.previewImageView.setImageURI(it)
        }
    }

    private fun analyzeImage() {
        // TODO: Menganalisa gambar yang berhasil ditampilkan.
        currentImageUri?.let {
            val intent = Intent(this@MainActivity, ResultActivity::class.java)
            intent.putExtra(ResultActivity.EXTRA_IMAGE_URI, it.toString())
            startActivity(intent)
        }
    }

//    private fun moveToResult() {
//        val intent = Intent(this, ResultActivity::class.java)
//        startActivity(intent)
//    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}