package com.and04.naturealbum.ui.maps

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import coil3.load
import coil3.request.crossfade
import coil3.request.error
import coil3.request.placeholder
import com.and04.naturealbum.R
import java.io.File
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.RelativeLayout
import java.io.IOException
import java.io.InputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.net.HttpURLConnection
import java.net.URL


class ImageMarkerCoilverson1 @JvmOverloads constructor(
    context: Context,
    uri: String,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {
    init {
        Log.e("ImageMarker", uri)
        LayoutInflater.from(context).inflate(R.layout.image_marker, this, true)
        findViewById<ImageView>(R.id.iv_marker_image).load(File(uri)) {
            crossfade(true)
            placeholder(R.drawable.cat_dummy)
            error(R.drawable.ic_launcher_background)
        }
    }
}






class ImageMarkerCoil @JvmOverloads constructor(
    context: Context,
    uri: String,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val imageView: ImageView

    init {
        Log.d("ImageMarkerCoil", "ImageMarkerCoil initialized with URI: $uri")

        // XML 레이아웃을 인플레이트
        LayoutInflater.from(context).inflate(R.layout.image_marker, this, true)
        imageView = findViewById(R.id.iv_marker_image)

        // Coil을 통해 이미지 로드, 로드 상태 확인
        //imageView.load(File(uri)) {
        imageView.load(Uri.parse(uri)) {
            crossfade(true)
            placeholder(R.drawable.cat_dummy)
            error(R.drawable.ic_launcher_background)
            listener(
                onStart = {
                    Log.d("ImageMarkerCoil", "Coil image loading started.")
                },
                onSuccess = { _, _ ->
                    Log.d("ImageMarkerCoil", "Coil image loading success.")
                },
                onError = { _, throwable ->
                    Log.e("ImageMarkerCoil", "Coil image loading failed: ${throwable.throwable}")
                },
                onCancel = {
                    Log.d("ImageMarkerCoil", "Coil image loading canceled.")
                }
            )
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        Log.d("ImageMarkerCoil", "ImageMarkerCoil attached to window.")
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        Log.d("ImageMarkerCoil", "ImageMarkerCoil detached from window.")
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        Log.d("ImageMarkerCoil", "ImageMarkerCoil onMeasure called.")
    }
}







class ImageMarker @JvmOverloads constructor(
    context: Context,
    uri: String,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    init {
        Log.e("ImageMarker", "Image URI: $uri")
        LayoutInflater.from(context).inflate(R.layout.image_marker, this, true)

        // `uri`가 파일 경로인지, URI로부터 이미지를 로드할지 결정
        val imageView = findViewById<ImageView>(R.id.iv_marker_image)

        // 파일 경로 처리
        val bitmap: Bitmap? = if (uri.startsWith("file://")) {
            // 파일 경로에서 비트맵 로드
            loadImageFromFile(Uri.parse(uri), context)
        } else {
            // URI로부터 비트맵 로드
            loadImageFromUri(Uri.parse(uri), context)
        }

        bitmap?.let {
            imageView.setImageBitmap(it)
        } ?: run {
            // 에러 처리: 이미지가 없으면 기본 이미지 사용
            imageView.setImageResource(R.drawable.cat_dummy)
        }
    }

    private fun loadImageFromFile(uri: Uri, context: Context): Bitmap? {
        return try {
            val file = File(uri.path)
            BitmapFactory.decodeFile(file.absolutePath)
        } catch (e: IOException) {
            Log.e("ImageMarker", "Error loading image from file: ${e.message}")
            null
        }
    }

    private fun loadImageFromUri(uri: Uri, context: Context): Bitmap? {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            BitmapFactory.decodeStream(inputStream)
        } catch (e: IOException) {
            Log.e("ImageMarker", "Error loading image from URI: ${e.message}")
            null
        }
    }
}


class ImageMarkerHttpBug @JvmOverloads constructor(
    context: Context,
    uri: String,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    init {
        Log.e("ImageMarker", "Image URI: $uri")
        LayoutInflater.from(context).inflate(R.layout.image_marker, this, true)

        val imageView = findViewById<ImageView>(R.id.iv_marker_image)

        // URI가 파일 경로인지 content:// URI인지 URL인지 확인
        if (uri.startsWith("file://")) {
            // 파일 경로에서 비트맵 로드
            val bitmap = loadImageFromFile(Uri.parse(uri), context)
            bitmap?.let {
                imageView.setImageBitmap(it)
            } ?: run {
                imageView.setImageResource(R.drawable.cat_dummy) // 기본 이미지
            }
        } else if (uri.startsWith("content://")) {
            // content:// URI에서 비트맵 로드
            val bitmap = loadImageFromContentUri(Uri.parse(uri), context)
            bitmap?.let {
                imageView.setImageBitmap(it)
            } ?: run {
                imageView.setImageResource(R.drawable.cat_dummy) // 기본 이미지
            }
        } else if (uri.startsWith("http://") || uri.startsWith("https://")) {
            // 서버 URL에서 비트맵 로드 (네트워크 작업)
            loadImageFromUrlAsync(uri, context, imageView)
        }
    }

    private fun loadImageFromFile(uri: Uri, context: Context): Bitmap? {
        return try {
            val file = File(uri.path)
            BitmapFactory.decodeFile(file.absolutePath)
        } catch (e: IOException) {
            Log.e("ImageMarker", "Error loading image from file: ${e.message}")
            null
        }
    }

    private fun loadImageFromContentUri(uri: Uri, context: Context): Bitmap? {
        return try {
            // ContentResolver를 통해 URI에서 InputStream을 열고 Bitmap으로 디코딩
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            BitmapFactory.decodeStream(inputStream)
        } catch (e: IOException) {
            Log.e("ImageMarker", "Error loading image from content URI: ${e.message}")
            null
        }
    }

    private fun loadImageFromUrlAsync(uri: String, context: Context, imageView: ImageView) {
        // Launch a coroutine to load image in the background
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val bitmap = loadImageFromUrl(uri, context)
                // Switch to the main thread to update UI
                imageView.post {
                    bitmap?.let {
                        imageView.setImageBitmap(it)
                    } ?: run {
                        imageView.setImageResource(R.drawable.cat_dummy) // 기본 이미지
                    }
                }
            } catch (e: Exception) {
                Log.e("ImageMarker", "Error loading image from URL: ${e.message}")
                imageView.post {
                    imageView.setImageResource(R.drawable.cat_dummy) // 기본 이미지
                }
            }
        }
    }

    private fun loadImageFromUrl(uri: String, context: Context): Bitmap? {
        return try {
            val url = URL(uri)
            val connection = url.openConnection() as HttpURLConnection
            connection.apply {
                doInput = true
                connect()
            }

            // 응답 코드 확인
            val responseCode = connection.responseCode
            Log.d("ImageMarker", "Response Code: $responseCode")

            if (responseCode == HttpURLConnection.HTTP_OK) {
                // InputStream을 열고 이미지 디코딩
                val inputStream = connection.inputStream
                val bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream.close() // 스트림을 닫아줍니다.

                // 디코딩된 Bitmap 반환
                bitmap
            } else {
                Log.e("ImageMarker", "Error loading image from URL: Response Code $responseCode")
                null
            }
        } catch (e: IOException) {
            Log.e("ImageMarker", "Error loading image from URL: ${e.message}")
            null
        }
    }


}
