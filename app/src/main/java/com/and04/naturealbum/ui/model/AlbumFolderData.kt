package com.and04.naturealbum.ui.model

import com.and04.naturealbum.data.room.Label
import com.and04.naturealbum.data.room.PhotoDetail

data class AlbumFolderData(
    val label: Label,
    val photoDetails: List<PhotoDetail>
)