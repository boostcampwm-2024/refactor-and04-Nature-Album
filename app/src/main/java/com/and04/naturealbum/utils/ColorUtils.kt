package com.and04.naturealbum.utils

import androidx.compose.ui.graphics.Color

fun String.toColor(): Color {
    val colorInt = android.graphics.Color.parseColor("#$this")
    return Color(colorInt)
}
