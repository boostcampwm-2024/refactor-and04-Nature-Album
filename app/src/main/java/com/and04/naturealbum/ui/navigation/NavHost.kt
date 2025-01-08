package com.and04.naturealbum.ui.navigation

import android.content.Intent
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import com.and04.naturealbum.ui.add.labelsearch.navigation.labelSearchNavigation
import com.and04.naturealbum.ui.add.savephoto.navigation.saveAlbumNavGraph
import com.and04.naturealbum.ui.album.labelphotos.navigation.albumFolderNavigation
import com.and04.naturealbum.ui.album.labels.navigation.labelsAlbumNavigation
import com.and04.naturealbum.ui.album.photoinfo.navigation.photoInfoNavigation
import com.and04.naturealbum.ui.friend.navigation.friendSearchNavigation
import com.and04.naturealbum.ui.home.navigation.homeNavGraph
import com.and04.naturealbum.ui.maps.navigation.mapNavigation
import com.and04.naturealbum.ui.mypage.navigation.myPageNavigation

@Composable
fun NatureAlbumNavHost(
    state: NatureAlbumState,
    takePictureLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>,
){
    NavHost(
        navController = state.navController,
        startDestination = NavigateDestination.Home.route,
        enterTransition = { fadeIn(animationSpec = tween(0)) },
        exitTransition = { fadeOut(animationSpec = tween(0)) },
    ) {
        homeNavGraph(
            state = state,
            takePictureLauncher = takePictureLauncher
        )

        saveAlbumNavGraph(
            state = state,
            takePictureLauncher = takePictureLauncher
        )

        labelSearchNavigation(
            state = state
        )

        labelsAlbumNavigation(
            state = state
        )

        albumFolderNavigation(
            state = state
        )

        photoInfoNavigation(
            state = state
        )

        myPageNavigation(
            state = state
        )

        mapNavigation(
            state = state
        )

        friendSearchNavigation(
            state = state
        )
    }
}
