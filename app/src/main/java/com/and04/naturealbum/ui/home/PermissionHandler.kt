package com.and04.naturealbum.ui.home

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class PermissionHandler(
    private val context: Context,
    private val activity: Activity,
    private val allPermissionGranted: () -> Unit,
    private val onRequestPermission: (Array<String>) -> Unit,
    private var showDialogPermissionExplain: () -> Unit,
) {

    fun onClickCamera() {
        if (!hasCameraHardware()) return

        val deniedPermissions = REQUESTED_PERMISSIONS.filter { permissions ->
            ContextCompat.checkSelfPermission(
                context,
                permissions
            ) != PackageManager.PERMISSION_GRANTED
        }
        if (deniedPermissions.isEmpty()) {
            allPermissionGranted()
        } else {
            val hasPreviouslyDeniedPermission = deniedPermissions.any { permission ->
                ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)
            }
            if (hasPreviouslyDeniedPermission) {
                showDialogPermissionExplain()
            } else {
                requestPermissions()
            }
        }
    }

    private fun hasCameraHardware(): Boolean {
        return context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)
    }

    fun requestPermissions() {
        val deniedPermissions = REQUESTED_PERMISSIONS.filter { permission ->
            ContextCompat.checkSelfPermission(
                context,
                permission
            ) != PackageManager.PERMISSION_GRANTED
        }
        onRequestPermission(deniedPermissions.toTypedArray())
    }

    companion object {
        private val REQUESTED_PERMISSIONS =
            listOf(
                Manifest.permission.CAMERA,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            )
    }
}
