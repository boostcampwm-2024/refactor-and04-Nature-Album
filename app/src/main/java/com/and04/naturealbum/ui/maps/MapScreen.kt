package com.and04.naturealbum.ui.maps

import android.annotation.SuppressLint
import android.graphics.PointF
import android.view.Gravity
import android.view.View
import androidx.annotation.IntRange
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.and04.naturealbum.R
import com.and04.naturealbum.data.room.Label
import com.and04.naturealbum.data.room.PhotoDetail
import com.and04.naturealbum.ui.component.BottomSheetState
import com.and04.naturealbum.ui.component.PartialBottomSheet
import com.and04.naturealbum.utils.toColor
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.MapView
import com.naver.maps.map.NaverMap
import com.naver.maps.map.clustering.ClusterMarkerInfo
import com.naver.maps.map.clustering.Clusterer
import com.naver.maps.map.clustering.DefaultClusterMarkerUpdater
import com.naver.maps.map.clustering.DefaultLeafMarkerUpdater
import com.naver.maps.map.clustering.DefaultMarkerManager
import com.naver.maps.map.clustering.LeafMarkerInfo
import com.naver.maps.map.clustering.MarkerInfo
import com.naver.maps.map.overlay.Align
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.Overlay
import com.naver.maps.map.overlay.OverlayImage


fun sizeToTint(
    size: Int,
    min: Color = Color(10,0,0),
    max: Color = Color(255,0,0),
    @IntRange(from = 1) threshold: Int = 20
): Int {
    val t = minOf(size, threshold)
    val r = min.red + t * (max.red - min.red) * 255 / threshold
    val g = min.green + t * (max.green - min.green) * 255 / threshold
    val b = min.blue + t * (max.blue - min.blue) * 255 / threshold
    return (r.toInt() shl 16) or (g.toInt() shl 8) or b.toInt()
}

@SuppressLint("NewApi")
@Composable
fun MapScreen(
    modifier: Modifier = Modifier,
    viewModel: MapScreenViewModel = hiltViewModel(),
) {
    val photos = viewModel.photos.collectAsStateWithLifecycle()
    val labels = viewModel.labels.collectAsStateWithLifecycle()
    val idToPhoto = remember { mutableMapOf<Int, PhotoDetail>() } // id와 PhotoDetail 매핑
    val context = LocalContext.current
    val lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
    val marker = remember { Marker() }

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

    var pick by remember { mutableStateOf<PhotoDetail?>(null) }
    var photoDetailIds by remember { mutableStateOf(listOf<Int>()) }
    val displayPhotos = remember { mutableStateOf(listOf<PhotoDetail>()) }

    LaunchedEffect(photoDetailIds) {
        displayPhotos.value = photoDetailIds.map { labelId -> idToPhoto[labelId]!! }
    }

    LaunchedEffect(pick) {
        mapView.getMapAsync { naverMap ->
            marker.map = pick?.let { pick ->
                imageMarker.loadImage(pick.photoUri)
                marker.position = LatLng(pick.latitude, pick.longitude)
                naverMap
            }
        }
    }

    val clusterImage = OverlayImage.fromResource(R.drawable.ic_cluster)

    val cluster: Clusterer<PhotoKey> = remember {
        val onClickMarker: (MarkerInfo) -> Overlay.OnClickListener = { info ->
            Overlay.OnClickListener {
                photoDetailIds = info.tag as List<Int>
                pick = photoDetailIds.map { labelId -> idToPhoto.getValue(labelId) }
                    .groupBy { photoDetail -> photoDetail.labelId }
                    .maxBy { (_, photos) -> photos.size }.value
                    .maxBy { photoDetail -> photoDetail.datetime }
                true
            }
        }
        Clusterer.ComplexBuilder<PhotoKey>().tagMergeStrategy { cluster ->
            cluster.children.flatMap { node -> node.tag as List<*> }
        }.clusterMarkerUpdater(object : DefaultClusterMarkerUpdater() {
            override fun updateClusterMarker(info: ClusterMarkerInfo, marker: Marker) {
                if ((info.tag as List<Int>).contains(pick?.id))
                    photoDetailIds = info.tag as List<Int>
                marker.captionText = info.size.toString()
                marker.iconTintColor = sizeToTint(info.size)
                marker.onClickListener = onClickMarker(info)
            }
        }).leafMarkerUpdater(object : DefaultLeafMarkerUpdater() {
            override fun updateLeafMarker(info: LeafMarkerInfo, marker: Marker) {
                if ((info.tag as List<Int>).contains(pick?.id))
                    photoDetailIds = info.tag as List<Int>
                marker.captionText = "1"
                marker.iconTintColor = sizeToTint(1)
                marker.onClickListener = onClickMarker(info)
            }
        }).markerManager(object : DefaultMarkerManager() {
            override fun createMarker(): Marker {
                return Marker().apply {
                    zIndex = -1
                    icon = clusterImage
                    isFlat = true
                    anchor = PointF(0.5f, 0.5f)
                    setCaptionAligns(Align.Center, Align.Center)
                    captionTextSize = 24f
                }
            }
        })
    }.build()

    LaunchedEffect(photos.value) {
        if (displayPhotos.value.isEmpty()) displayPhotos.value = photos.value
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
        AndroidView(factory = { mapView }, modifier = modifier.fillMaxSize()) {
            mapView.getMapAsync { naverMap ->
                cluster.map = naverMap
                naverMap.onMapClickListener = NaverMap.OnMapClickListener { _, _ ->
                    displayPhotos.value = photos.value
                    pick = null
                }
                val uiSettings = naverMap.uiSettings
                uiSettings.logoGravity = Gravity.TOP or Gravity.START
            }
        }

        PartialBottomSheet(
            initialState = BottomSheetState.Collapsed,
            modifier = modifier.padding(horizontal = 16.dp),
            fullExpansionSize = 0.95f
        ) {
            PhotoGrid(
                photos = displayPhotos,
                labels = labels.value,
                modifier = modifier,
                onPhotoClick = { photo -> pick = photo })
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PhotoGrid(
    photos: State<List<PhotoDetail>>,
    labels: List<Label>,
    columnCount: Int = 3,
    modifier: Modifier = Modifier,
    onPhotoClick: (PhotoDetail) -> Unit,
) {
    val labelIdToLabel = labels.associateBy { label -> label.id }
    val groupByLabel = photos
        .value
        .groupBy { photoDetail -> photoDetail.labelId }
        .toList()
        .sortedByDescending { (_, photoDetails) -> photoDetails.size }
        .map { (labelId, photoDetails) ->
            labelIdToLabel[labelId]!! to photoDetails.sortedByDescending { photoDetail -> photoDetail.datetime }
        }

    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        groupByLabel.forEach { (label, photos) ->
            stickyHeader {
                val backgroundColor = label.backgroundColor.toColor()
                SuggestionChip(
                    onClick = {},
                    label = { Text(text = label.name) },
                    modifier = modifier,
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        containerColor = backgroundColor,
                        labelColor = if (backgroundColor.luminance() > 0.5f) Color.Black else Color.White
                    ),
                )
            }

            items(photos.windowed(columnCount, columnCount, true)) { row ->
                Row(
                    modifier = modifier, horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    row.forEach { photo ->
                        AsyncImage(
                            model = photo.photoUri,
                            contentDescription = photo.description, // TODO: 해당 description 무엇으로 할지 확정
                            modifier = Modifier
                                .wrapContentSize(Alignment.Center)
                                .aspectRatio(1f)
                                .weight(1f)
                                .clip(MaterialTheme.shapes.medium)
                                .clickable { onPhotoClick(photo) },
                            contentScale = ContentScale.Crop,
                        )
                    }
                    repeat(columnCount - row.size) {
                        Box(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}
