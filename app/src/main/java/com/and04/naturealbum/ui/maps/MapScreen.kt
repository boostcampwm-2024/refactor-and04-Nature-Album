package com.and04.naturealbum.ui.maps

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.naver.maps.map.MapView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalLifecycleOwner

@Composable
fun MapScreen(modifier: Modifier = Modifier) {
    // 현재 Context를 가져와 MapView 초기화
    val context = LocalContext.current
    // 현재 LifecycleOwner를 가져와 생명주기 관리에 사용
    val lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current

    // MapView를 Compose에서 사용할 수 있도록 rememberSaveable을 사용하여 상태를 관리
    // 구성 변경 시에도 MapView가 재생성되지 않고 유지
    // TODO: ID는 ids 파일로 별도 관리할 예정
    val MAP_VIEW_ID = 12345

    val mapView = remember {
        MapView(context).apply {
            id = MAP_VIEW_ID // MapView에 고유한 ID를 설정
        }
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
                else -> Unit
            }
        }
        // Lifecycle에 Observer를 추가하여 생명주기를 관찰
        lifecycle.addObserver(observer)

        // DisposableEffect가 해제될 때 Observer를 제거하고 MapView의 리소스를 해제
        onDispose {
            lifecycle.removeObserver(observer)
            mapView.onDestroy() // MapView의 리소스를 해제하여 메모리 누수를 방지
        }
    }

    // AndroidView를 사용하여 Compose 내에 MapView를 표시
    AndroidView(factory = { mapView }, modifier = modifier) {
        // 필요한 경우 추가적인 설정을 업데이트
        it.getMapAsync { naverMap ->
            // TODO: 지도가 준비된 후 추가 설정을 수행
            naverMap.minZoom = 10.0
            naverMap.maxZoom = 18.0
        }
    }
}
