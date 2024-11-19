package com.and04.naturealbum.ui.maps

import android.util.Log
import com.and04.naturealbum.R
import com.and04.naturealbum.data.room.PhotoDetail
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.clustering.Cluster
import com.naver.maps.map.clustering.Node
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage

object F {
    val a = Array(22) { 4.007501668557849E7 / (1 shl it) }
    val b = Array(22) { a[it] / 512.0 }
}

class ClusterManager(photos: List<PhotoDetail>) {
    private var zoom: Int = -1
    private var nodes = mutableListOf<Node>()
    private val mapIdToPhoto = photos.associateBy { it.id }.toMutableMap()
    private val mapIdToMarker = photos.associate {
        it.id to Marker().apply {
            position = LatLng(it.latitude, it.longitude)
            icon = OverlayImage.fromResource(
                R.drawable.ellipse_15
            )
        }
    }.toMutableMap()
    private val parentMap = mutableMapOf<Int, List<Int>>()

    fun setPhotos(photos: List<PhotoDetail>) {
        photos.forEach { photo ->
            mapIdToPhoto[photo.id] = photo
            mapIdToMarker[photo.id] = Marker().apply {
                position = LatLng(photo.latitude, photo.longitude)
                icon = OverlayImage.fromResource(
                    R.drawable.ellipse_15
                )
            }
        }
    }

    fun setNodeGraph(cluster: Cluster) {
        nodes.add(cluster)
    }

    fun setLeafNode(tag: List<Int>) {
        parentMap[tag[0]] = tag
    }

    fun setClusterNode(tag: List<Int>) {
        tag.forEach { id -> parentMap[id] = tag }
    }

    fun setClusterMarker(marker: Marker, tag: List<Int>) {
        if (zoom == -1) return
        if (nodes.isEmpty()) return
        if (mapIdToMarker.isEmpty()) return
        val bounds = LatLngBounds.Builder().apply {
            tag.forEach { id -> include(mapIdToMarker[id]?.position) }
        }.build()
        bounds.center.let { marker.position = it }
        val z = bounds.northWest.distanceTo(bounds.northEast)
        val zz  = F.b[zoom]
        marker.width = (bounds.northWest.distanceTo(bounds.northEast) / F.b[zoom]).toInt()
        marker.height = (bounds.northWest.distanceTo(bounds.southWest) / F.b[zoom]).toInt()
        Log.e("MarkerSize", "${marker.width}, ${marker.height}")
    }

    fun setZoom(zoom: Int) {
        this.zoom = zoom
    }

    fun getMarker() = mapIdToMarker.values

}