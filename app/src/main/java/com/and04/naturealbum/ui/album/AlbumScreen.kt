package com.and04.naturealbum.ui.album

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.and04.naturealbum.R
import com.and04.naturealbum.data.dto.AlbumDto
import com.and04.naturealbum.ui.component.AlbumLabel
import com.and04.naturealbum.ui.model.UiState
import com.and04.naturealbum.ui.theme.NatureAlbumTheme
import com.and04.naturealbum.utils.GetTopBar
import com.and04.naturealbum.utils.gridColumnCount
import com.and04.naturealbum.utils.toColor

@Composable
fun AlbumScreen(
    onLabelClick: (Int) -> Unit,
    onNavigateToMyPage: () -> Unit,
    navigateToBackScreen: () -> Unit,
    viewModel: AlbumViewModel = hiltViewModel(),
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()

    viewModel.loadAlbums()

    AlbumScreen(
        uiState = uiState,
        onLabelClick = onLabelClick,
        navigateToBackScreen = navigateToBackScreen,
        onNavigateToMyPage = onNavigateToMyPage
    )
}

@Composable
fun AlbumScreen(
    uiState: State<UiState<List<AlbumDto>>>,
    onLabelClick: (Int) -> Unit,
    navigateToBackScreen: () -> Unit,
    onNavigateToMyPage: () -> Unit,
) {
    when (val success = uiState.value) {
        is UiState.Success -> {
            val context = LocalContext.current
            val albumList = success.data

            Scaffold(
                topBar = {
                    context.GetTopBar(
                        navigateToBackScreen = navigateToBackScreen,
                        navigateToMyPage = onNavigateToMyPage,
                    )
                }
            ) { paddingValues ->
                Column(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize()
                ) {
                    AlbumGrid(
                        albums = albumList,
                        onLabelClick = onLabelClick,
                        columnCount = context.gridColumnCount(),
                    )
                }
            }
        }

        else -> {
            // TODO: loading
        }
    }
}

@Composable
fun AlbumGrid(
    albums: List<AlbumDto>,
    onLabelClick: (Int) -> Unit,
    columnCount: Int,
) {
    if (albums.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = stringResource(R.string.nothing_album_txt))
        }
    } else {
        AlbumGridList(
            albums = albums,
            onLabelClick = onLabelClick,
            columnCount = columnCount,
        )
    }

}

@Composable
fun AlbumGridList(
    albums: List<AlbumDto>,
    onLabelClick: (Int) -> Unit,
    columnCount: Int,
) {
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
        val uiState = remember { mutableStateOf(UiState.Success(listOf<AlbumDto>())) }

        AlbumScreen(
            uiState = uiState,
            onLabelClick = { },
            onNavigateToMyPage = { },
            navigateToBackScreen = { },
        )
    }
}
