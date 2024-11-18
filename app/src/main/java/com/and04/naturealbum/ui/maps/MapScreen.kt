package com.and04.naturealbum.ui.maps

import android.annotation.SuppressLint
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.and04.naturealbum.R
import com.and04.naturealbum.data.ItemKey
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.MapView
import com.naver.maps.map.clustering.ClusterMarkerInfo
import com.naver.maps.map.clustering.Clusterer
import com.naver.maps.map.clustering.DefaultClusterMarkerUpdater
import com.naver.maps.map.clustering.DefaultLeafMarkerUpdater
import com.naver.maps.map.clustering.LeafMarkerInfo
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.MarkerIcons
import kotlinx.coroutines.delay


@Composable
fun MapFromLocalFileScreen(
    modifier: Modifier = Modifier,
    viewModel: MapScreenViewModel = hiltViewModel(),
) {
    val photos = viewModel.photos.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current

    val mapView = remember {
        MapView(context).apply {
            id = R.id.map_view_id
        }
    }

    // MapView 생명주기 관리
    DisposableEffect(lifecycleOwner) {
        val lifecycle = lifecycleOwner.lifecycle
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
        lifecycle.addObserver(observer)

        onDispose {
            lifecycle.removeObserver(observer)
            mapView.onDestroy()
        }
    }

    // AndroidView로 MapView를 표시
    AndroidView(factory = { mapView }, modifier = modifier) {
        mapView.getMapAsync { naverMap ->
            photos.value.map { photoDetail ->
                // ImageMarker 사용
                val imageMarker =
                    ImageMarkerFromLocalFileBitmap(context).apply { loadImage(photoDetail.photoUri) }
                Marker().apply {
                    position = LatLng(photoDetail.latitude, photoDetail.longitude)
                    icon = OverlayImage.fromView(imageMarker) // ImageMarker를 아이콘으로 설정
                    map = naverMap
                }
            }
        }
    }
}

@SuppressLint("NewApi")
@Composable
fun MapScreen(
    modifier: Modifier = Modifier,
    viewModel: MapScreenViewModel = hiltViewModel(),
) {
    val photos = viewModel.photos.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current

    val mapView = remember {
        MapView(context).apply {
            id = R.id.map_view_id
        }
    }

    val cluster: Clusterer<ItemKey> = remember {
        Clusterer.Builder<ItemKey>().clusterMarkerUpdater(object : DefaultClusterMarkerUpdater() {
            override fun updateClusterMarker(info: ClusterMarkerInfo, marker: Marker) {
                super.updateClusterMarker(info, marker)
                Log.d("Marker", "$info")
                Log.d("Marker", "$marker")
                marker.zIndex = 0
                Log.d("Marker", "$marker")
            }
        })
            .leafMarkerUpdater(object : DefaultLeafMarkerUpdater() {
                override fun updateLeafMarker(info: LeafMarkerInfo, marker: Marker) {
                    super.updateLeafMarker(info, marker)
                    Log.d("Marker", "$info")
                    Log.d("Marker", "$marker")
                    val key = info.key as ItemKey
                    val imageMarker = ImageMarkerCoil(context)
                    imageMarker.visibility = View.INVISIBLE
                    mapView.addView(imageMarker)
                    imageMarker.loadImage(key.photoDetail.photoUri) {
                        imageMarker.viewTreeObserver.addOnGlobalLayoutListener(object :
                            ViewTreeObserver.OnGlobalLayoutListener {
                            override fun onGlobalLayout() {
                                if (imageMarker.isImageLoaded()) {
                                    val overlayImage = OverlayImage.fromView(imageMarker)
                                    marker.icon = overlayImage
                                    marker.zIndex = -1
                                    mapView.removeView(imageMarker)
                                    imageMarker.viewTreeObserver.removeOnGlobalLayoutListener(this)
                                }
                            }
                        })
                    }
                }
            })
            .build()
    }

    LaunchedEffect(photos.value) {
        Log.e("MapScreen", "LaunchedEffect ${photos.value.size}")
        cluster.addAll(photos.value.associate { photoDetail -> ItemKey(photoDetail) to null })
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
            mapView.onDestroy() // MapView의 리소스를 해제하여 메모리 누수를 방지
            cluster.clear()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // AndroidView를 MapView로 바로 설정
        AndroidView(factory = { mapView }, modifier = modifier.matchParentSize())

        LaunchedEffect(cluster) {
            delay(100) // 모든 뷰가 준비될 시간을 주기 위해 약간의 지연을 추가
            Log.e("MapScreen", "get")
            mapView.getMapAsync { naverMap ->
                cluster.map = naverMap
            }
        }
    }
}

@Composable
fun LoadingScreen() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White.copy(alpha = 0.7f))
            .focusable()
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(8.dp))
            Text("")
        }
    }
}
