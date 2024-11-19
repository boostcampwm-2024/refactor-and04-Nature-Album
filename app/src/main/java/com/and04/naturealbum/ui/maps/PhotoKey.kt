package com.and04.naturealbum.ui.maps

import com.naver.maps.geometry.LatLng
import com.naver.maps.map.clustering.ClusteringKey

class PhotoKey(val id: Int, private val position: LatLng) : ClusteringKey {

    override fun getPosition(): LatLng = position

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PhotoKey) return false
        return id == other.id
    }

    override fun hashCode() = id
}
