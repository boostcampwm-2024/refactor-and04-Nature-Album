package com.and04.naturealbum.ui.label_search

import androidx.compose.ui.graphics.Color

fun getRandomColor(): String {
    val r = (0..255).random()
    val g = (0..255).random()
    val b = (0..255).random()

    return "#${r.toHex()}${g.toHex()}${b.toHex()}"
}

fun Int.toHex(): String {
    val hex = Integer.toHexString(this)
    return if (hex.length == 1) "0$hex" else hex
}

fun getLabelTextColor(colorString: String): Color {
    val color = android.graphics.Color.parseColor(colorString)

    val r = android.graphics.Color.red(color)
    val g = android.graphics.Color.green(color)
    val b = android.graphics.Color.blue(color)

    val brightness = (0.299 * r + 0.587 * g + 0.114 * b)

    return if (brightness < 128) {
        Color.White
    } else {
        Color.Black
    }
}