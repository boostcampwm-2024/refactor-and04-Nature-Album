package com.and04.naturealbum.data.dto

data class AlbumDto(
    val labelId: Int,
    val labelName: String,
    val labelBackgroundColor: String,
    val photoDetailUri: String
) {
    companion object {
        fun dummyList(): List<AlbumDto> {
            return listOf(
                AlbumDto(0, "강아지", "E57373", ""),
                AlbumDto(1, "고양이", "D1C4E9", ""),
                AlbumDto(2, "해오라기", "C5E1A5", "")
            )
        }
    }
}
