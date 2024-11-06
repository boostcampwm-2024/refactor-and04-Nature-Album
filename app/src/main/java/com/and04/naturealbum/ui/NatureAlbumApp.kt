package com.and04.naturealbum.ui

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.and04.naturealbum.ui.home.GPSHandler
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
    val context = LocalContext.current
    val activity = context as? Activity ?: return
    var imageUri: Uri? = remember { null }
    val takePictureLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                navController.navigate(NavigateDestination.SavePhoto.route)
            }
        }
    val takePicture = {
        val fileName = "${System.currentTimeMillis()}.jpg"
        val imageFile = File(context.filesDir, fileName)
        imageUri =
            FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", imageFile)

        Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
            try {
                takePictureLauncher.launch(this)
            } catch (e: ActivityNotFoundException) {
                // TODO: 카메라 전환 오류 처리
            }
        }
    }
    val locationSettingsLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartIntentSenderForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                takePicture()
            }
        }
    val isGrantedGPS = { intentSenderRequest: IntentSenderRequest ->
        locationSettingsLauncher.launch(intentSenderRequest)
    }
    val allPermissionGranted = {
        GPSHandler(
            activity,
            { intentSenderRequest ->
                isGrantedGPS(
                    intentSenderRequest
                )
            },
            {
                takePicture()
            }
        ).startLocationUpdates()
    }

    NavHost(
        navController = navController,
        startDestination = NavigateDestination.Home.route
    ) {
        composable(NavigateDestination.Home.route) {
            HomeScreen(allPermissionGranted = { allPermissionGranted() })
        }
        composable(NavigateDestination.SavePhoto.route) {
            imageUri?.let { uri -> SavePhotoScreen(model = uri) }
        }
    }
}
