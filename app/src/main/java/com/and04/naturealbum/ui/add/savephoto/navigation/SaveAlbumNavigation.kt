package com.and04.naturealbum.ui.add.savephoto.navigation

import android.content.Intent
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.and04.naturealbum.ui.add.savephoto.SavePhotoScreen
import com.and04.naturealbum.ui.navigation.NatureAlbumState
import com.and04.naturealbum.ui.navigation.NavigateDestination

fun NavGraphBuilder.saveAlbumNavGraph(
    state: NatureAlbumState,
    takePictureLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>
){
    composable(NavigateDestination.SavePhoto.route) { backStackEntry ->
        val savePhotoBackStackEntry = remember(backStackEntry) {
            state.getNavBackStackEntry(NavigateDestination.SavePhoto.route)
        }
        SavePhotoScreen(
            locationHandler = state.locationHandler.value,
            location = state.lastLocation.value,
            model = state.imageUri.value,
            fileName = state.fileName.value,
            onBack = { state.takePicture(takePictureLauncher) },
            onSave = { state.navigateSavePhotoToAlbum() },
            onCancel = { state.navigateToHome() },
            label = state.selectedLabel.value,
            onLabelSelect = { state.navigateToSearchLabel() },
            onNavigateToMyPage = { state.navigateToMyPage() },
            viewModel = hiltViewModel(savePhotoBackStackEntry),
        )
    }
}
