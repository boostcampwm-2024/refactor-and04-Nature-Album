package com.and04.naturealbum

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.and04.naturealbum.ui.theme.NatureAlbumTheme

@Composable
fun NatureAlbumApp (viewModel: HomeViewModel, onClickCamera: () -> Unit,) {
    val navController = rememberNavController()

    NatureAlbumTheme {
        NatureAlbumNavHost(navController, onClickCamera, viewModel)
    }
}

@Composable
fun NatureAlbumNavHost(
    navController: NavHostController,
    onClickCamera: () -> Unit,
    viewModel: HomeViewModel,
) {
    NavHost(
        navController = navController,
        startDestination = NavigateDestination.Home.route
    ) {
        composable(NavigateDestination.Home.route) {
            HomeScreen(onClickCamera = onClickCamera, homeViewModel = viewModel)
        }
    }
}
