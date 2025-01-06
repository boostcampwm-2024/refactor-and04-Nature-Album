package com.and04.naturealbum.model

import com.and04.naturealbum.data.room.Label
import com.and04.naturealbum.data.room.PhotoDetail

data class AlbumData(
    val label: Label,
    val photoDetails: PhotoDetail
)

data class AlbumFolderData(
    val label: Label,
    val photoDetails: List<PhotoDetail>
)
