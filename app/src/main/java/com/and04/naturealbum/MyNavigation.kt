package com.and04.naturealbum

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun MyNav(
    homeViewModel: HomeViewModel,
    onClickCamera: () -> Unit,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    NavHost(navController = navController, startDestination = "Home", modifier = modifier) {
        composable("Home") { HomeScreen(onClickCamera, homeViewModel = homeViewModel) }
    }
}
