package com.and04.naturealbum.data.dto

data class FirebaseLabel(
    val backgroundColor: String,
    val thumbnailUri: String
)

data class FirebaseLabelResponse(
    val labelName: String = "",
    val backgroundColor: String = "",
    val thumbnailUri: String = ""
)

data class FirebasePhotoInfo(
    val uri: String,
    val label: String,
    val latitude: Double?,
    val longitude: Double?,
    val description: String,
    val datetime: String
)

data class FirebasePhotoInfoResponse(
    val fileName: String = "",
    val uri: String = "",
    val label: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val description: String = "",
    val datetime: String = ""
)
