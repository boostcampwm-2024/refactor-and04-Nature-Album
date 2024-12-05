package com.and04.naturealbum.ui.maps

import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import coil3.load
import coil3.request.error
import coil3.request.placeholder
import coil3.request.transformations
import coil3.transform.CircleCropTransformation
import com.and04.naturealbum.R
import com.and04.naturealbum.databinding.ImageMarkerBinding

class ImageMarker @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding: ImageMarkerBinding =
        ImageMarkerBinding.inflate(LayoutInflater.from(context), this, true)

    fun loadImage(uri: String, onImageLoaded: () -> Unit = {}) {
        binding.ivMarkerImage.load(Uri.parse(uri)) {
            transformations(CircleCropTransformation())
            placeholder(R.drawable.ic_hourglass_top)
            error(R.drawable.ic_hourglass_disable)
            listener(
                onSuccess = { _, _ ->
                    onImageLoaded()
                },
                onStart = { _ ->
                    onImageLoaded()
                },
                onError = { _, _ ->
                    onImageLoaded()
                }
            )
        }
    }
}
