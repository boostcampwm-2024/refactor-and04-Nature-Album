package com.and04.naturealbum.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.net.toUri
import androidx.exifinterface.media.ExifInterface
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream

class ImageConvert(
    private val context: Context
) {
    fun resizeImage(uri: Uri): ResizePicture? {
        try {
            val storage = context.filesDir
            val fileName = "${System.currentTimeMillis()}.jpg"

            val imageFile = File(storage, fileName)
            imageFile.createNewFile()

            val fos = FileOutputStream(imageFile)

            decodeBitmapFromUri(uri)?.apply {
                if (Build.VERSION.SDK_INT >= 30) {
                    compress(Bitmap.CompressFormat.WEBP_LOSSY, 100, fos)
                } else {
                    compress(Bitmap.CompressFormat.JPEG, 100, fos)
                }

                recycle()
            } ?: throw NullPointerException()

            fos.flush()
            fos.close()

            return ResizePicture(
                fileName = fileName,
                uri = imageFile.absolutePath.toUri()
            )

        } catch (e: Exception) {
            Log.e("ImageConvert", "FileUtil - ${e.message}")
        }

        return null
    }

    private fun decodeBitmapFromUri(uri: Uri): Bitmap? {
        val input = BufferedInputStream(context.contentResolver.openInputStream(uri))
        input.mark(input.available())

        var bitmap: Bitmap?

        BitmapFactory.Options().run {
            inJustDecodeBounds = true
            bitmap = BitmapFactory.decodeStream(input, null, this)
            input.reset()

            inSampleSize = calculateInSampleSize(this)
            inJustDecodeBounds = false

            bitmap = BitmapFactory.decodeStream(input, null, this)
            if (bitmap != null) bitmap = rotateImageIfRequired(bitmap = bitmap!!, uri = uri)
        }

        input.close()

        return bitmap
    }

    private fun calculateInSampleSize(options: BitmapFactory.Options): Int {
        val (height: Int, width: Int) = options.run { outHeight to outWidth }
        var inSampleSize = 1
        if (height > MAX_HEIGHT || width > MAX_WIDTH) {

            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2

            while (halfHeight / inSampleSize >= MAX_HEIGHT && halfWidth / inSampleSize >= MAX_WIDTH) {
                inSampleSize *= 2
            }
        }
        Log.e("inSampleSize", inSampleSize.toString())
        return inSampleSize
    }

    private fun rotateImageIfRequired(bitmap: Bitmap, uri: Uri): Bitmap? {
        val input = context.contentResolver.openInputStream(uri) ?: return null
        val exif = ExifInterface(input)

        val orientation =
            exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)

        return when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(bitmap, 90)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(bitmap, 180)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(bitmap, 270)
            else -> bitmap
        }
    }

    private fun rotateImage(bitmap: Bitmap, degree: Int): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degree.toFloat())
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    companion object {
        private const val MAX_WIDTH = 800
        private const val MAX_HEIGHT = 600
    }
}

data class ResizePicture(
    val fileName: String,
    val uri: Uri
)
