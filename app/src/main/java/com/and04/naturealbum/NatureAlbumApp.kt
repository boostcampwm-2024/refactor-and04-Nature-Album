package com.and04.naturealbum

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.and04.naturealbum.ui.theme.NatureAlbumTheme

@Composable
fun NatureAlbumApp () {
    val navController = rememberNavController()

    NatureAlbumTheme {
        NatureAlbumNavHost(navController)
    }
}

@Composable
fun NatureAlbumNavHost(
    navController: NavHostController,
) {
    NavHost(
        navController = navController,
        startDestination = NavigateDestination.Home.route
    ) {
        composable(NavigateDestination.Home.route) {
            HomeScreen()
        }
    }
}
