package com.and04.naturealbum.data.dto

import java.time.LocalDateTime

data class AlbumDto(
    val labelId: Int,
    val labelName: String,
    val labelBackgroundColor: String,
    val photoDetailUri: String
)

data class UnSynchronizedAlbumsDto(
    val labelId: Int,
    val labelName: String,
    val labelBackgroundColor: String,
    val photoDetailUri: String,
    val fileName: String
)

data class UnSynchronizedPhotoDetailsDto(
    val photoDetailUri: String,
    val labelName: String,
    val fileName: String,
    val longitude: Double,
    val latitude: Double,
    val description: String,
    val datetime: LocalDateTime
)
