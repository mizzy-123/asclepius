package com.dicoding.asclepius.helper

import android.content.Context
import android.graphics.Bitmap
import android.media.ExifInterface
import android.media.Image
import android.net.Uri
import android.os.SystemClock
import android.provider.MediaStore
import androidx.camera.core.ImageProxy
import com.dicoding.asclepius.R
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Tensor
import org.tensorflow.lite.support.common.ops.CastOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.task.core.BaseOptions
import org.tensorflow.lite.task.core.vision.ImageProcessingOptions
import org.tensorflow.lite.task.vision.classifier.Classifications
import org.tensorflow.lite.task.vision.classifier.ImageClassifier


class ImageClassifierHelper(
    var threshold: Float = 0.1f,
    var maxResult: Int = 3,
    val modelName: String = "cancer_classification.tflite",
    val context: Context,
    val classifierListener: ClassifierListener?
    ) {
    private var imageClassifier: ImageClassifier? = null

    init {
        setupImageClassifier()
    }

    private fun setupImageClassifier() {
        // TODO: Menyiapkan Image Classifier untuk memproses gambar.
        val optionsBuilder = ImageClassifier.ImageClassifierOptions.builder()
            .setScoreThreshold(threshold)
            .setMaxResults(maxResult)
        val baseOptionsBuilder = BaseOptions.builder()
            .setNumThreads(4)
        optionsBuilder.setBaseOptions(baseOptionsBuilder.build())

        try {
            imageClassifier = ImageClassifier.createFromFileAndOptions(
                context,
                modelName,
                optionsBuilder.build()
            )
        } catch (e: IllegalStateException){
            classifierListener?.onError(context.getString(R.string.image_classifier_failed))
        }

    }

    fun classifyStaticImage(imageUri: Uri) {
        // TODO: mengklasifikasikan imageUri dari gambar statis.
        val bitmap = getBitmapFromUri(imageUri)
        if (imageClassifier == null){
            setupImageClassifier()
        }

        val imageProcessor = ImageProcessor.Builder()
            .add(ResizeOp(224, 224, ResizeOp.ResizeMethod.NEAREST_NEIGHBOR))
            .add(CastOp(DataType.UINT8))
            .build()


//        val tensorImage = TensorImage(DataType.UINT8)
//        tensorImage.load(bitmap)
        val tensorImage = imageProcessor.process(TensorImage.fromBitmap(bitmap))

        val orientation = getImageOrientation(context, imageUri)

        val imageProcessingOption = ImageProcessingOptions.builder()
            .setOrientation(orientation)
            .build()

        val results = imageClassifier?.classify(tensorImage, imageProcessingOption)
        classifierListener?.onResults(results)
    }

    private fun getImageOrientation(context: Context, imageUri: Uri): ImageProcessingOptions.Orientation {
        var orientation = ImageProcessingOptions.Orientation.TOP_LEFT
        context.contentResolver.openInputStream(imageUri)?.use { inputStream ->
            val exifInterface = ExifInterface(inputStream)
            val exifOrientation = exifInterface.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )

            orientation = when (exifOrientation){
                ExifInterface.ORIENTATION_ROTATE_90 -> ImageProcessingOptions.Orientation.RIGHT_TOP
                ExifInterface.ORIENTATION_ROTATE_180 -> ImageProcessingOptions.Orientation.BOTTOM_RIGHT
                ExifInterface.ORIENTATION_ROTATE_270 -> ImageProcessingOptions.Orientation.RIGHT_BOTTOM
                else -> ImageProcessingOptions.Orientation.TOP_LEFT
            }
        }

        return orientation
    }

    private fun getBitmapFromUri(imageUri: Uri): Bitmap {
        return MediaStore.Images.Media.getBitmap(context.contentResolver, imageUri)
    }

    interface ClassifierListener {
        fun onError(error: String)
        fun onResults(
            results: List<Classifications>?
        )
    }

}