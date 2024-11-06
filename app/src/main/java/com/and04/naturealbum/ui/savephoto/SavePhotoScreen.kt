package com.and04.naturealbum.ui.savephoto

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Create
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.and04.naturealbum.R
import com.and04.naturealbum.ui.theme.NatureAlbumTheme

@Composable
fun SavePhotoScreen(
    fileName: String
) {
    SavePhotoScreen(
        model = fileName,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavePhotoScreen(
    model: Any, // uri, Resource id, bitmap 등등.. 타입이 확정지어지지 않음
    onBack: () -> Unit = {},
    onSave: () -> Unit = {},
    onLabelSelect: () -> Unit = {},
    description: String = "",
    label: Label? = null,
    modifier: Modifier = Modifier
) {
    BackHandler(onBack = onBack)
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.app_name))
                },
                actions = {
                    IconButton(onClick = { /* TODO */ }) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = null /* TODO */
                        )
                    }
                }
            )
        },
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(model)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                modifier = modifier
                    .weight(1f)
                    .align(Alignment.CenterHorizontally)
                    .clip(RoundedCornerShape(10.dp))
            )

            Spacer(modifier = modifier.size(36.dp))
            LabelSelection(label, onClick = onLabelSelect, modifier = modifier)

            Spacer(modifier = modifier.size(36.dp))
            Description(description, modifier = modifier)

            ToggleButton(selected = false, modifier = modifier)

            Spacer(modifier = modifier.size(36.dp))
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(30.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconTextButton(
                    modifier = modifier.weight(1f),
                    imageVector = Icons.Default.Close,
                    text = stringResource(R.string.save_photo_screen_cancel),
                    onClick = { onBack() })
                IconTextButton(
                    modifier = modifier.weight(1f),
                    imageVector = Icons.Outlined.Create,
                    text = stringResource(R.string.save_photo_screen_save),
                    onClick = { onSave() })
            }

        }
    }
}

@Composable
private fun IconTextButton(
    imageVector: ImageVector,
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit
) {
    Button(
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
                text = text,
                textAlign = TextAlign.Center,
                modifier = modifier
            )
        }
    }
}

@Composable
private fun ToggleButton(
    selected: Boolean,
    modifier: Modifier = Modifier,
) {
    Spacer(modifier = modifier.size(24.dp))
    Row(
        modifier = modifier
            .padding(vertical = 8.dp)
            .selectable(
                selected = selected,
                onClick = { TODO() }
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        RadioButton(
            selected = selected,
            {},
            modifier = modifier
                .size(24.dp)
        )
        Text(stringResource(R.string.save_photo_screen_set_represent))
    }
}

@Composable
private fun LabelSelection(
    label: Label?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    Column(modifier = modifier) {
        Text(
            stringResource(R.string.save_photo_screen_label),
            modifier = modifier,
            style = MaterialTheme.typography.headlineLarge,
            fontSize = TextUnit(20f, TextUnitType.Sp),
        )
        Spacer(modifier = modifier.size(8.dp))
        Button(
            onClick = { onClick() },
            modifier = modifier
                .fillMaxWidth(),
            contentPadding = PaddingValues(all = 4.dp),
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
                    modifier = modifier
                        .padding(12.dp)
                        .size(24.dp),
                    imageVector = Icons.Default.Menu,
                    contentDescription = null
                )
                Box(
                    modifier = modifier
                        .weight(1f)
                        .padding(horizontal = 4.dp)
                ) {
                    label?.let {
                        val backgroundColor = Color(label.backgroundColor)
                        SuggestionChip(
                            onClick = {},
                            label = {
                                Text(text = label.name)
                            },
                            colors = SuggestionChipDefaults.suggestionChipColors(
                                containerColor = backgroundColor,
                                labelColor = if (backgroundColor.luminance() > 0.5f) Color.Black else Color.White,
                            )
                        )
                    }
                        ?: Text(text = stringResource(R.string.save_photo_screen_select_label))
                }
            }
            Icon(
                modifier = modifier
                    .padding(12.dp)
                    .size(24.dp),
                imageVector = Icons.Default.Search,
                contentDescription = null
            )
        }
    }
}

@Composable
private fun Description(
    description: String,
    modifier: Modifier
) {
    Column(modifier = modifier) {
        Text(
            stringResource(R.string.save_photo_screen_description),
            modifier = modifier,
            style = MaterialTheme.typography.headlineLarge,
            fontSize = TextUnit(20f, TextUnitType.Sp),
        )
        Spacer(modifier = modifier.size(8.dp))
        TextField(
            value = description,
            onValueChange = { TODO() },
            placeholder = { Text(stringResource(R.string.save_photo_screen_description_about_photo)) },
            modifier = modifier
                .fillMaxWidth()
                .height(120.dp),
        )
    }
}


@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_NO)
@Composable
private fun ScreenPreview() {
    NatureAlbumTheme {
        SavePhotoScreen(R.drawable.cat_dummy, label = Label(0, 0xFF0000FF, "cat"))
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_NO)
@Composable
private fun ScreenEmptyPreview() {
    NatureAlbumTheme {
        SavePhotoScreen(R.drawable.cat_dummy)
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_NO)
@Composable
private fun ScreenDescriptionPreview() {
    NatureAlbumTheme {
        SavePhotoScreen(
            R.drawable.cat_dummy, description = "내용을 적어보아요.\n" +
                    "최대 4줄까지는 기본으로 보이고\n" +
                    "그 아래는 스크롤이 되도록 해보아요\n" +
                    "룰루", label = Label(0, 0xFFFFFFFF, "cat")
        )
    }
}
