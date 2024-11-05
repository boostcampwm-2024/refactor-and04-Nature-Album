package com.and04.naturealbum

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.and04.naturealbum.ui.theme.NatureAlbumTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onClickCamera: () -> Unit = {},
    homeViewModel: HomeViewModel = viewModel(),
) {
    val dialogData by homeViewModel.dialogData.collectAsStateWithLifecycle()

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
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            InfoContent(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
            )
            NavigateContent(
                modifier = Modifier
                    .weight(2f)
                    .fillMaxSize()
            ) { onClickCamera() }

            if (dialogData != DialogData() && dialogData.dialogText != null)  MyDialog(dialogData = dialogData)
        }
    }
}

@Composable
fun InfoContent(modifier: Modifier) {
    RoundedShapeButton("TODO", modifier, { /* TODO */ })
}

@Composable
fun NavigateContent(modifier: Modifier, onClickCamera: () -> Unit) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        RoundedShapeButton("TODO", modifier, { /* TODO */ })

        Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            RoundedShapeButton("TODO", modifier, { /* TODO */ })
            RoundedShapeButton("TODO", modifier) { onClickCamera() }
        }
    }

    Spacer(modifier = Modifier.padding(bottom = 72.dp))
}

@Composable
fun RoundedShapeButton(text: String, modifier: Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(10)
    ) {
        Text(
            text = text,
            modifier = Modifier
                .padding(top = 24.dp)
                .align(Alignment.Top)
        )
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_NO)
@Composable
fun HomePreview() {
    NatureAlbumTheme {
        HomeScreen()
    }
}
