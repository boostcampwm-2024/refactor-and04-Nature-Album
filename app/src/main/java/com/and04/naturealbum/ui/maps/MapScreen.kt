package com.and04.naturealbum.ui.maps

import android.annotation.SuppressLint
import android.location.Location
import android.view.Gravity
import android.view.View
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Diversity3
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.ImageNotSupported
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import coil3.compose.SubcomposeAsyncImage
import coil3.compose.SubcomposeAsyncImageContent
import coil3.request.ImageRequest
import coil3.request.placeholder
import com.and04.naturealbum.R
import com.and04.naturealbum.data.dto.FirebaseFriend
import com.and04.naturealbum.ui.component.LoadingIcons
import com.and04.naturealbum.ui.component.PartialBottomSheet
import com.and04.naturealbum.ui.component.RotatingImageLoading
import com.and04.naturealbum.ui.mypage.UserManager
import com.and04.naturealbum.utils.toColor
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapView
import com.naver.maps.map.NaverMap
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.Overlay
import com.naver.maps.map.overlay.OverlayImage

private const val USER_SELECT_MAX = 4

@SuppressLint("NewApi")
@Composable
fun MapScreen(
    location: Location? = null,
    modifier: Modifier = Modifier,
    viewModel: MapScreenViewModel = hiltViewModel(),
) {
    val friends = viewModel.friends.collectAsStateWithLifecycle()
    val openDialog = remember { mutableStateOf(false) }

    val myPhotos = viewModel.photos.collectAsStateWithLifecycle()
    val friendsPhotos = viewModel.friendsPhotos.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current

    val marker = remember { Marker() }
    var pick by remember { mutableStateOf<PhotoItem?>(null) }
    val displayPhotos = remember { mutableStateOf(listOf<PhotoItem>()) }

    val clusterManagers: List<ClusterManager> = remember {
        ColorRange.entries.map { colorRange ->
            ClusterManager(
                colorRange = colorRange,
                onMarkerClick = { info ->
                    Overlay.OnClickListener {
                        displayPhotos.value = info.tag as List<PhotoItem>
                        pick = displayPhotos.value
                            .groupBy { photoItem -> photoItem.label }
                            .maxBy { (_, photoItems) -> photoItems.size }.value
                            .maxBy { photoItem -> photoItem.time }
                        true
                    }
                },
                onClusterChange = { info ->
                    val changedCluster = info.tag as List<PhotoItem>
                    if (changedCluster.contains(pick)) displayPhotos.value = changedCluster
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
                naverMap.onMapClickListener = NaverMap.OnMapClickListener { _, _ ->
                    displayPhotos.value = emptyList()
                    pick = null
                }
                val uiSettings = naverMap.uiSettings
                uiSettings.logoGravity = Gravity.TOP or Gravity.START
                uiSettings.setLogoMargin(150, 25, 0, 0)
            }
        }
    }

    val imageMarker = remember {
        ImageMarker(context).apply {
            visibility = View.INVISIBLE
            mapView.addView(this)
        }
    }

    BackHandler(
        enabled = (pick != null)
    ) {
        pick = null
        displayPhotos.value = emptyList()
    }

    LaunchedEffect(pick) {
        mapView.getMapAsync { naverMap ->
            marker.map = pick?.let { pick ->
                imageMarker.loadImage(pick.uri) {
                    marker.icon = OverlayImage.fromView(imageMarker)
                }
                marker.position = pick.position
                naverMap
            }
        }
    }

    LaunchedEffect(myPhotos.value) {
        clusterManagers[0].setPhotoItems(myPhotos.value)
    }

    LaunchedEffect(friendsPhotos.value) {
        clusterManagers.drop(1).forEachIndexed { index, cluster ->
            cluster.setPhotoItems(friendsPhotos.value.getOrNull(index) ?: emptyList())
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
                else -> {}
            }
        }
        // Lifecycle에 Observer를 추가하여 생명주기를 관찰
        lifecycle.addObserver(observer)

        // DisposableEffect가 해제될 때 Observer를 제거하고 MapView의 리소스를 해제
        onDispose {
            lifecycle.removeObserver(observer)
            clusterManagers.forEach { cluster ->
                cluster.clear()
            }
            mapView.onDestroy() // MapView의 리소스를 해제하여 메모리 누수를 방지
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // AndroidView를 MapView로 바로 설정
        AndroidView(factory = { mapView }, modifier = modifier.fillMaxSize()) {
            mapView.getMapAsync { NaverMap ->
                location?.let { position ->
                    val cameraUpdate =
                        CameraUpdate.scrollTo(LatLng(position.latitude, position.longitude))
                    NaverMap.moveCamera(cameraUpdate)
                }
            }
        }

        if (UserManager.isSignIn()) {
            IconButton(
                onClick = {
                    viewModel.fetchFriends(UserManager.getUser()!!.uid)
                    openDialog.value = true
                },
                modifier = Modifier
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
            isVisible = displayPhotos.value.isNotEmpty(),
            modifier = modifier.padding(horizontal = 16.dp),
            fullExpansionSize = 0.95f
        ) {
            PhotoGrid(
                photos = displayPhotos,
                modifier = modifier,
                onPhotoClick = { photo -> pick = photo }
            )
        }
    }

    FriendDialog(
        isOpen = openDialog,
        friends = friends,
        onDismiss = { openDialog.value = false },
        onConfirm = { selectedFriends ->
            viewModel.fetchFriendsPhotos(selectedFriends.map { friend -> friend.user.uid })
            openDialog.value = false
        }
    )
}

@Composable
fun FriendDialog(
    isOpen: State<Boolean> = remember { mutableStateOf(true) },
    friends: State<List<FirebaseFriend>> = remember { mutableStateOf(emptyList()) },
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit = {},
    onConfirm: (List<FirebaseFriend>) -> Unit = {}
) {
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    var selectedFriends by remember { mutableStateOf<List<FirebaseFriend>>(emptyList()) }
    if (isOpen.value) {
        Dialog(
            onDismissRequest = { onDismiss() },
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .sizeIn(maxHeight = screenHeight * 0.7f),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ),
                shape = RoundedCornerShape(16.dp),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Text(
                        text = stringResource(R.string.map_friend_dialog_title),
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Text(
                        text = stringResource(R.string.map_friend_dialog_body),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                LazyColumn(
                    modifier = modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                ) {
                    items(friends.value) { friend ->
                        FriendDialogItem(friend = friend,
                            isSelect = selectedFriends.contains(friend),
                            onSelect = {
                                if (selectedFriends.contains(friend)) {
                                    selectedFriends = selectedFriends.filter { it != friend }
                                } else if (selectedFriends.size < USER_SELECT_MAX) {
                                    selectedFriends = selectedFriends + friend
                                }
                            })
                        HorizontalDivider()
                    }
                }

                Row(
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = { onDismiss() }
                    ) {
                        Text(
                            text = stringResource(R.string.map_friend_dialog_cancel_btn),
                            style = MaterialTheme.typography.labelLarge,
                        )
                    }

                    Spacer(modifier = Modifier.size(8.dp))

                    TextButton(
                        onClick = { onConfirm(selectedFriends) }
                    ) {
                        Text(
                            text = stringResource(R.string.map_friend_dialog_confirm_btn),
                            style = MaterialTheme.typography.labelLarge,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FriendDialogItem(
    friend: FirebaseFriend,
    isSelect: Boolean,
    modifier: Modifier = Modifier,
    onSelect: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            modifier = modifier
                .size(40.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop,
            model = friend.user.photoUrl,
            contentDescription = friend.user.displayName
        )
        Text(
            modifier = modifier.weight(1f),
            text = friend.user.displayName,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.bodyLarge
        )
        Checkbox(
            checked = isSelect,
            colors = CheckboxDefaults.colors().copy(
                uncheckedBoxColor = MaterialTheme.colorScheme.primary,
                uncheckedBorderColor = MaterialTheme.colorScheme.primary,
            ),
            onCheckedChange = { onSelect() }
        )
    }
}

@Composable
fun PhotoGrid(
    photos: State<List<PhotoItem>>,
    columnCount: Int = 3,
    modifier: Modifier = Modifier,
    onPhotoClick: (PhotoItem) -> Unit,
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
                        SubcomposeAsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(photo.uri)
                                .placeholder(R.drawable.ic_image)
                                .build(),
                            contentDescription = photo.label.name,
                            modifier = Modifier
                                .wrapContentSize(Alignment.Center)
                                .aspectRatio(1f)
                                .weight(1f)
                                .clip(MaterialTheme.shapes.medium)
                                .clickable { onPhotoClick(photo) },
                            contentScale = ContentScale.Crop,
                        ) {
                            val state by painter.state.collectAsState()
                            when (state) {
                                is AsyncImagePainter.State.Loading -> RotatingImageLoading(
                                    drawableRes = LoadingIcons.entries.random().id,
                                    stringRes = null,
                                )

                                is AsyncImagePainter.State.Success -> SubcomposeAsyncImageContent()
                                is AsyncImagePainter.State.Empty -> Icon(
                                    imageVector = Icons.Outlined.Image,
                                    contentDescription = stringResource(R.string.map_image_loading)
                                )

                                is AsyncImagePainter.State.Error -> Icon(
                                    imageVector = Icons.Outlined.ImageNotSupported,
                                    contentDescription = stringResource(R.string.map_image_load_fail)
                                )
                            }
                        }
                    }
                    repeat(columnCount - row.size) {
                        Box(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}
