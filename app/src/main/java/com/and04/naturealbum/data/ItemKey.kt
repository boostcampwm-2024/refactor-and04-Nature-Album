package com.and04.naturealbum.data

import com.and04.naturealbum.data.room.PhotoDetail
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.clustering.ClusteringKey

class ItemKey(val photoDetail: PhotoDetail) : ClusteringKey {

    override fun getPosition(): LatLng = LatLng(photoDetail.latitude, photoDetail.longitude)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ItemKey) return false
        return photoDetail.id == other.photoDetail.id
    }

    override fun hashCode() = photoDetail.id
}