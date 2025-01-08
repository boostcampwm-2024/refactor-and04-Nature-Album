package com.and04.naturealbum.ui.album.labels.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.and04.naturealbum.ui.album.labels.AlbumScreen
import com.and04.naturealbum.ui.navigation.NatureAlbumState
import com.and04.naturealbum.ui.navigation.NavigateDestination

fun NavGraphBuilder.labelsAlbumNavigation(
    state: NatureAlbumState
){
    composable(NavigateDestination.Album.route) {
        AlbumScreen(
            onLabelClick = { labelId -> state.navigateToAlbumFolder(labelId) },
            onNavigateToMyPage = { state.navigateToMyPage() },
            navigateToBackScreen = { state.popupBackStack() },
        )
    }
}
