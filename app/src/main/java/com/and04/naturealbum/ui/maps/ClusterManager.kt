package com.and04.naturealbum.ui.maps

import com.and04.naturealbum.R
import com.and04.naturealbum.data.room.PhotoDetail
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.clustering.Cluster
import com.naver.maps.map.clustering.Node
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage

class ClusterManager(photos: List<PhotoDetail>) {
    private var nodes = mutableListOf<Node>()
    private val mapIdToPhoto = photos.associateBy { it.id }
    private val mapIdToMarker = photos.associate { it.id to Marker(LatLng(it.latitude, it.longitude), OverlayImage.fromResource(
        R.drawable.frame)) }
    private val parentMap = mutableMapOf<Int, List<Int>>()
    fun setNodeGraph(cluster: Cluster) {
        nodes.add(cluster)
    }
    fun setLeafNode(tag: List<Int>){
        parentMap[tag[0]] = tag
    }
    fun setClusterNode(tag: List<Int>){
        tag.forEach{ id-> parentMap[id] = tag}
    }
    fun getMarker() = mapIdToMarker.values

}