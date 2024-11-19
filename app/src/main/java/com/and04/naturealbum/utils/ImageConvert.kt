package com.and04.naturealbum.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.content.FileProvider
import androidx.exifinterface.media.ExifInterface
import com.and04.naturealbum.NatureAlbum
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream

object ImageConvert {
    private const val MAX_WIDTH = 800
    private const val MAX_HEIGHT = 600
    private const val COMPRESS_QUALITY = 80

    fun resizeImage(uri: Uri): ResizePicture? {
        try {
            val context = NatureAlbum.getInstance()
            val storage = context.filesDir
            val fileName = "${System.currentTimeMillis()}.jpg"

            val imageFile = File(storage, fileName)
            imageFile.createNewFile()

            FileOutputStream(imageFile).use { fos ->
                decodeBitmapFromUri(context = context, uri = uri)?.apply {
                    if (Build.VERSION.SDK_INT >= 30) {
                        compress(Bitmap.CompressFormat.WEBP_LOSSY, COMPRESS_QUALITY, fos)
                    } else {
                        compress(Bitmap.CompressFormat.JPEG, COMPRESS_QUALITY, fos)
                    }

                    recycle()
                } ?: throw NullPointerException()

                fos.flush()
            }

            return ResizePicture(
                fileName = fileName,
                uri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    imageFile
                )
            )

        } catch (e: Exception) {
            Log.e("ImageConvert", "FileUtil - ${e.message}")
        }

        return null
    }

    private fun decodeBitmapFromUri(context: Context, uri: Uri): Bitmap? {
        BufferedInputStream(
            context.contentResolver.openInputStream(uri)
        ).use { input ->
            input.mark(input.available())

            var bitmap: Bitmap?

            BitmapFactory.Options().run {
                inJustDecodeBounds = true
                bitmap = BitmapFactory.decodeStream(input, null, this)
                input.reset()

                inSampleSize = calculateInSampleSize(options = this)
                inJustDecodeBounds = false

                bitmap = BitmapFactory.decodeStream(input, null, this)
                if (bitmap != null) bitmap =
                    rotateImageIfRequired(context = context, bitmap = bitmap!!, uri = uri)
            }

            return bitmap
        }
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

        return inSampleSize
    }

    private fun rotateImageIfRequired(context: Context, bitmap: Bitmap, uri: Uri): Bitmap? {
        val input = context.contentResolver.openInputStream(uri) ?: return null
        input.use {
            val exif = ExifInterface(input)

            val orientation =
                exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL
                )

            return when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(bitmap, 90)
                ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(bitmap, 180)
                ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(bitmap, 270)
                else -> bitmap
            }
        }
    }

    private fun rotateImage(bitmap: Bitmap, degree: Int): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degree.toFloat())
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }
}

data class ResizePicture(
    val fileName: String,
    val uri: Uri
)
