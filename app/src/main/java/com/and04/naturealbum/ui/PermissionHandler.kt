package com.and04.naturealbum.ui

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class PermissionHandler(
    private val context: Context,
    private val allPermissionGranted: () -> Unit,
    private val onRequestPermission: (Array<String>) -> Unit,
    private var showPermissionExplainDialog: () -> Unit,
) {
    private fun hasCameraHardware(): Boolean {
        return context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)
    }

    fun checkPermissions(permissions: List<String>) {
        if (permissions == CAMERA_PERMISSIONS && !hasCameraHardware()) return

        val activity = context as? Activity ?: return
        val deniedPermissions = permissions.filter { permission ->
            ContextCompat.checkSelfPermission(
                context,
                permission
            ) != PackageManager.PERMISSION_GRANTED
        }
        if (deniedPermissions.isEmpty()) {
            allPermissionGranted()
        } else {
            val hasPreviouslyDeniedPermission = deniedPermissions.any { permission ->
                ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)
            }
            if (hasPreviouslyDeniedPermission) {
                showPermissionExplainDialog()
            } else {
                requestPermissions(permissions)
            }
        }
    }

    fun requestPermissions(permissions: List<String>) {
        val deniedPermissions = permissions.filter { permission ->
            ContextCompat.checkSelfPermission(
                context,
                permission
            ) != PackageManager.PERMISSION_GRANTED
        }
        onRequestPermission(deniedPermissions.toTypedArray())
    }

    companion object {
        val CAMERA_PERMISSIONS =
            listOf(
                Manifest.permission.CAMERA,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            )
        val MAP_PERMISSIONS =
            listOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            )
    }
}
