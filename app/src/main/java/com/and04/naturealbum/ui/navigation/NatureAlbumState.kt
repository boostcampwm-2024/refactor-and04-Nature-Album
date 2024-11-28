package com.and04.naturealbum.ui.navigation

import android.app.Activity.RESULT_OK
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.location.Location
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.and04.naturealbum.data.room.Label
import com.and04.naturealbum.ui.LocationHandler
import com.and04.naturealbum.utils.ImageConvert
import java.io.File

@Stable
class NatureAlbumState(
    val navController: NavHostController,
    private val context: Context,
) {
    var lastLocation = mutableStateOf<Location?>(null)
    var locationHandler = mutableStateOf(LocationHandler(context))
    var imageUri = mutableStateOf(Uri.EMPTY)
    var fileName = mutableStateOf("")
    var selectedLabel = mutableStateOf<Label?>(null)
    private var imageFile = mutableStateOf<File?>(null)

    fun handleLauncher(result: ActivityResult) {
        if (result.resultCode == RESULT_OK) {
            val resizePicture = ImageConvert.resizeImage(imageUri.value)!!
            imageFile.value?.delete()
            imageUri.value = resizePicture.uri
            fileName.value = resizePicture.fileName

            locationHandler.value.getLocation { location -> lastLocation.value = location }
            navController.navigate(NavigateDestination.SavePhoto.route) {
                launchSingleTop = true
            }
        } else {
            imageUri.value = Uri.EMPTY
            navController.navigate(NavigateDestination.Home.route) {
                popUpTo(NavigateDestination.Home.route) { inclusive = false }
                selectedLabel.value = null
                launchSingleTop = true
            }
        }
    }

    fun takePicture(launcher: ManagedActivityResultLauncher<Intent, ActivityResult>) {
        // TODO: imageUri가 EMPTY가 아닐때 해당 파일 삭제
        fileName.value = "temp_${System.currentTimeMillis()}.jpg"
        imageFile.value = File(context.filesDir, fileName.value)
        imageUri.value =
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                imageFile.value!!
            )

        Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, imageUri.value)
            try {
                launcher.launch(this)
            } catch (e: ActivityNotFoundException) {
                // TODO: 카메라 전환 오류 처리
            }
        }
    }

    fun getNavBackStackEntry() =
        navController.getBackStackEntry(NavigateDestination.SavePhoto.route)

    fun navigateToAlbum() = navController.navigate(NavigateDestination.Album.route)

    fun navigateToMap() = navController.navigate(NavigateDestination.Map.route)

    fun navigateToMyPage() = navController.navigate(NavigateDestination.MyPage.route)

    fun navigateToSearchLabel() = navController.navigate(NavigateDestination.SearchLabel.route)

    fun navigateToHome() = navController.navigate(NavigateDestination.Home.route)

    fun navigateSavePhotoToAlbum() {
        navController.navigate(NavigateDestination.Album.route) {
            popUpTo(NavigateDestination.Home.route) { inclusive = false }
            selectedLabel.value = null
        }
    }

    fun navigateToAlbumFolder(labelId: Int) {
        navController.navigate("${NavigateDestination.AlbumFolder.route}/$labelId")
    }

    fun navigateToAlbumInfo(photoDetailId: Int) {
        navController.navigate("${NavigateDestination.PhotoInfo.route}/$photoDetailId")
    }
}

@Composable
fun rememberNatureAlbumState(
    navController: NavHostController = rememberNavController(),
    context: Context = LocalContext.current,
): NatureAlbumState {
    val saver = Saver<NatureAlbumState, Map<String, Any?>>(
        save = { state ->
            mapOf(
                "imageUri" to state.imageUri.value.toString(),
                "fileName" to state.fileName.value,
                "lastLocation" to state.lastLocation.value,
            )
        },
        restore = { restoredMap ->
            NatureAlbumState(
                navController = navController,
                context = context,
            ).apply {
                imageUri.value = Uri.parse(restoredMap["imageUri"] as String)
                fileName.value = restoredMap["fileName"] as String
                lastLocation.value = restoredMap["lastLocation"] as Location?
            }
        }
    )

    return rememberSaveable(saver = saver) {
        NatureAlbumState(
            navController = navController,
            context = context,
        )
    }
}
