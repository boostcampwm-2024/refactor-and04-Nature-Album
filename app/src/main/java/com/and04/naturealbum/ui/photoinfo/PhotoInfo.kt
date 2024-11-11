package com.and04.naturealbum.ui.photoinfo

import android.graphics.Color.parseColor
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.and04.naturealbum.data.room.Label
import com.and04.naturealbum.data.room.PhotoDetail
import com.and04.naturealbum.ui.component.AlbumLabel
import com.and04.naturealbum.ui.component.MyTopAppBar
import com.and04.naturealbum.ui.savephoto.UiState

@Composable
fun PhotoInfo(
    selectedPhotoDetail: Int = 0,
    photoInfoViewModel: PhotoInfoViewModel = hiltViewModel()
) {
    photoInfoViewModel.loadPhotoDetail(selectedPhotoDetail)
    Scaffold(topBar = { MyTopAppBar() }) { innerPadding ->
        Content(innerPadding = innerPadding)
    }
}

@Composable
private fun Content(
    innerPadding: PaddingValues,
    photoInfoViewModel: PhotoInfoViewModel = hiltViewModel()
) {
    val uiState = photoInfoViewModel.uiState.collectAsState()
    val photoDetail = photoInfoViewModel.photoDetail.collectAsState()
    val label = photoInfoViewModel.label.collectAsState()

    when (uiState.value) {
        is UiState.Idle, UiState.Loading -> {
            //TODO Loading
        }

        is UiState.Success -> {
            PhotoDetailInfo(innerPadding, photoDetail, label)
        }
    }
}

@Composable
private fun PhotoDetailInfo(
    innerPadding: PaddingValues,
    photoDetail: State<PhotoDetail>,
    label: State<Label>
) {
    
}

@Preview
@Composable
private fun PhotoInfoPreview() {
    PhotoInfo()
}