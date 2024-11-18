package com.and04.naturealbum.ui.savephoto

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.location.Location
import android.net.Uri
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.and04.naturealbum.R
import com.and04.naturealbum.data.room.Label
import com.and04.naturealbum.ui.component.BackgroundImage
import com.and04.naturealbum.ui.component.RotatingImageLoading
import com.and04.naturealbum.ui.theme.NatureAlbumTheme
import com.and04.naturealbum.utils.GetTopbar
import com.and04.naturealbum.utils.isPortrait

@Composable
fun SavePhotoScreen(
    location: Location?,
    model: Uri,
    onBack: () -> Unit,
    onSave: () -> Unit,
    onLabelSelect: () -> Unit,
    description: String = "",
    label: Label? = null,
    onNavigateToMyPage: () -> Unit,
    viewModel: SavePhotoViewModel = hiltViewModel(),
) {
    // TODO : 상태 변경시 로딩화면등 화면 변경, 없으면 이름 변경 고려
    val photoSaveState by viewModel.photoSaveState.collectAsStateWithLifecycle()
    val rememberDescription = rememberSaveable { mutableStateOf(description) }
    val isRepresented = rememberSaveable { mutableStateOf(false) }

    if (photoSaveState == UiState.Success) {
        onSave()
    }

    SavePhotoScreen(
        model = model,
        label = label,
        location = location,
        photoSaveState = photoSaveState,
        rememberDescription = rememberDescription,
        onDescriptionChange = { newDescription -> rememberDescription.value = newDescription },
        isRepresented = isRepresented,
        onRepresentedChange = { isRepresented.value = !isRepresented.value },
        onNavigateToMyPage = onNavigateToMyPage,
        onLabelSelect = onLabelSelect,
        onBack = onBack,
        savePhoto = viewModel::savePhoto
    )
}

@Composable
fun SavePhotoScreen(
    model: Uri,
    label: Label?,
    location: Location?,
    rememberDescription: State<String>,
    onDescriptionChange: (String) -> Unit,
    isRepresented: State<Boolean>,
    onRepresentedChange: () -> Unit,
    photoSaveState: UiState,
    onNavigateToMyPage: () -> Unit,
    onLabelSelect: () -> Unit,
    onBack: () -> Unit,
    savePhoto: (String, Label, Location, String, Boolean) -> Unit
) {
    Scaffold(
        topBar = { LocalContext.current.GetTopbar { onNavigateToMyPage() } },
    ) { innerPadding ->
        BackgroundImage()

        if (LocalContext.current.isPortrait()) {
            SavePhotoScreenPortrait(
                innerPadding = innerPadding,
                model = model,
                label = label,
                location = location,
                rememberDescription = rememberDescription,
                onDescriptionChange = onDescriptionChange,
                isRepresented = isRepresented,
                onRepresentedChange = onRepresentedChange,
                photoSaveState = photoSaveState,
                onLabelSelect = onLabelSelect,
                onBack = onBack,
                savePhoto = savePhoto
            )
        } else {
            SavePhotoScreenLandscape(
                innerPadding = innerPadding,
                model = model,
                label = label,
                location = location,
                rememberDescription = rememberDescription,
                onDescriptionChange = onDescriptionChange,
                isRepresented = isRepresented,
                onRepresentedChange = onRepresentedChange,
                photoSaveState = photoSaveState,
                onLabelSelect = onLabelSelect,
                onBack = onBack,
                savePhoto = savePhoto
            )
        }
    }

    if (photoSaveState.value == UiState.Loading) {
        RotatingImageLoading(
            drawalbeRes = R.drawable.fish_loading_image,
            stringRes = R.string.save_photo_screen_loading,
        )
    }

    BackHandler(onBack = onBack)
}

@Composable
fun IconTextButton(
    enabled: Boolean = true,
    imageVector: ImageVector,
    modifier: Modifier = Modifier,
    @StringRes stringRes: Int,
    onClick: () -> Unit,
) {
    Button(
        enabled = enabled,
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier.size(24.dp),
                imageVector = imageVector,
                contentDescription = null
            )
            Text(
                text = stringResource(stringRes),
                textAlign = TextAlign.Center,
                modifier = modifier
            )
        }
    }
}

@Composable
fun ToggleButton(
    selected: State<Boolean>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clickable(onClick = { onClick() }),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        RadioButton(
            selected = selected.value,
            onClick = { onClick() },
            modifier = modifier
                .size(24.dp)
                .focusable(false),
        )
        Text(stringResource(R.string.save_photo_screen_set_represent))
    }
}

@Composable
fun LabelSelection(
    label: Label?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.padding(horizontal = 12.dp)) {
        Text(
            stringResource(R.string.save_photo_screen_label),
            style = MaterialTheme.typography.headlineLarge,
            fontSize = TextUnit(20f, TextUnitType.Sp),
        )
        Button(
            onClick = { onClick() },
            modifier = modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        ) {
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = null
                )
                Box(
                    modifier = modifier
                        .weight(1f)
                        .padding(horizontal = 24.dp)
                ) {
                    label?.let {
                        val backgroundColor = Color(label.backgroundColor.toLong(16))
                        SuggestionChip(
                            onClick = { onClick() },
                            label = { Text(text = label.name) },
                            colors = SuggestionChipDefaults.suggestionChipColors(
                                containerColor = backgroundColor,
                                labelColor = if (backgroundColor.luminance() > 0.5f) Color.Black else Color.White,
                            ),
                            modifier = Modifier.heightIn(max = 24.dp)
                        )
                    } ?: Text(text = stringResource(R.string.save_photo_screen_select_label))
                }
            }
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
            )
        }
    }
}

@Composable
fun Description(
    description: State<String>,
    modifier: Modifier,
    onValueChange: (String) -> Unit,
) {
    Column(
        modifier = modifier.padding(12.dp)
    ) {
        Text(
            stringResource(R.string.save_photo_screen_description),
            style = MaterialTheme.typography.headlineLarge,
            fontSize = TextUnit(20f, TextUnitType.Sp),
        )
        TextField(
            value = description.value,
            onValueChange = { text -> onValueChange(text) },
            placeholder = { Text(stringResource(R.string.save_photo_screen_description_about_photo)) },
            modifier = modifier
                .weight(1f)
                .fillMaxWidth()
        )
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_NO)
@Composable
private fun ScreenPreview() {
    NatureAlbumTheme {
        val rememberDescription = rememberSaveable { mutableStateOf("") }
        val isRepresented = rememberSaveable { mutableStateOf(false) }

        SavePhotoScreen(
            model = "".toUri(),
            label = Label(0, "0000FF", "cat"),
            location = null,
            rememberDescription = rememberDescription,
            onDescriptionChange = { },
            isRepresented = isRepresented,
            onRepresentedChange = { },
            photoSaveState = UiState.Success,
            onNavigateToMyPage = { },
            onLabelSelect = { },
            onBack = { },
            savePhoto = { _, _, _, _, _ -> },
        )
    }
}
