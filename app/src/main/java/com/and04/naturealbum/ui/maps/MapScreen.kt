package com.and04.naturealbum.ui.maps

import android.annotation.SuppressLint
import android.view.Gravity
import android.view.View
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Diversity3
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
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
import com.and04.naturealbum.R
import com.and04.naturealbum.data.dto.FirebaseFriend
import com.and04.naturealbum.data.room.Label
import com.and04.naturealbum.ui.component.BottomSheetState
import com.and04.naturealbum.ui.component.PartialBottomSheet
import com.and04.naturealbum.ui.friend.FriendViewModel
import com.and04.naturealbum.ui.mypage.UserManager
import com.and04.naturealbum.utils.toColor
import com.naver.maps.map.MapView
import com.naver.maps.map.NaverMap
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.Overlay
import com.naver.maps.map.overlay.OverlayImage

private const val USER_SELECT_MAX = 4

@SuppressLint("NewApi")
@Composable
fun MapScreen(
    modifier: Modifier = Modifier,
    viewModel: MapScreenViewModel = hiltViewModel(),
    friendViewModel: FriendViewModel = hiltViewModel(),
) {
    val friends = friendViewModel.friends.collectAsStateWithLifecycle()
    val openAlertDialog = remember { mutableStateOf(false) }
    var showFriends by remember { mutableStateOf(emptyList<FirebaseFriend>()) }

    val myPhotos = viewModel.photos.collectAsStateWithLifecycle()
    val myLabels = viewModel.labels.collectAsStateWithLifecycle()

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

    var pick by remember { mutableStateOf<PhotoItem?>(null) }
    val displayPhotos = remember { mutableStateOf(listOf<PhotoItem>()) }

    LaunchedEffect(pick) {
        mapView.getMapAsync { naverMap ->
            marker.map = pick?.let { pick ->
                imageMarker.loadImage(pick.uri)
                marker.position = pick.position
                naverMap
            }
        }
    }

    val cluster: ClusterManager = remember {
        ClusterManager(
            colorRange = ColorRange.RED,
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

    LaunchedEffect(myPhotos.value, myLabels.value) {
        if (myLabels.value.isEmpty()) return@LaunchedEffect
        if (displayPhotos.value.isEmpty()) displayPhotos.value =
            myPhotos.value.toPhotoItems(myLabels.value)
        cluster.setPhotoItems(myPhotos.value.toPhotoItems(myLabels.value))
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
            mapView.onDestroy() // MapView의 리소스를 해제하여 메모리 누수를 방지
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // AndroidView를 MapView로 바로 설정
        AndroidView(factory = { mapView }, modifier = modifier.fillMaxSize()) {
            mapView.getMapAsync { naverMap ->
                cluster.setMap(naverMap)
                naverMap.onMapClickListener = NaverMap.OnMapClickListener { _, _ ->
                    displayPhotos.value = emptyList()
                    pick = null
                }
                val uiSettings = naverMap.uiSettings
                uiSettings.logoGravity = Gravity.TOP or Gravity.START
            }
        }

        if (UserManager.isSignIn()) {
            IconButton(
                onClick = {
                    friendViewModel.fetchFriends(UserManager.getUser()!!.uid)
                    openAlertDialog.value = true
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
                Icon(imageVector = Icons.Default.Diversity3, contentDescription = "친구 지도 보기")
            }
        }

        PartialBottomSheet(
            initialState = BottomSheetState.Collapsed,
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
        isOpen = openAlertDialog,
        friends = friends,
        prevSelectedFriends = showFriends,
        onDismiss = { openAlertDialog.value = false },
        onConfirm = { selectedFriends ->
            showFriends = selectedFriends
            openAlertDialog.value = false
        }
    )
}

@Composable
fun FriendDialog(
    isOpen: State<Boolean> = remember { mutableStateOf(true) },
    friends: State<List<FirebaseFriend>> = remember { mutableStateOf(emptyList()) },
    prevSelectedFriends: List<FirebaseFriend> = emptyList(),
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit = {},
    onConfirm: (List<FirebaseFriend>) -> Unit = {}
) {
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    var selectedFriends by remember { mutableStateOf(prevSelectedFriends) }

    if (isOpen.value) {
        Dialog(
            onDismissRequest = { onDismiss() },
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .sizeIn(maxHeight = screenHeight * 0.7f),
                shape = RoundedCornerShape(16.dp),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Text(
                        text = "친구 지도 같이 보기",
                    )
                    Text(
                        text = "친구의 지도를 함께 봐봅시다\n" + "총 4명의 친구 선택 가능해요!"
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

                    Button(onClick = { onDismiss() }) {
                        Text(text = "취소")
                    }

                    Spacer(modifier = Modifier.size(8.dp))

                    Button(onClick = { onConfirm(selectedFriends) }) {
                        Text(text = "적용")
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
            modifier = modifier.size(40.dp), model = friend.user.photoUrl, contentDescription = null
        )
        Text(
            modifier = modifier.weight(1f),
            text = friend.user.email,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Checkbox(checked = isSelect, onCheckedChange = { onSelect() })
    }
}

@OptIn(ExperimentalFoundationApi::class)
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
            stickyHeader {
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
                        AsyncImage(
                            model = photo.uri,
                            contentDescription = photo.label.name, // TODO: 해당 description 무엇으로 할지 확정
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
