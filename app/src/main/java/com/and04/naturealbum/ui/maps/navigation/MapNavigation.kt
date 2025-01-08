package com.and04.naturealbum.ui.maps.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.and04.naturealbum.ui.maps.MapScreen
import com.and04.naturealbum.ui.navigation.NatureAlbumState
import com.and04.naturealbum.ui.navigation.NavigateDestination

fun NavGraphBuilder.mapNavigation(
    state: NatureAlbumState
){
    composable(NavigateDestination.Map.route) {
        MapScreen(navigateToHome = { state.popupBackStack() })
    }
}
