package com.and04.naturealbum.ui.mypage.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.and04.naturealbum.background.service.FirebaseMessagingService.Companion.MY_PAGE_URI
import com.and04.naturealbum.ui.mypage.MyPageScreen
import com.and04.naturealbum.ui.navigation.NatureAlbumState
import com.and04.naturealbum.ui.navigation.NavigateDestination

fun NavGraphBuilder.myPageNavigation(
    state: NatureAlbumState
){
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
}
