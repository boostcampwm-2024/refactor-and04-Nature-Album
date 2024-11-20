package com.and04.naturealbum.ui.maps

import android.annotation.SuppressLint
import android.graphics.PointF
import android.view.View
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.and04.naturealbum.R
import com.and04.naturealbum.data.room.PhotoDetail
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.MapView
import com.naver.maps.map.clustering.ClusterMarkerInfo
import com.naver.maps.map.clustering.Clusterer
import com.naver.maps.map.clustering.DefaultClusterMarkerUpdater
import com.naver.maps.map.clustering.DefaultLeafMarkerUpdater
import com.naver.maps.map.clustering.DefaultMarkerManager
import com.naver.maps.map.clustering.LeafMarkerInfo
import com.naver.maps.map.clustering.MarkerInfo
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.Overlay
import com.naver.maps.map.overlay.OverlayImage

@SuppressLint("NewApi")
@Composable
fun MapScreen(
    modifier: Modifier = Modifier,
    viewModel: MapScreenViewModel = hiltViewModel(),
) {
    val photos = viewModel.photos.collectAsStateWithLifecycle()
    val idToPhoto = remember { mutableMapOf<Int, PhotoDetail>() } // id와 PhotoDetail 매핑
    val context = LocalContext.current
    val lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
    val marker = Marker()

    val mapView = remember {
        MapView(context).apply {
            id = R.id.map_view_id
        }
    }

    val imageMarker = remember {
        ImageMarker(context).apply {
            visibility = View.INVISIBLE
            mapView.addView(this)
            viewTreeObserver.addOnGlobalLayoutListener({
                if (isImageLoaded()) {
                    marker.icon = OverlayImage.fromView(this@apply)
                }
            })
        }
    }

    val clusterImage = OverlayImage.fromResource(R.drawable.ic_cluster)

    val cluster: Clusterer<PhotoKey> = remember {
        val onClickMarker: (MarkerInfo) -> Overlay.OnClickListener = { info ->
            Overlay.OnClickListener {
                val photoDetailIds = info.tag as List<*>
                val pick =
                    photoDetailIds.map { id -> idToPhoto[id]!! } // id 리스트를 PhotoDetail 리스트로 변환
                        .groupBy { photoDetail -> photoDetail.labelId } // labelId로 그룹화
                        .maxBy { (_, photos) -> photos.size } // 그룹 중 가장 많은 사진을 가진 그룹 선택
                        .value // 가장 많은 사진을 가진 그룹의 사진 리스트
                        .maxBy { photoDetail -> photoDetail.datetime } // 가장 최근 사진 선택
                imageMarker.loadImage(pick.photoUri)
                marker.position = LatLng(pick.latitude, pick.longitude)
                mapView.getMapAsync { naverMap -> marker.map = naverMap }
                true
            }
        }
        Clusterer.ComplexBuilder<PhotoKey>().tagMergeStrategy { cluster ->
            // cluster의 tag는 해당 cluster에 포함된 사진들의 id 리스트
            cluster.children.flatMap { node -> node.tag as List<*> }
        }.clusterMarkerUpdater(object : DefaultClusterMarkerUpdater() {
            override fun updateClusterMarker(info: ClusterMarkerInfo, marker: Marker) {
                marker.onClickListener = onClickMarker(info)
            }
        }).leafMarkerUpdater(object : DefaultLeafMarkerUpdater() {
            override fun updateLeafMarker(info: LeafMarkerInfo, marker: Marker) {
                marker.onClickListener = onClickMarker(info)
            }
        }).markerManager(object : DefaultMarkerManager() {
            override fun createMarker(): Marker {
                return Marker().apply {
                    zIndex = -1
                    icon = clusterImage
                    isFlat = true
                    anchor = PointF(0.5f, 0.5f)
                }
            }
        })
    }.build()

    LaunchedEffect(photos.value) {
        photos.value.forEach { photoDetail -> idToPhoto[photoDetail.id] = photoDetail }
        cluster.addAll(photos.value.associate { photoDetail ->
            PhotoKey(
                photoDetail.id, LatLng(photoDetail.latitude, photoDetail.longitude)
            ) to listOf(photoDetail.id)
        })
    }

    // MapView의 생명주기를 관리하기 위해 DisposableEffect를 사용
    DisposableEffect(lifecycleOwner) {
        // 현재 LifecycleOwner의 Lifecycle을 가져오기
        val lifecycle = lifecycleOwner.lifecycle
        // Lifecycle 이벤트를 관찰하는 Observer를 생성
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_CREATE -> mapView.onCreate(null)
                Lifecycle.Event.ON_START -> mapView.onStart()
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                Lifecycle.Event.ON_STOP -> mapView.onStop()
                Lifecycle.Event.ON_DESTROY -> mapView.onDestroy()
                else -> {}
            }
        }
        // Lifecycle에 Observer를 추가하여 생명주기를 관찰
        lifecycle.addObserver(observer)

        // DisposableEffect가 해제될 때 Observer를 제거하고 MapView의 리소스를 해제
        onDispose {
            lifecycle.removeObserver(observer)
            cluster.clear()
            cluster.map = null
            mapView.onDestroy() // MapView의 리소스를 해제하여 메모리 누수를 방지
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // AndroidView를 MapView로 바로 설정
        AndroidView(factory = { mapView }, modifier = modifier.fillMaxSize())

        mapView.getMapAsync { naverMap ->
            cluster.map = naverMap
        }
    }
}

@Composable
fun PhotoGrid(
    modifier: Modifier = Modifier,
    photos: List<PhotoDetail>,
    onPhotoClick: (Int) -> Unit,
) {
    LazyVerticalGrid(columns = GridCells.Adaptive(256.dp)) {
        items(photos) { photo ->
            AsyncImage(model = photo.photoUri, contentDescription = null)
        }
    }
}
