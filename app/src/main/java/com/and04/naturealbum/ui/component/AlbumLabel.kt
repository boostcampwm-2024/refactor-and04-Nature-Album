package com.and04.naturealbum.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import com.and04.naturealbum.ui.theme.AppTypography

@Composable
fun AlbumLabel(
    modifier: Modifier = Modifier,
    text: String,
    backgroundColor: Color,
) {
    val calculatedTextColor = if (backgroundColor.luminance() > 0.5f) Color.Black else Color.White

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        Text(
            text = text,
            color = calculatedTextColor,
            style = AppTypography.labelMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}
