package com.and04.naturealbum.data.dto

import android.net.Uri

data class AlbumDto(
    val labelId: Int,
    val labelName: String,
    val photoDetailUri: Uri
)
