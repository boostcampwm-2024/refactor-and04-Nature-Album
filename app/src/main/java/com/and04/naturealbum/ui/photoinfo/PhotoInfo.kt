package com.and04.naturealbum.ui.photoinfo

import android.content.res.Configuration
import android.graphics.Color.parseColor
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.and04.naturealbum.R
import com.and04.naturealbum.data.room.Label
import com.and04.naturealbum.data.room.PhotoDetail
import com.and04.naturealbum.ui.component.AlbumLabel
import com.and04.naturealbum.ui.savephoto.UiState
import com.and04.naturealbum.utils.GetTopbar

@Composable
fun PhotoInfo(
    selectedPhotoDetail: Int = 0,
    onNavigateToMyPage: () -> Unit,
    photoInfoViewModel: PhotoInfoViewModel = hiltViewModel(),
) {
    val isDataLoaded = rememberSaveable { mutableStateOf(false) }
    val uiState by photoInfoViewModel.uiState.collectAsStateWithLifecycle()
    val photoDetail by photoInfoViewModel.photoDetail.collectAsStateWithLifecycle()
    val label by photoInfoViewModel.label.collectAsStateWithLifecycle()

    LaunchedEffect(selectedPhotoDetail) {
        if (!isDataLoaded.value) {
            photoInfoViewModel.loadPhotoDetail(selectedPhotoDetail)
            isDataLoaded.value = true
        }
    }

    PhotoInfo(
        onNavigateToMyPage = onNavigateToMyPage,
        uiState = uiState,
        photoDetail = photoDetail,
        label = label
    )
}

@Composable
fun PhotoInfo(
    onNavigateToMyPage: () -> Unit,
    uiState: UiState,
    photoDetail: PhotoDetail,
    label: Label
) {
    Scaffold(
        topBar = { LocalContext.current.GetTopbar { onNavigateToMyPage() } }
    ) { innerPadding ->
        Content(
            innerPadding = innerPadding,
            uiState = uiState,
            photoDetail = photoDetail,
            label = label
        )
    }
}

@Composable
private fun Content(
    innerPadding: PaddingValues,
    uiState: UiState,
    photoDetail: PhotoDetail,
    label: Label
) {
    when (uiState) {
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
    photoDetail: PhotoDetail,
    label: Label,
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    if (isLandscape) {
        PhotoInfoLandscape(
            innerPadding = innerPadding,
            photoDetail = photoDetail,
            label = label,
        )
    } else {
        PhotoInfoPortrait(
            innerPadding = innerPadding,
            photoDetail = photoDetail,
            label = label,
        )
    }
}

@Composable
private fun PhotoInfoLandscape(
    innerPadding: PaddingValues,
    photoDetail: PhotoDetail,
    label: Label,
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(36.dp)
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(photoDetail.photoUri)
                    .crossfade(true)
                    .build(),
                contentDescription = photoDetail.description,
                modifier = Modifier.clip(RoundedCornerShape(10.dp))
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AlbumLabel(
                modifier = Modifier
                    .background(
                        color = Color(parseColor("#${label.backgroundColor}")),
                        shape = CircleShape
                    )
                    .fillMaxWidth(0.6f),
                text = label.name,
                backgroundColor = Color(parseColor("#${label.backgroundColor}"))
            )

            RowInfo(
                imgVector = Icons.Default.DateRange,
                contentDescription = stringResource(R.string.photo_info_screen_calender_icon),
                text = photoDetail.datetime.toString() // TODO: date format
            )

            RowInfo(
                imgVector = Icons.Default.LocationOn,
                contentDescription = stringResource(R.string.photo_info_screen_location_icon),
                text = "${photoDetail.latitude}, ${photoDetail.longitude}" // TODO: 좌표를 주소로 변경
            )

            RowInfo(
                imgVector = Icons.Default.Edit,
                contentDescription = stringResource(R.string.photo_info_screen_description_icon),
                text = photoDetail.description
            )
        }
    }
}

@Composable
private fun PhotoInfoPortrait(
    innerPadding: PaddingValues,
    photoDetail: PhotoDetail,
    label: Label,
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
                    color = Color(parseColor("#${label.backgroundColor}")),
                    shape = CircleShape
                )
                .fillMaxWidth(0.4f),
            text = label.name,
            backgroundColor = Color(parseColor("#${label.backgroundColor}"))
        )

        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(photoDetail.photoUri)
                .crossfade(true)
                .build(),
            contentDescription = photoDetail.description,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .clip(RoundedCornerShape(10.dp))
        )

        RowInfo(
            imgVector = Icons.Default.DateRange,
            contentDescription = stringResource(R.string.photo_info_screen_calender_icon),
            text = photoDetail.datetime.toString() // TODO: date format
        )

        RowInfo(
            imgVector = Icons.Default.LocationOn,
            contentDescription = stringResource(R.string.photo_info_screen_location_icon),
            text = "${photoDetail.latitude}, ${photoDetail.longitude}" // TODO: 좌표를 주소로 변경
        )

        RowInfo(
            imgVector = Icons.Default.Edit,
            contentDescription = stringResource(R.string.photo_info_screen_description_icon),
            text = photoDetail.description
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
    PhotoInfo(
        onNavigateToMyPage = {},
        uiState = UiState.Success,
        photoDetail = PhotoDetail.emptyPhotoDetail(),
        label = Label.emptyLabel()
    )
}
