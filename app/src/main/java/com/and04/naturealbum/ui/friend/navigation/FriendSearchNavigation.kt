package com.and04.naturealbum.ui.friend.navigation

import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.and04.naturealbum.ui.friend.FriendSearchScreen
import com.and04.naturealbum.ui.navigation.NatureAlbumState
import com.and04.naturealbum.ui.navigation.NavigateDestination

fun NavGraphBuilder.friendSearchNavigation(
    state: NatureAlbumState

){
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
