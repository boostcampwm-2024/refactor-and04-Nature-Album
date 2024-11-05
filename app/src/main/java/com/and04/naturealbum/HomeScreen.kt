package com.and04.naturealbum

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.and04.naturealbum.ui.theme.NatureAlbumTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onClickCamera: () -> Unit,
    homeViewModel: HomeViewModel = viewModel(),
) {
    val dialogData by homeViewModel.dialogData.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
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
        Column(modifier = Modifier.padding(innerPadding)) {
            InfoContent()
            NavigateContent(onClickCamera)
            if (dialogData.text.isNotEmpty() && dialogData.isShow)  MyDialog(dialogData = dialogData)

        }
    }
}

@Composable
fun InfoContent() {

}

@Composable
fun NavigateContent(onClickCamera: () -> Unit) {
    Button(onClick = {/* TODO */ }) {
        Text("지도")
    }
    Row {
        Button(onClick = {/* TODO */ }) {
            Text("도감")
        }
        Button(onClick = onClickCamera) {
            Text("카메라")
        }
    }
}


@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_NO)
@Composable
fun HomePreview() {
    NatureAlbumTheme {
        HomeScreen(
            onClickCamera = {}
        )
    }
}
