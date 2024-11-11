package com.and04.naturealbum.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp

@Composable
fun RoundedShapeButton(text: String, modifier: Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(10)
    ) {
        Text(
            text = text,
            modifier = Modifier
                .padding(top = 24.dp)
                .align(Alignment.Top)
        )
    }
}

@Composable
fun NavigationImageButton(
    text: String,
    modifier: Modifier,
    textColor: Color,
    imageVector: ImageVector,
    onClick: () -> Unit
) {

    val svgAspectRatio = imageVector.viewportWidth / imageVector.viewportHeight

    Box(modifier = modifier
        .aspectRatio(svgAspectRatio)
        .clickable { onClick() })
    {
        Image(
            imageVector = imageVector,
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize()
        )

        Text(
            text = text,
            modifier = Modifier
                .padding(start = 12.dp, top = 8.dp),
            color = textColor
        )
    }
}
