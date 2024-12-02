package com.and04.naturealbum.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.and04.naturealbum.ui.album.AlbumScreen
import com.and04.naturealbum.ui.albumfolder.AlbumFolderScreen
import com.and04.naturealbum.ui.home.HomeScreen
import com.and04.naturealbum.ui.labelsearch.LabelSearchScreen
import com.and04.naturealbum.ui.maps.MapScreen
import com.and04.naturealbum.ui.mypage.MyPageScreen
import com.and04.naturealbum.ui.navigation.NatureAlbumState
import com.and04.naturealbum.ui.navigation.NavigateDestination
import com.and04.naturealbum.ui.navigation.rememberNatureAlbumState
import com.and04.naturealbum.ui.photoinfo.PhotoInfo
import com.and04.naturealbum.ui.savephoto.SavePhotoScreen

@Composable
fun NatureAlbumApp(
    state: NatureAlbumState = rememberNatureAlbumState(),
) {
    val takePictureLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult()
        ) { result ->
            state.handleLauncher(result)
        }


    NavHost(
        navController = state.navController,
        startDestination = NavigateDestination.Home.route,
        enterTransition = { fadeIn(animationSpec = tween(0)) },
        exitTransition = { fadeOut(animationSpec = tween(0)) },
    ) {
        composable(NavigateDestination.Home.route) {
            HomeScreen(
                locationHandler = state.locationHandler.value,
                takePicture = { state.takePicture(takePictureLauncher) },
                onNavigateToAlbum = { state.navigateToAlbum() },
                onNavigateToMap = {
                    state.locationHandler.value.getLocation { location ->
                        state.lastLocation.value = location
                    }
                    state.navigateToMap()
                },
                onNavigateToMyPage = { state.navigateToMyPage() },
            )
        }

        composable(NavigateDestination.SavePhoto.route) { backStackEntry ->
            val viewmodel = remember(backStackEntry) {
                state.getNavBackStackEntry()
            }
            SavePhotoScreen(
                location = state.lastLocation.value,
                model = state.imageUri.value,
                fileName = state.fileName.value,
                onBack = { state.takePicture(takePictureLauncher) },
                onSave = { state.navigateSavePhotoToAlbum() },
                label = state.selectedLabel.value,
                onLabelSelect = { state.navigateToSearchLabel() },
                onNavigateToMyPage = { state.navigateToMyPage() },
                viewModel = hiltViewModel(viewmodel),
            )
        }

        composable(NavigateDestination.SearchLabel.route) { backStackEntry ->
            val viewmodel = remember(backStackEntry) {
                state.getNavBackStackEntry()
            }

            LabelSearchScreen(
                onSelected = { label ->
                    state.selectedLabel.value = label
                    state.popupBackStack()
                },
                savePhotoViewModel = hiltViewModel(viewmodel),
            )
        }

        composable(NavigateDestination.Album.route) {
            AlbumScreen(
                onLabelClick = { labelId -> state.navigateToAlbumFolder(labelId) },
                onNavigateToMyPage = { state.navigateToMyPage() },
            )
        }

        composable("${NavigateDestination.AlbumFolder.route}/{labelId}") { backStackEntry ->
            val labelId = backStackEntry.arguments?.getString("labelId")?.toInt()!!

            AlbumFolderScreen(
                selectedAlbumLabel = labelId,
                onPhotoClick = { photoDetailId -> state.navigateToAlbumInfo(photoDetailId) },
                onNavigateToMyPage = { state.navigateToMyPage() },
            )
        }

        composable("${NavigateDestination.PhotoInfo.route}/{photoDetailId}") { backStackEntry ->
            val photoDetailId = backStackEntry.arguments?.getString("photoDetailId")?.toInt()!!

            PhotoInfo(
                selectedPhotoDetail = photoDetailId,
                onNavigateToMyPage = { state.navigateToMyPage() },
            )
        }

        composable(NavigateDestination.MyPage.route) {
            MyPageScreen(navigateToHome = { state.popupBackStack() })
        }

        composable(NavigateDestination.Map.route) {
            MapScreen(state.lastLocation.value)
        }
    }
}
