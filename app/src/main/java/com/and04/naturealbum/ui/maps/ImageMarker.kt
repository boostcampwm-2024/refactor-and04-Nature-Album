package com.and04.naturealbum.ui.maps

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.RelativeLayout
import androidx.constraintlayout.widget.ConstraintLayout
import coil3.load
import coil3.request.allowHardware
import coil3.request.error
import coil3.request.transformations
import coil3.transform.CircleCropTransformation
import com.and04.naturealbum.R
import com.and04.naturealbum.databinding.ImageMarkerBinding
import java.io.File
import java.io.IOException
import java.io.InputStream


class ImageMarkerCoil @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding: ImageMarkerBinding =
        ImageMarkerBinding.inflate(LayoutInflater.from(context), this, true)

    fun loadImage(uri: String, onImageLoaded: () -> Unit) {
        binding.ivMarkerImage.load(Uri.parse(uri)) {
            transformations(CircleCropTransformation())
            error(R.drawable.ic_launcher_background)
            allowHardware(false)
            listener(
                onSuccess = { _, _ ->
                    onImageLoaded()  // 이미지 로딩 완료 후 콜백 호출
                }
            )
        }
    }

    fun isImageLoaded(): Boolean = width > 0 && height > 0
}


class ImageMarkerFromLocalFileBitmap @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    private val binding: ImageMarkerBinding =
        ImageMarkerBinding.inflate(LayoutInflater.from(context), this, true)

    fun loadImage(uri: String) {
        // 파일 경로 처리
        val bitmap: Bitmap? = if (uri.startsWith("file://")) {
            // 파일 경로에서 비트맵 로드
            loadImageFromFile(Uri.parse(uri), context)
        } else {
            // URI로부터 비트맵 로드
            loadImageFromUri(Uri.parse(uri), context)
        }

        bitmap?.let {
            binding.ivMarkerImage.setImageBitmap(bitmap)
        } ?: run {
            // 에러 처리: 이미지가 없으면 기본 이미지 사용
            binding.ivMarkerImage.setImageResource(R.drawable.ic_launcher_background)
        }
    }

    private fun loadImageFromFile(uri: Uri, context: Context): Bitmap? {
        return try {
            val file = File(uri.path)
            BitmapFactory.decodeFile(file.absolutePath)
        } catch (e: IOException) {
            null
        }
    }

    private fun loadImageFromUri(uri: Uri, context: Context): Bitmap? {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            BitmapFactory.decodeStream(inputStream)
        } catch (e: IOException) {
            null
        }
    }
}
