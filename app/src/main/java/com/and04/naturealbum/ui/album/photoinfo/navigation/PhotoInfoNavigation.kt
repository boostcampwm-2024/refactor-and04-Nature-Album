package com.and04.naturealbum.ui.album.photoinfo.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.and04.naturealbum.ui.album.photoinfo.PhotoInfo
import com.and04.naturealbum.ui.navigation.NatureAlbumState
import com.and04.naturealbum.ui.navigation.NavigateDestination

fun NavGraphBuilder.photoInfoNavigation(
    state: NatureAlbumState
){
    composable("${NavigateDestination.PhotoInfo.route}/{photoDetailId}") { backStackEntry ->
        val photoDetailId = backStackEntry.arguments?.getString("photoDetailId")?.toInt()!!

        PhotoInfo(
            selectedPhotoDetail = photoDetailId,
            onNavigateToMyPage = { state.navigateToMyPage() },
            navigateToBackScreen = { state.popupBackStack() },
        )
    }
}
