package com.and04.naturealbum.ui.album

import android.graphics.Color.parseColor
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.and04.naturealbum.R
import com.and04.naturealbum.data.room.PhotoDetail
import com.and04.naturealbum.ui.component.AlbumLabel
import com.and04.naturealbum.ui.component.MyTopAppBar
import com.and04.naturealbum.ui.savephoto.UiState
import com.and04.naturealbum.ui.theme.NatureAlbumTheme

@Composable
fun AlbumFolderScreen(
    selectedAlbumLabel: Int = 0,
    onPhotoClick: (Int) -> Unit = {},
    albumFolderViewModel: AlbumFolderViewModel = hiltViewModel(),
) {
    albumFolderViewModel.loadFolderData(selectedAlbumLabel)

    val editMode = remember { mutableStateOf(false) }
    val checkList = remember { mutableStateOf(setOf<PhotoDetail>()) }
    if (!editMode.value) checkList.value = setOf()

    Scaffold(topBar = { MyTopAppBar() }) { innerPadding ->
        ItemContainer(
            innerPaddingValues = innerPadding,
            onPhotoClick = onPhotoClick,
            editMode = editMode,
            checkList = checkList,
        )
    }
    BackHandler(enabled = editMode.value) {
        if (editMode.value) {
            editMode.value = false
        }
    }
}

@Composable
private fun ItemContainer(
    innerPaddingValues: PaddingValues,
    onPhotoClick: (Int) -> Unit,
    editMode: MutableState<Boolean>,
    checkList: MutableState<Set<PhotoDetail>>,
    albumFolderViewModel: AlbumFolderViewModel = hiltViewModel(),
) {
    val uiState = albumFolderViewModel.uiState.collectAsState()
    val label = albumFolderViewModel.label.collectAsState()
    val photoDetails = albumFolderViewModel.photoDetails.collectAsState()

    when (uiState.value) {
        is UiState.Loading, UiState.Idle -> {
            //TODO Loading
        }

        is UiState.Success -> {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPaddingValues)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AlbumLabel(
                        modifier = Modifier
                            .background(
                                color = Color(parseColor("#${label.value.backgroundColor}")),
                                shape = CircleShape
                            )
                            .fillMaxWidth(0.9f),
                        text = label.value.name,
                        backgroundColor = Color(parseColor("#${label.value.backgroundColor}"))
                    )
                    Box(
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        LazyVerticalStaggeredGrid(
                            columns = StaggeredGridCells.Fixed(2),
                            state = rememberLazyStaggeredGridState(),
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = PaddingValues(24.dp),
                            horizontalArrangement = Arrangement.spacedBy(28.dp),
                            verticalItemSpacing = 16.dp
                        ) {
                            items(photoDetails.value) { photoDetail ->
                                Item(
                                    item = photoDetail,
                                    onPhotoClick = onPhotoClick,
                                    editMode = editMode,
                                    checkList = checkList,
                                )
                            }
                        }
                        if (editMode.value) {
                            Button(
                                onClick = {
                                    Log.d("FFFF", "${checkList.value.joinToString(",")}")
                                    editMode.value = false
                                },
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(16.dp) // 버튼 여백 설정
                            ) {
                                Text(stringResource(R.string.album_folder_screen_save_button))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Item(
    item: PhotoDetail,
    onPhotoClick: (Int) -> Unit,
    editMode: MutableState<Boolean>,
    checkList: MutableState<Set<PhotoDetail>>,
) {
    var isSelected by remember { mutableStateOf(false) }
    if (!editMode.value && isSelected) {
        isSelected = false
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onPhotoClick(item.id) }
    ) {
        Column {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(28.dp))
                    .fillMaxWidth()
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onLongPress = { editMode.value = !editMode.value },
                            onTap = {
                                if (editMode.value) {
                                    isSelected = !isSelected
                                    if (isSelected) {
                                        checkList.value += item
                                    } else {
                                        checkList.value -= item
                                    }
                                }
                            })
                    }
            ) {
                AsyncImage(
                    model = item.photoUri,
                    contentDescription = stringResource(R.string.album_folder_screen_item_image_description),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxWidth(),
                )

                if (editMode.value && isSelected) {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(color = Color.Black.copy(alpha = 0.5f)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = stringResource(R.string.album_folder_screen_item_select_icon_description),
                            tint = MaterialTheme.colorScheme.surface,
                        )
                    }
                }
            }
            Text(
                text = item.description,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
@Preview
private fun ContentPreview() {
    NatureAlbumTheme {
        AlbumFolderScreen()
    }
}

data class TmpItem(
    val id: Int,
    val img: Int,
    val descroption: String,
)

val tmpItems = listOf(
    TmpItem(
        id = 1,
        img = R.drawable.cat_dummy,
        descroption = "사진 11111111111111111111111111111111111111111111",
    ),
    TmpItem(
        id = 2,
        img = R.drawable.sample_image_01,
        descroption = "사진 22222",
    ),
    TmpItem(
        id = 3,
        img = R.drawable.sample_image_06,
        descroption = "사진 3333",
    ),
    TmpItem(
        id = 4,
        img = R.drawable.sample_image_03,
        descroption = "사진 4444",
    ),
    TmpItem(
        id = 5,
        img = R.drawable.sample_image_06,
        descroption = "사진 5555",
    ),
    TmpItem(
        id = 6,
        img = R.drawable.sample_image_05,
        descroption = "6",
    ),
    TmpItem(
        id = 7,
        img = R.drawable.sample_image_06,
        descroption = "7",
    ),
    TmpItem(
        id = 8,
        img = R.drawable.sample_image_07,
        descroption = "8",
    ),
    TmpItem(
        id = 9,
        img = R.drawable.sample_image_06,
        descroption = "9",
    ),
    TmpItem(
        id = 10,
        img = R.drawable.sample_image_07,
        descroption = "10",
    ),
    TmpItem(
        id = 11,
        img = R.drawable.sample_image_06,
        descroption = "11",
    ),
    TmpItem(
        id = 12,
        img = R.drawable.sample_image_07,
        descroption = "12",
    ),
    TmpItem(
        id = 13,
        img = R.drawable.sample_image_06,
        descroption = "13",
    ),
    TmpItem(
        id = 14,
        img = R.drawable.sample_image_07,
        descroption = "14",
    ),
    TmpItem(
        id = 15,
        img = R.drawable.sample_image_06,
        descroption = "15",
    ),
)
