package com.and04.naturealbum.ui.maps

import android.util.Log
import android.view.ViewTreeObserver
import android.widget.ImageView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.ImageLoader
import coil3.request.ImageRequest
import coil3.request.target
import coil3.request.transformations
import coil3.transform.CircleCropTransformation
import com.and04.naturealbum.R
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.MapView
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import kotlinx.coroutines.delay

@Composable
fun MapScreenGood(
    modifier: Modifier = Modifier,
    viewModel: MapScreenViewModel = hiltViewModel(),
) {
    val photos = viewModel.photos.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val lifecycleOwner: LifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current

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
                else -> {
                    Log.d("MapViewLifecycle", "Other Event: $event")
                }
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
        it.getMapAsync { naverMap ->
            naverMap.minZoom = 10.0
            naverMap.maxZoom = 18.0

            photos.value.map { photoDetail ->

                // ImageMarker 사용
                val imageMarker = ImageMarker(context, photoDetail.photoUri)
                // imageMarker = ImageMarker(context, "https://cdn.travie.com/news/photo/first/201710/img_19975_1.jpg")
                val marker = Marker().apply {
                    position = LatLng(photoDetail.latitude, photoDetail.longitude)
                    icon = OverlayImage.fromView(imageMarker) // ImageMarker를 아이콘으로 설정
                    width = 72
                    height = 100
                    map = naverMap
                }
            }
        }
    }
}


@Composable
fun MapScreen(
    modifier: Modifier = Modifier,
    viewModel: MapScreenViewModel = hiltViewModel(),
) {
    val photos = viewModel.photos.collectAsStateWithLifecycle()
    // 현재 Context를 가져와 MapView 초기화
    val context = LocalContext.current
    // 현재 LifecycleOwner를 가져와 생명주기 관리에 사용
    val lifecycleOwner: LifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current

    // MapView를 Compose에서 사용할 수 있도록 rememberSaveable을 사용하여 상태를 관리
    // 구성 변경 시에도 MapView가 재생성되지 않고 유지
    val mapView = remember {
        MapView(context).apply {
            id = R.id.map_view_id
        }
    }

    // 상태 기반으로 imageMarkers 리스트 관리
    val imageMarkers = remember { mutableStateListOf<ImageMarkerCoil>() }
    val markersList = remember { mutableStateListOf<Marker>() }
    var markersReady by remember { mutableStateOf(false) }

// photos가 비어 있지 않을 때만 LaunchedEffect 실행
    if (photos.value.isNotEmpty()) {
        LaunchedEffect(photos.value) {
            markersReady = false  // 로딩 시작
            imageMarkers.clear()
            markersList.clear()

            Log.d("MapScreen", "Photos size: ${photos.value.size}")
            var loadedImagesCount = 0

            photos.value.forEach { photoDetail ->
                val imageMarker = ImageMarkerCoil(context, photoDetail.photoUri)
                mapView.addView(imageMarker) // 객체를 생성하고 MapView에 추가
                imageMarkers.add(imageMarker) // imageMarkers 리스트에 추가

                // 이미지 로딩 시작
                imageMarker.loadImage {
                    loadedImagesCount++

                    imageMarker.viewTreeObserver.addOnGlobalLayoutListener {
                        if (imageMarker.height > 0 && imageMarker.width > 0) {
                            val overlayImage = OverlayImage.fromView(imageMarker)
                            val marker = Marker().apply {
                                position = LatLng(photoDetail.latitude, photoDetail.longitude)
                                icon = overlayImage
                                width = 72
                                height = 100
                            }

                            markersList.add(marker)

                            imageMarker.postDelayed({
                                mapView.removeView(imageMarker)
                            }, 0)


                            if (loadedImagesCount == photos.value.size) {
                                markersReady = true  // 모든 이미지 로드 완료 후 상태 업데이트
                                Log.d(
                                    "MapScreen",
                                    "---------------------All images loaded. markersReady: $markersReady"
                                )
                            }
                        }

                    }


                }
            }

            Log.d("MapScreen", "ImageMarkers size after adding: ${imageMarkers.size}")
            //markersReady = imageMarkers.size == photos.value.size  // 준비 완료 상태 업데이트
            Log.d(
                "MapScreen",
                "마커 준비 됐니?: ${markersReady} / imageMarkers.size : ${imageMarkers.size} / Photos size: ${photos.value.size}"
            )
        }
    }

    // MapView의 생명주기를 관리하기 위해 DisposableEffect를 사용
    DisposableEffect(lifecycleOwner) {
        // 현재 LifecycleOwner의 Lifecycle을 가져오기
        val lifecycle = lifecycleOwner.lifecycle
        // Lifecycle 이벤트를 관찰하는 Observer를 생성
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_CREATE -> {
                    Log.d("MapViewLifecycle", "ON_CREATE")
                    mapView.onCreate(null)
                }

                Lifecycle.Event.ON_START -> {
                    Log.d("MapViewLifecycle", "ON_START")
                    mapView.onStart()
                }

                Lifecycle.Event.ON_RESUME -> {
                    Log.d("MapViewLifecycle", "ON_RESUME")
                    mapView.onResume()
                }

                Lifecycle.Event.ON_PAUSE -> {
                    Log.d("MapViewLifecycle", "ON_PAUSE")
                    mapView.onPause()
                }

                Lifecycle.Event.ON_STOP -> {
                    Log.d("MapViewLifecycle", "ON_STOP")
                    mapView.onStop()
                }

                Lifecycle.Event.ON_DESTROY -> {
                    Log.d("MapViewLifecycle", "ON_DESTROY")
                    mapView.onDestroy()
                }

                else -> {
                    Log.d("MapViewLifecycle", "Other Event: $event")
                }
            }
        }
        // Lifecycle에 Observer를 추가하여 생명주기를 관찰
        lifecycle.addObserver(observer)

        // DisposableEffect가 해제될 때 Observer를 제거하고 MapView의 리소스를 해제
        onDispose {
            lifecycle.removeObserver(observer)
            Log.d("MapViewLifecycle", "onDispose - MapView onDestroy")
            mapView.onDestroy() // MapView의 리소스를 해제하여 메모리 누수를 방지
            // MapView에서 모든 imageMarkers 제거
            imageMarkers.forEach { mapView.removeView(it) }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // AndroidView를 MapView로 바로 설정
        AndroidView(factory = { mapView }, modifier = modifier.matchParentSize())

        if (markersReady) {
            LaunchedEffect(markersReady) {
                delay(100) // 모든 뷰가 준비될 시간을 주기 위해 약간의 지연을 추가
                mapView.getMapAsync { naverMap ->
                    naverMap.minZoom = 10.0
                    naverMap.maxZoom = 18.0
                    markersList.forEach { marker -> marker.map = naverMap }
                }
            }
        }

        if (!markersReady) {
            LoadingScreen()
        }
    }

//
//    // AndroidView를 사용하여 Compose 내에 MapView를 표시
//    AndroidView(factory = { mapView }, modifier = modifier) {
//        //////////////////// 아래의 업데이트는 백그라운드로 빼도 됨
//        // 필요한 경우 추가적인 설정을 업데이트
//        it.getMapAsync { naverMap ->
//            // TODO: 지도가 준비된 후 추가 설정을 수행
//            naverMap.minZoom = 10.0
//            naverMap.maxZoom = 18.0
//
//            photos.value.map { photoDetail ->
//
////                // Load the image into an ImageView using Coil
////                val imageView = ImageView(context).apply {
////                    val request = ImageRequest.Builder(context)
////                        .data(photoDetail.photoUri)
////                        .transformations(CircleCropTransformation()) // Optional: Circle crop for rounded images
////                        .target(this)
////                        .listener(
////                            onStart = {
////                                Log.e("Coil", "이미지 로드 시작: ${photoDetail.photoUri}")
////                            },
////                            onSuccess = { _, _ ->
////                                Log.d("Coil", "이미지 로드 성공: ${photoDetail.photoUri}")
////                            },
////                            onError = { _, throwable ->
////                                Log.e("Coil", "이미지 로드 실패: ${throwable}")
////                            }
////                        )
////                        .build()
////
////                    ImageLoader(context).enqueue(request).apply {
////                        Log.d("Coil", "Request added job: ${this.job}")
////                        Log.d("Coil", "Request added isDisposed: ${this.isDisposed}")
////                    }
////                }
//
////                // Use the ImageView as an OverlayImage for the Marker
////                Marker().apply {
////                    position = LatLng(photoDetail.latitude, photoDetail.longitude)
////                    icon =
////                        OverlayImage.fromView(imageView) // Set the ImageView with loaded image as icon
////                    icon = OverlayImage.fromResource(R.drawable.btn_camera_background)
////                    width = 72
////                    height = 100
////                    map = naverMap
////                }
//
////
////                Marker().apply {
////                    position = LatLng(photoDetail.latitude, photoDetail.longitude)
////                    icon = OverlayImage.fromView(ImageMarkerCoil(context, photoDetail.photoUri))
////                    width = 72
////                    height = 100
////                    map = naverMap
////                }
////////////////////////////////////////////////////////////
////                val imageMarker = ImageMarkerCoil(context, photoDetail.photoUri)
////
////// MapView에 imageMarker를 부착하여 윈도우에 연결
////                mapView.addView(imageMarker)
////
////// 마커에 적용
////                imageMarker.loadImage {
////                    val overlayImage = OverlayImage.fromView(imageMarker)
////
////                    Marker().apply {
////                        position = LatLng(photoDetail.latitude, photoDetail.longitude)
////                        //position = LatLng(35.2093663, 129.0444894)
////                        icon = overlayImage
////                        width = 72
////                        height = 100
////                        map = naverMap
////                    }
////                }
//                // imageMarkers를 통해 마커를 추가
//                photos.value.forEachIndexed { index, photoDetail2 ->
//                    val overlayImage = OverlayImage.fromView(imageMarkers[index])
//
//                    Marker().apply {
//                        position = LatLng(photoDetail2.latitude, photoDetail2.longitude)
//                        icon = overlayImage
//                        width = 72
//                        height = 100
//                        map = naverMap
//                    }
//                }
////////////////////////////////////////////////////////////
////// 필요시 나중에 mapView에서 imageMarker 제거 가능
////                imageMarker.postDelayed({
////                    mapView.removeView(imageMarker)
////                }, 2000)
//
//
//
//            }
//
//        }
//
//    }
}

@Composable
fun LoadingScreen() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White.copy(alpha = 0.7f))
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(8.dp))
            Text("Loading map markers...")
        }
    }
}


