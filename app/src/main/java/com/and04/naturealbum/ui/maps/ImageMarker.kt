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


class ImageMarker @JvmOverloads constructor(
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
