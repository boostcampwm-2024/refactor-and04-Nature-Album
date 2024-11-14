package com.and04.naturealbum.ui.album

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.and04.naturealbum.data.dto.AlbumDto
import com.and04.naturealbum.ui.component.AlbumLabel
import com.and04.naturealbum.ui.component.PortraitTopAppBar
import com.and04.naturealbum.ui.savephoto.UiState
import com.and04.naturealbum.ui.theme.NatureAlbumTheme
import com.and04.naturealbum.utils.toColor

@Composable
fun AlbumItem(album: AlbumDto, onLabelClick: (Int) -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AlbumLabel(
            modifier = Modifier
                .background(
                    color = album.labelBackgroundColor.toColor(),
                    shape = CircleShape
                )
                .fillMaxWidth(0.8f)
                .clickable { onLabelClick(album.labelId) },
            text = album.labelName,
            backgroundColor = album.labelBackgroundColor.toColor()
        )
        Spacer(modifier = Modifier.height(10.dp))
        AsyncImage(
            model = album.photoDetailUri.toUri(),
            contentDescription = album.labelName, // TODO: 해당 description 무엇으로 할지 확정
            modifier = Modifier
                .wrapContentSize(Alignment.Center)
                .aspectRatio(1f)
                .clip(MaterialTheme.shapes.medium)
                .clickable { onLabelClick(album.labelId) },
            contentScale = ContentScale.Crop,
        )
    }
}

@Composable
fun AlbumGrid(albums: List<AlbumDto>, onLabelClick: (Int) -> Unit) {
    val configuration = LocalConfiguration.current
    val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT
    val columnCount = if (isPortrait) 2 else 4

    LazyVerticalGrid(
        columns = GridCells.Fixed(columnCount),
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(24.dp),
        horizontalArrangement = Arrangement.spacedBy(28.dp),
    ) {
        items(
            items = albums,
            key = { albumDto -> albumDto.labelId }
        ) { album ->
            AlbumItem(
                album = album,
                onLabelClick = onLabelClick,
            )
        }
    }
}

@Composable
fun AlbumScreen(onLabelClick: (Int) -> Unit, viewModel: AlbumViewModel = hiltViewModel()) {
    val isDataLoaded = rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(onLabelClick) {
        if (!isDataLoaded.value) {
            viewModel.loadAlbums()
            isDataLoaded.value = true
        }
    }
    val uiState = viewModel.uiState.observeAsState()
    val albumList by viewModel.albumList.observeAsState()

    when (uiState.value) {
        is UiState.Loading, UiState.Idle, null -> {
            // TODO: loading
        }

        UiState.Success -> {
            Scaffold(topBar = { PortraitTopAppBar() }) { paddingValues ->
                Column(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize()
                ) {
                    albumList?.let { albumList ->
                        AlbumGrid(
                            albums = albumList,
                            onLabelClick = onLabelClick
                        )
                    }
                }
            }
        }
    }
}

@Preview(
    name = "AlbumLabel Light Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Preview(
    name = "AlbumLabel Dark Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)

@Composable
fun AlbumLabelPreview() {
    NatureAlbumTheme() {
        Surface {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AlbumLabel(
                    modifier = Modifier
                        .background(color = Color(0xFFE57373), shape = CircleShape)
                        .fillMaxWidth(0.8f),
                    text = "고양이",
                    backgroundColor = Color(0xFFE57373)
                )
                AlbumLabel(
                    modifier = Modifier
                        .background(color = Color(0xFFD1C4E9), shape = CircleShape)
                        .fillMaxWidth(0.8f),
                    text = "강아지",
                    backgroundColor = Color(0xFFD1C4E9)
                )
                AlbumLabel(
                    modifier = Modifier
                        .background(color = Color(0xFFC5E1A5), shape = CircleShape)
                        .fillMaxWidth(0.8f),
                    text = "해오라기",
                    backgroundColor = Color(0xFFC5E1A5)
                )
            }
        }
    }

}

@Preview(
    name = "AlbumScreen Light Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Preview(
    name = "AlbumScreen Dark Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun AlbumScreenPreview() {
    NatureAlbumTheme {
        Surface {
            AlbumScreen(onLabelClick = {})
        }
    }
}
