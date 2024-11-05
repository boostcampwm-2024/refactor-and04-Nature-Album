package com.and04.naturealbum

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File

class MainActivity : ComponentActivity() {

    private val homeViewModel: HomeViewModel by viewModels()
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val deniedPermissions = permissions.filter { permission -> !permission.value }.keys
            if (deniedPermissions.isEmpty()) {
                dispatchTakePictureIntent()
            } else {
                val hasPreviouslyDeniedPermission = deniedPermissions.any { permission ->
                    ActivityCompat.shouldShowRequestPermissionRationale(this, permission)
                }
                if (hasPreviouslyDeniedPermission) {
                    showPermissionExplainDialog()
                } else {
                    showPermissionGoToSettingsDialog()
                }
            }
        }
    private val takePictureLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                Log.d("FFFF", "사진 촬영 성공")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NatureAlbumApp(homeViewModel) { onClickCamera() }
        }
    }

    private fun onClickCamera() {
        if (!hasCameraHardware()) return
        val deniedPermissions = REQUESTED_PERMISSIONS.filter { permissions ->
            ContextCompat.checkSelfPermission(
                this,
                permissions
            ) != PackageManager.PERMISSION_GRANTED
        }
        if (deniedPermissions.isEmpty()) {
            dispatchTakePictureIntent()
        } else {
            val hasPreviouslyDeniedPermission = deniedPermissions.any { permission ->
                ActivityCompat.shouldShowRequestPermissionRationale(this, permission)
            }
            if (hasPreviouslyDeniedPermission) {
                showPermissionExplainDialog()
            } else {
                requestPermissions()
            }
        }
    }

    private fun hasCameraHardware(): Boolean {
        return applicationContext.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)
    }

    private fun requestPermissions() {
        val deniedPermissions = REQUESTED_PERMISSIONS.filter { permission ->
            ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED
        }
        if (deniedPermissions.isEmpty()) {
            dispatchTakePictureIntent()
        } else {
            requestPermissionLauncher.launch(deniedPermissions.toTypedArray())
        }
    }

    private fun showPermissionExplainDialog() {
        homeViewModel.showDialog(
            DialogData(
                onConfirmation = { requestPermissions() },
                onDismissRequest = { homeViewModel.dismissDialog() },
                dialogText = R.string.main_activity_permission_explain,
            )
        )
    }

    private fun showPermissionGoToSettingsDialog() {
        homeViewModel.showDialog(
            DialogData(
                onConfirmation = {
                    homeViewModel.dismissDialog()
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", packageName, null)
                    }
                    startActivity(intent)
                },
                onDismissRequest = { homeViewModel.dismissDialog() },
                dialogText = R.string.main_activity_permission_go_to_settings,
            )
        )
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val imageFile = File(filesDir, "${System.currentTimeMillis()}.jpg")
        val imageUri = FileProvider.getUriForFile(this, "${packageName}.fileprovider", imageFile)
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        try {
            takePictureLauncher.launch(takePictureIntent)
        } catch (e: ActivityNotFoundException) {
            // display error state to the user
        }
    }

    companion object {
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
    }
}
