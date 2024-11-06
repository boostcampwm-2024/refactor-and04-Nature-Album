package com.and04.naturealbum.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.and04.naturealbum.ui.album.AlbumScreen
import com.and04.naturealbum.ui.home.HomeScreen
import com.and04.naturealbum.ui.savephoto.SavePhotoScreen
import com.and04.naturealbum.ui.theme.NatureAlbumTheme
import java.io.File

@Composable
fun NatureAlbumApp() {
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
            HomeScreen(
                onTakePicture = { fileName ->
                    navController.navigate(
                        "savePhoto/${fileName}"
                    )
                },
                onNavigateToAlbum = {
                    navController.navigate(NavigateDestination.Album.route)
                })
        }
        composable("savePhoto/{fileName}") { backStackEntry ->
            val fileName = backStackEntry.arguments?.getString("fileName")
            fileName?.let {
                SavePhotoScreen(File(LocalContext.current.filesDir, fileName))
            }
        }

        composable(NavigateDestination.Album.route) {
            AlbumScreen()
        }
    }
}
