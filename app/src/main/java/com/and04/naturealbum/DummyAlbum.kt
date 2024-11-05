package com.and04.naturealbum

import androidx.compose.ui.graphics.Color

data class Album(
    val id: Int,
    val label: Label,
    val photoDetail: PhotoDetail
)

data class Label(
    val id: Int,
    val backgroundColor: Color,
    val name: String
)

data class PhotoDetail(
    val id: Int,
    val imageResId: Int,
    val description: String
)


fun getDummyAlbums(): List<Album> {
    val label1 = Label(1, Color(0xFFE57373), "식빵")
    val label2 = Label(2, Color(0xFF81C784), "고양이")
    val photo1 = PhotoDetail(1, R.drawable.sample_image_01, "밥 먹자")
    val photo2 = PhotoDetail(2, R.drawable.sample_image_02, "고양이")
    return listOf(
        Album(1, label1, photo1),
        Album(2, label2, photo2),
        Album(3, label1, photo1),
        Album(4, label2, photo2)
    )
}
