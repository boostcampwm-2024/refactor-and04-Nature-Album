package com.and04.naturealbum.ui.component

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.and04.naturealbum.R

@Composable
fun RotatingButton(
    rotatingState: Boolean,
    imageVector: ImageVector,
    contentDescription: String,
) {
    val infiniteTransition =
        rememberInfiniteTransition(stringResource(R.string.rotating_icon_button))
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1_000),
        ),
        label = stringResource(R.string.rotating_icon_button)
    )
    if (rotatingState) {
        Icon(
            imageVector = imageVector,
            contentDescription = contentDescription,
            modifier = Modifier
                .size(64.dp)
                .graphicsLayer(rotationZ = rotation)
        )
    } else {
        Icon(
            imageVector = Icons.Default.Sync,
            contentDescription = stringResource(R.string.my_page_sync_icon_content_description),
            modifier = Modifier.size(64.dp)
        )
    }
}
