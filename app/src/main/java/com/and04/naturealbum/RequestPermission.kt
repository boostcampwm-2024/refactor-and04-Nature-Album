package com.and04.naturealbum

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.compose.runtime.MutableState
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File

fun onClickCamera(
    context: Context,
    requestPermissionLauncher: ManagedActivityResultLauncher<Array<String>, Map<String, @JvmSuppressWildcards Boolean>>,
    takePicture: () -> Unit,
    dialogPermissionExplainState: MutableState<Boolean>,
) {
    val activity = context as? Activity ?: return
    if (!hasCameraHardware(context)) return
    val deniedPermissions = REQUESTED_PERMISSIONS.filter { permissions ->
        ContextCompat.checkSelfPermission(
            context,
            permissions
        ) != PackageManager.PERMISSION_GRANTED
    }
    if (deniedPermissions.isEmpty()) {
        takePicture()
    } else {
        val hasPreviouslyDeniedPermission = deniedPermissions.any { permission ->
            ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)
        }
        if (hasPreviouslyDeniedPermission) {
            dialogPermissionExplainState.value = true
        } else {
            requestPermissions(context, requestPermissionLauncher)
        }
    }
}

private fun hasCameraHardware(context: Context): Boolean {
    return context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)
}

fun dispatchTakePictureIntent(
    imageUri: Uri,
    takePictureLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>
) {
    val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
    try {
        takePictureLauncher.launch(takePictureIntent)
    } catch (e: ActivityNotFoundException) {
        // TODO: 카메라 전환 오류
    }
}

fun requestPermissions(
    context: Context,
    requestPermissionLauncher: ManagedActivityResultLauncher<Array<String>, Map<String, @JvmSuppressWildcards Boolean>>,
) {
    val deniedPermissions = REQUESTED_PERMISSIONS.filter { permission ->
        ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED
    }
    requestPermissionLauncher.launch(deniedPermissions.toTypedArray())
}

private val REQUESTED_PERMISSIONS by lazy {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        listOf(
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_MEDIA_LOCATION,
        )
    } else {
        listOf(
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
        )
    }
}
