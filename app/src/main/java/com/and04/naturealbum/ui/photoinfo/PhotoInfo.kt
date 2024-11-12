package com.and04.naturealbum.ui.photoinfo

import android.graphics.Color.parseColor
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.and04.naturealbum.R
import com.and04.naturealbum.data.room.Label
import com.and04.naturealbum.data.room.PhotoDetail
import com.and04.naturealbum.ui.component.AlbumLabel
import com.and04.naturealbum.ui.component.MyTopAppBar
import com.and04.naturealbum.ui.savephoto.UiState

@Composable
fun PhotoInfo(
    selectedPhotoDetail: Int = 0,
    photoInfoViewModel: PhotoInfoViewModel = hiltViewModel(),
) {
    photoInfoViewModel.loadPhotoDetail(selectedPhotoDetail)
    Scaffold(topBar = { MyTopAppBar() }) { innerPadding ->
        Content(innerPadding = innerPadding)
    }
}

@Composable
private fun Content(
    innerPadding: PaddingValues,
    photoInfoViewModel: PhotoInfoViewModel = hiltViewModel(),
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
    label: State<Label>,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AlbumLabel(
            modifier = Modifier
                .background(
                    color = Color(parseColor("#${label.value.backgroundColor}")),
                    shape = CircleShape
                )
                .fillMaxWidth(0.4f),
            text = label.value.name,
            backgroundColor = Color(parseColor("#${label.value.backgroundColor}"))
        )

        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(photoDetail.value.photoUri)
                .crossfade(true)
                .build(),
            contentDescription = photoDetail.value.description,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .clip(RoundedCornerShape(10.dp))
        )

        RowInfo(
            imgVector = Icons.Default.DateRange,
            contentDescription = stringResource(R.string.photo_info_screen_calender_icon),
            text = photoDetail.value.datetime.toString() //TODO date format
        )

        RowInfo(
            imgVector = Icons.Default.LocationOn,
            contentDescription = stringResource(R.string.photo_info_screen_location_icon),
            text = "${photoDetail.value.latitude}, ${photoDetail.value.longitude}"//TODO 좌표 >> 주소로 변경
        )

        RowInfo(
            imgVector = Icons.Default.Edit,
            contentDescription = stringResource(R.string.photo_info_screen_description_icon),
            text = photoDetail.value.description
        )
    }
}

@Composable
private fun RowInfo(
    imgVector: ImageVector,
    contentDescription: String,
    text: String,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = imgVector,
            contentDescription = contentDescription,
        )
        Text(text)
    }
}

@Preview
@Composable
private fun PhotoInfoPreview() {
    PhotoInfo()
}
