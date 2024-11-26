package com.and04.naturealbum.ui.maps

import com.and04.naturealbum.data.room.Label
import com.and04.naturealbum.data.room.PhotoDetail
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.clustering.ClusteringKey
import java.time.LocalDateTime

class PhotoKey(photoItem: PhotoItem) : ClusteringKey {
    val id = photoItem.uri.hashCode()
    private val position = photoItem.position
    override fun getPosition(): LatLng = position

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PhotoKey) return false
        return id == other.id
    }

    override fun hashCode() = id
}

data class LabelItem(
    val name: String,
    val color: String,
)

data class PhotoItem(
    val uri: String,
    val position: LatLng,
    val label: LabelItem,
    val time: LocalDateTime,
)

// Room Data -> UI Data
fun List<Label>.toLabelItems() = map { label -> label.toLabelItem() }
fun Label.toLabelItem() = LabelItem(name, backgroundColor)
fun List<PhotoDetail>.toPhotoItems(labels: List<Label>): List<PhotoItem> {
    val labelMap = labels.associateBy { it.id }
    return map { photoDetail ->
        PhotoItem(
            photoDetail.photoUri,
            LatLng(photoDetail.latitude, photoDetail.longitude),
            labelMap.getValue(photoDetail.labelId).toLabelItem(),
            photoDetail.datetime
        )
    }
}