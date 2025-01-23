package com.and04.naturealbum.ui.maps

import android.graphics.PointF
import android.view.Gravity
import android.view.View
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Diversity3
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.request.ImageRequest
import coil3.request.placeholder
import com.and04.naturealbum.R
import com.and04.naturealbum.data.dto.FirebaseFriend
import com.and04.naturealbum.ui.component.LoadingAsyncImage
import com.and04.naturealbum.ui.component.NetworkDisconnectContent
import com.and04.naturealbum.ui.component.PartialBottomSheet
import com.and04.naturealbum.ui.component.PhotoContent
import com.and04.naturealbum.ui.utils.UserManager
import com.and04.naturealbum.utils.color.toColor
import com.and04.naturealbum.utils.network.NetworkState
import com.and04.naturealbum.utils.network.NetworkViewModel
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.CameraAnimation
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapView
import com.naver.maps.map.NaverMap
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.Overlay
import com.naver.maps.map.overlay.OverlayImage

@Composable
fun MapScreen(
    navigateToHome: () -> Unit,
    modifier: Modifier = Modifier,
    userViewModel: MapScreenViewModel = hiltViewModel(),
    networkViewModel: NetworkViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current

    val networkState = networkViewModel.networkState.collectAsStateWithLifecycle()
    val friends = userViewModel.friends.collectAsStateWithLifecycle()

    val photosByUid = userViewModel.photosByUid.collectAsStateWithLifecycle()

    val showPhotoContent = remember { mutableStateOf(false) }

    val openDialog = remember { mutableStateOf(false) }
    val pick = remember { mutableStateOf<PhotoItem?>(null) }
    val marker = remember {
        Marker().apply {
            onClickListener = Overlay.OnClickListener {
                showPhotoContent.value = true
                true
            }
        }
    }
    val bottomSheetPhotos = remember { mutableStateOf(listOf<PhotoItem>()) }
    val selectedFriends = remember { mutableStateOf(listOf<FirebaseFriend>()) }

    val clusterManagers: List<ClusterManager> = remember {
        // 클러스터 매니저 5개 미리 생성
        ColorRange.entries.map { colorRange ->
            ClusterManager(
                colorRange = colorRange,
                onClusterClick = { info ->
                    Overlay.OnClickListener {
                        bottomSheetPhotos.value = info.tag as List<PhotoItem>
                        pick.value = bottomSheetPhotos.value
                            .groupBy { photoItem -> photoItem.label }
                            .maxBy { (_, photoItems) -> photoItems.size }.value
                            .maxBy { photoItem -> photoItem.time }
                        true
                    }
                },
                onClusterChange = { info ->
                    val changedCluster = info.tag as List<PhotoItem>
                    if (changedCluster.contains(pick.value)) bottomSheetPhotos.value =
                        changedCluster
                }
            )
        }
    }

    val mapView = remember {
        MapView(context).apply {
            id = R.id.map_view_id
            getMapAsync { naverMap ->
                clusterManagers.forEach { cluster ->
                    cluster.setMap(naverMap)
                }
                naverMap.maxZoom = 18.0
                naverMap.onMapClickListener = NaverMap.OnMapClickListener { _, _ ->
                    bottomSheetPhotos.value = emptyList()
                    pick.value = null
                }
                val uiSettings = naverMap.uiSettings
                uiSettings.logoGravity = Gravity.TOP or Gravity.END
                uiSettings.setLogoMargin(0, 16, 160, 0)
                uiSettings.isCompassEnabled = false
                uiSettings.isScaleBarEnabled = false
                uiSettings.isZoomControlEnabled = false
            }
        }
    }

    val cameraPivot = remember { mutableStateOf(PointF(0.5f, 0.5f)) }

    val imageMarker = remember {
        ImageMarker(context).apply {
            visibility = View.INVISIBLE
            mapView.addView(this)
        }
    }

    BackHandler(
        enabled = (pick.value != null)
    ) {
        pick.value = null
        bottomSheetPhotos.value = emptyList()
    }

    LaunchedEffect(cameraPivot.value) {
        mapView.getMapAsync { naverMap ->
            pick.value?.let { pick ->
                naverMap.moveCamera(
                    CameraUpdate.scrollTo(pick.position).pivot(cameraPivot.value)
                        .animate(CameraAnimation.Easing, 500)
                )
            }
        }
    }

    LaunchedEffect(pick.value) {
        mapView.getMapAsync { naverMap ->
            marker.map = pick.value?.let { pick ->
                naverMap.moveCamera(
                    CameraUpdate.scrollTo(pick.position).pivot(cameraPivot.value)
                        .animate(CameraAnimation.Easing, 500)
                )
                imageMarker.loadImage(pick.uri) {
                    marker.icon = OverlayImage.fromView(imageMarker)
                }
                marker.position = pick.position
                naverMap
            }
        }
    }

    LaunchedEffect(photosByUid.value) {
        clusterManagers.forEachIndexed { index, cluster ->
            cluster.setPhotoItems(
                photosByUid.value.keys.elementAtOrNull(index) ?: "",
                photosByUid.value.values.elementAtOrNull(index) ?: emptyList()
            )
        }

        pick.value = null
        bottomSheetPhotos.value = emptyList()

        val totalPhotos = photosByUid.value.values.flatten()
        if (totalPhotos.isNotEmpty()) {
            val bound = LatLngBounds.Builder().apply {
                totalPhotos.forEach { photoItem ->
                    include(photoItem.position)
                }
            }.build()
            mapView.getMapAsync { naverMap ->
                naverMap.moveCamera(
                    CameraUpdate.fitBounds(bound, 300).animate(CameraAnimation.Easing, 500)
                )
            }
        }
    }

    DisposableEffect(lifecycleOwner) {
        val lifecycle = lifecycleOwner.lifecycle

        val observer = object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                when (event) {
                    Lifecycle.Event.ON_CREATE -> mapView.onCreate(null)
                    Lifecycle.Event.ON_START -> mapView.onStart()
                    Lifecycle.Event.ON_RESUME -> mapView.onResume()
                    Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                    Lifecycle.Event.ON_STOP -> mapView.onStop()
                    Lifecycle.Event.ON_DESTROY -> {
                        clusterManagers.forEach { cluster ->
                            cluster.clear()
                        }
                        mapView.onDestroy()
                        lifecycle.removeObserver(this)
                    }

                    else -> {}
                }
            }
        }

        lifecycle.addObserver(observer)

        onDispose {
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        if (networkState.value == NetworkState.DISCONNECTED) {
            NetworkDisconnectContent()
        } else {
            // AndroidView를 MapView로 바로 설정
            AndroidView(factory = { mapView }, modifier = modifier.fillMaxSize())

            if (UserManager.isSignIn()) {
                IconButton(
                    onClick = {
                        userViewModel.fetchFriends(UserManager.getUser()!!.uid)
                        openDialog.value = true
                    },
                    modifier = modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(48.dp),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Diversity3,
                        contentDescription = stringResource(R.string.map_show_friend_map)
                    )
                }
            }

            PartialBottomSheet(
                isVisible = bottomSheetPhotos.value.isNotEmpty(),
                onCollapsed = { isCollapsed ->
                    cameraPivot.value = if (isCollapsed) PointF(0.5f, 0.5f) else PointF(0.5f, 0.3f)
                },
                modifier = modifier.padding(horizontal = 16.dp),
                fullExpansionSize = 0.95f
            ) {
                PhotoGrid(
                    photos = bottomSheetPhotos,
                    modifier = modifier,
                    onPhotoClick = { photo -> pick.value = photo },
                    onPhotoDoubleClick = { photo ->
                        pick.value = photo
                        showPhotoContent.value = true
                    }
                )
            }

            FriendDialog(
                isOpen = openDialog,
                friends = friends,
                selectedFriends = selectedFriends,
                onDismiss = { openDialog.value = false },
                onConfirm = { friends ->
                    selectedFriends.value = friends
                    userViewModel.fetchFriendsPhotos(friends.map { friend -> friend.user.uid })
                    openDialog.value = false
                }
            )
            if (showPhotoContent.value) {
                PhotoContent(
                    imageUri = pick.value!!.uri,
                    contentDescription = pick.value!!.label.name,
                    onDismiss = { showPhotoContent.value = false }
                )
            }
        }
        IconButton(
            onClick = navigateToHome,
            modifier = modifier
                .size(48.dp)
                .align(Alignment.TopStart),
        ) {
            Icon(
                modifier = modifier.size(24.dp),
                imageVector = Icons.AutoMirrored.Default.ArrowBack,
                contentDescription = stringResource(R.string.map_arrow_back_button),
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun PhotoGrid(
    photos: State<List<PhotoItem>>,
    columnCount: Int = 3,
    modifier: Modifier = Modifier,
    onPhotoClick: (PhotoItem) -> Unit,
    onPhotoDoubleClick: (PhotoItem) -> Unit,
) {
    val groupByLabel = photos
        .value
        .groupBy { photoItem -> photoItem.label }
        .toList()
        .sortedByDescending { (_, photoItem) -> photoItem.size }

    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        groupByLabel.forEach { (label, photos) ->
            item {
                val backgroundColor = label.color.toColor()
                SuggestionChip(
                    onClick = {},
                    label = {
                        Text(
                            text = label.name,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
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
                        LoadingAsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(photo.uri)
                                .placeholder(R.drawable.ic_image)
                                .build(),
                            contentDescription = photo.label.name,
                            modifier = modifier
                                .wrapContentSize(Alignment.Center)
                                .aspectRatio(1f)
                                .weight(1f)
                                .clip(MaterialTheme.shapes.medium)
                                .combinedClickable(
                                    onClick = { onPhotoClick(photo) },
                                    onDoubleClick = { onPhotoDoubleClick(photo) },
                                ),
                            contentScale = ContentScale.Crop,
                        )
                    }
                    repeat(columnCount - row.size) {
                        Box(modifier = modifier.weight(1f))
                    }
                }
            }
        }
    }
}
