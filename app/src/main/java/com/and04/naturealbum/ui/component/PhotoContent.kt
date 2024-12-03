package com.and04.naturealbum.ui.component

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun PhotoContent(
    imageUri: Any,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    BackHandler {
        onDismiss()
    }

    BoxWithConstraints(
        modifier
            .fillMaxSize()
            .background(Color.Black)
    )
    {
        val state = rememberTransformableState { zoomChange, offsetChange, rotationChange ->
            scale = (scale * zoomChange).coerceIn(1f, 3f)

            val extraWidth = (scale - 1) * constraints.maxWidth
            val extraHeight = (scale - 1) * constraints.maxHeight

            val maxX = extraWidth / 2
            val maxY = extraHeight / 2

            offset = Offset(
                x = (offset.x + scale * offsetChange.x).coerceIn(-maxX, maxX),
                y = (offset.y + scale * offsetChange.y).coerceIn(-maxY, maxY)
            )
        }
        AsyncImage(
            model = imageUri,
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center)
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    translationX = offset.x
                    translationY = offset.y
                }
                .transformable(state),
            contentDescription = "",
        )

        IconButton(
            modifier = modifier.align(Alignment.TopEnd),
            onClick = {onDismiss()},
            colors = IconButtonDefaults.iconButtonColors(contentColor = Color.White)
        ) {
            Icon(
                modifier = modifier.size(96.dp),
                imageVector = Icons.Default.Close,
                contentDescription = null
            )
        }
    }
}
