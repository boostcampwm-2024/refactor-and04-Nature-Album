package com.and04.naturealbum.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.and04.naturealbum.background.service.FirebaseMessagingService.Companion.MY_PAGE_URI
import com.and04.naturealbum.ui.album.labels.AlbumScreen
import com.and04.naturealbum.ui.album.labelphotos.AlbumFolderScreen
import com.and04.naturealbum.ui.mypage.friendsearch.FriendSearchScreen
import com.and04.naturealbum.ui.home.HomeScreen
import com.and04.naturealbum.ui.add.labelsearch.LabelSearchScreen
import com.and04.naturealbum.ui.maps.MapScreen
import com.and04.naturealbum.ui.mypage.MyPageScreen
import com.and04.naturealbum.ui.navigation.NatureAlbumState
import com.and04.naturealbum.ui.navigation.NavigateDestination
import com.and04.naturealbum.ui.navigation.rememberNatureAlbumState
import com.and04.naturealbum.ui.album.photoinfo.PhotoInfo
import com.and04.naturealbum.ui.add.savephoto.SavePhotoScreen

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
                onNavigateToMap = { state.navigateToMap() },
                onNavigateToMyPage = { state.navigateToMyPage() },
            )
        }

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

        composable(NavigateDestination.SearchLabel.route) { backStackEntry ->
            val savePhotoBackStackEntryForSearchLabel = remember(backStackEntry) {
                state.getNavBackStackEntry(NavigateDestination.SavePhoto.route)
            }

            LabelSearchScreen(
                onSelected = { label ->
                    state.selectedLabel.value = label
                    state.popupBackStack()
                },
                savePhotoViewModel = hiltViewModel(savePhotoBackStackEntryForSearchLabel),
            )
        }

        composable(NavigateDestination.Album.route) {
            AlbumScreen(
                onLabelClick = { labelId -> state.navigateToAlbumFolder(labelId) },
                onNavigateToMyPage = { state.navigateToMyPage() },
                navigateToBackScreen = { state.popupBackStack() },
            )
        }

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

        composable("${NavigateDestination.PhotoInfo.route}/{photoDetailId}") { backStackEntry ->
            val photoDetailId = backStackEntry.arguments?.getString("photoDetailId")?.toInt()!!

            PhotoInfo(
                selectedPhotoDetail = photoDetailId,
                onNavigateToMyPage = { state.navigateToMyPage() },
                navigateToBackScreen = { state.popupBackStack() },
            )
        }

        composable(
            route = NavigateDestination.MyPage.route,
            deepLinks = listOf(navDeepLink {
                uriPattern = MY_PAGE_URI
            })
        ) {
            MyPageScreen(
                navigateToHome = { state.popupBackStack() },
                navigateToFriendSearchScreen = { state.navigateToFriendSearch() },
            )
        }

        composable(NavigateDestination.Map.route) {
            MapScreen(navigateToHome = { state.popupBackStack() })
        }
        composable(NavigateDestination.FriendSearch.route) { backStackEntry ->
            val backStackEntryForMyPage = remember(backStackEntry) {
                state.getNavBackStackEntry(NavigateDestination.MyPage.route)
            }
            FriendSearchScreen(
                onBack = { state.popupBackStack() },
                friendViewModel = hiltViewModel(backStackEntryForMyPage),
                networkViewModel = hiltViewModel(backStackEntryForMyPage),
            )
        }
    }

    if (startDestination == MY_PAGE_URI) {
        state.navigateToMyPage()
    }
}
