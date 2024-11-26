package com.and04.naturealbum.data.dto

data class FirebaseLabel(
    val backgroundColor: String,
    val thumbnailUri: String
)

data class FirebasePhotoInfo(
    val uri: String,
    val label: String,
    val latitude: Double?,
    val longitude: Double?,
    val description: String,
    val datetime: String
)
