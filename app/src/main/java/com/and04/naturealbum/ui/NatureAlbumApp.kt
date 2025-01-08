package com.and04.naturealbum.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import com.and04.naturealbum.background.service.FirebaseMessagingService.Companion.MY_PAGE_URI
import com.and04.naturealbum.ui.navigation.NatureAlbumNavHost
import com.and04.naturealbum.ui.navigation.NatureAlbumState
import com.and04.naturealbum.ui.navigation.rememberNatureAlbumState

@Composable
fun NatureAlbumApp(
    startDestination: String? = null,
    state: NatureAlbumState = rememberNatureAlbumState(),
) {
    val takePictureLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult()
        ) { result ->
            state.handleLauncher(result)
        }

    NatureAlbumNavHost(
        state = state,
        takePictureLauncher = takePictureLauncher
    )

    if (startDestination == MY_PAGE_URI) {
        state.navigateToMyPage()
    }
}
