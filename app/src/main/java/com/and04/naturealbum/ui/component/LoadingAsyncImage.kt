package com.and04.naturealbum.ui.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.ImageNotSupported
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import coil3.compose.AsyncImagePainter
import coil3.compose.SubcomposeAsyncImage
import coil3.compose.SubcomposeAsyncImageContent
import com.and04.naturealbum.R

@Composable
fun LoadingAsyncImage(
    model: Any?,
    contentDescription: String? = null,
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    clipToBounds: Boolean = true,
) {
    SubcomposeAsyncImage(
        model = model,
        contentDescription = contentDescription,
        modifier = modifier,
        alignment = alignment,
        contentScale = contentScale,
        clipToBounds = clipToBounds
    ) {
        val state by painter.state.collectAsState()
        when (state) {
            is AsyncImagePainter.State.Loading -> RotatingImageLoading(
                drawableRes = LoadingIcons.entries.random().id,
                stringRes = null,
            )

            is AsyncImagePainter.State.Success -> SubcomposeAsyncImageContent()
            is AsyncImagePainter.State.Empty -> Icon(
                imageVector = Icons.Outlined.Image,
                contentDescription = stringResource(R.string.loading_async_image_loading)
            )

            is AsyncImagePainter.State.Error -> Icon(
                imageVector = Icons.Outlined.ImageNotSupported,
                contentDescription = stringResource(R.string.loading_async_image_load_fail)
            )
        }
    }
}