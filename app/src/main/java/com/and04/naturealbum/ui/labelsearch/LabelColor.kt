package com.and04.naturealbum.ui.labelsearch

fun getRandomColor(): String {
    val r = (0..255).random()
    val g = (0..255).random()
    val b = (0..255).random()

    return "FF${r.toHex()}${g.toHex()}${b.toHex()}"
}

private fun Int.toHex(): String {
    val hex = Integer.toHexString(this)
    return if (hex.length == 1) "0$hex" else hex
}