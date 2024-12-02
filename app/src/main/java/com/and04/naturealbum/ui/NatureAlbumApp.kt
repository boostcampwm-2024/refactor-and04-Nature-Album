package com.and04.naturealbum.ui

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.and04.naturealbum.ui.album.AlbumScreen
import com.and04.naturealbum.ui.albumfolder.AlbumFolderScreen
import com.and04.naturealbum.ui.friend.FriendSearchScreen
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
        startDestination = NavigateDestination.Home.route
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
                state.getNavBackStackEntry(NavigateDestination.SavePhoto.route)
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
                state.getNavBackStackEntry(NavigateDestination.SavePhoto.route)
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
            MyPageScreen(
                navigateToHome = { state.popupBackStack() },
                navigateToFriendSearchScreen = { state.navigateToFriendSearch() },
            )
        }

        composable(NavigateDestination.Map.route) {
            MapScreen(state.lastLocation.value)
        }
        composable(NavigateDestination.FriendSearch.route) { backStackEntry ->
            val viewmodel = remember(backStackEntry) {
                state.getNavBackStackEntry(NavigateDestination.MyPage.route)
            }
            FriendSearchScreen(
                onBack = { state.popupBackStack() },
                friendViewModel = hiltViewModel(viewmodel),
            )
        }
    }
}
