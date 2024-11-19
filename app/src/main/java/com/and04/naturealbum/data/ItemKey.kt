package com.and04.naturealbum.data

import com.and04.naturealbum.data.room.PhotoDetail
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.clustering.ClusteringKey

class ItemKey(val id: Int, private val position: LatLng) : ClusteringKey {

    override fun getPosition(): LatLng = position

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ItemKey) return false
        return id == other.id
    }

    override fun hashCode() = id
}