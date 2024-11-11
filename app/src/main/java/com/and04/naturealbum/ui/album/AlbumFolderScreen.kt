package com.and04.naturealbum.ui.album

import android.graphics.Color.parseColor
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.and04.naturealbum.data.room.PhotoDetail
import com.and04.naturealbum.ui.component.AlbumLabel
import com.and04.naturealbum.ui.component.MyTopAppBar
import com.and04.naturealbum.ui.savephoto.UiState
import com.and04.naturealbum.ui.theme.NatureAlbumTheme

@Composable
fun AlbumFolderScreen(
    selectedAlbumLabel: Int = 0,
    onPhotoClick: (Int) -> Unit = {},
    albumFolderViewModel: AlbumFolderViewModel = hiltViewModel()
) {
    albumFolderViewModel.loadFolderData(selectedAlbumLabel)
    Scaffold(topBar = { MyTopAppBar() }) { innerPadding ->
        ItemContainer(
            innerPaddingValues = innerPadding,
            onPhotoClick
        )
    }
}

@Composable
private fun ItemContainer(
    innerPaddingValues: PaddingValues,
    onPhotoClick: (Int) -> Unit,
    albumFolderViewModel: AlbumFolderViewModel = hiltViewModel()
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
                        contentAlignment = Alignment.Center
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
                                Item(item = photoDetail, onPhotoClick = onPhotoClick)
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
    onPhotoClick: (Int) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onPhotoClick(item.id) }
    ) {
        Column {
            AsyncImage(
                model = item.photoUri,
                contentDescription = "", // TODO:
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .clip(RoundedCornerShape(28.dp))
                    .fillMaxWidth()
            )
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