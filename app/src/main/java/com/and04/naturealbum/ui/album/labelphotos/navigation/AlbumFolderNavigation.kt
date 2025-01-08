package com.and04.naturealbum.ui.album.labelphotos.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.and04.naturealbum.ui.album.labelphotos.AlbumFolderScreen
import com.and04.naturealbum.ui.navigation.NatureAlbumState
import com.and04.naturealbum.ui.navigation.NavigateDestination

fun NavGraphBuilder.albumFolderNavigation(
    state: NatureAlbumState
){
    composable("${NavigateDestination.AlbumFolder.route}/{labelId}") { backStackEntry ->
        val labelId = backStackEntry.arguments?.getString("labelId")?.toInt()!!

        AlbumFolderScreen(
            selectedAlbumLabel = labelId,
            onPhotoClick = { photoDetailId -> state.navigateToAlbumInfo(photoDetailId) },
            onNavigateToMyPage = { state.navigateToMyPage() },
            navigateToBackScreen = { state.popupBackStack() },
            onNavigateToAlbum = {
                state.popupBackStack()
            }
        )
    }
}
