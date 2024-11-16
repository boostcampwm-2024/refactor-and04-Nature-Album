package com.and04.naturealbum.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import com.and04.naturealbum.R

@Composable
fun BackgroundImage() {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            imageVector = ImageVector.vectorResource(id = R.drawable.drawable_background_image_bottom_start),
            modifier = Modifier.align(Alignment.BottomStart),
            contentDescription = null,
        )

        Image(
            imageVector = ImageVector.vectorResource(id = R.drawable.drawable_background_image_top_end),
            modifier = Modifier.align(Alignment.TopEnd),
            contentDescription = null,
        )
    }
}
