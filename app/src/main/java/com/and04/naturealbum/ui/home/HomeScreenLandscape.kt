package com.and04.naturealbum.ui.home

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import com.and04.naturealbum.R
import com.and04.naturealbum.ui.LocationHandler
import com.and04.naturealbum.ui.component.ClippingButtonWithFile
import com.and04.naturealbum.ui.component.MyTopAppBar

@Composable
fun HomeScreenLandscape(
    locationHandler: LocationHandler,
    takePicture: () -> Unit,
    onNavigateToAlbum: () -> Unit,
) {
    val context = LocalContext.current
    val activity = context as? Activity ?: return

    var permissionDialogState by remember { mutableStateOf(PermissionDialogState.None) }

    val locationSettingsLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartIntentSenderForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                takePicture()
            }
        }


    val requestPermissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val deniedPermissions = permissions.filter { permission -> !permission.value }.keys
            when {
                deniedPermissions.isEmpty() -> {
                    locationHandler.checkLocationSettings(
                        takePicture = takePicture,
                        showGPSActivationDialog = { intentSenderRequest ->
                            locationSettingsLauncher.launch(intentSenderRequest)
                        }
                    )
                }

                else -> {
                    val hasPreviouslyDeniedPermission = deniedPermissions.any { permission ->
                        ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)
                    }

                    permissionDialogState = if (hasPreviouslyDeniedPermission) {
                        PermissionDialogState.Explain
                    } else {
                        PermissionDialogState.GoToSettings
                    }
                }
            }
        }

    val permissionHandler = remember {
        PermissionHandler(
            context = context,
            activity = activity,
            allPermissionGranted = {
                locationHandler.checkLocationSettings(
                    takePicture = takePicture,
                    showGPSActivationDialog = { intentSenderRequest ->
                        locationSettingsLauncher.launch(intentSenderRequest)
                    }
                )
            },
            onRequestPermission = { deniedPermissions ->
                requestPermissionLauncher.launch(deniedPermissions)
            },
            showPermissionExplainDialog = { permissionDialogState = PermissionDialogState.Explain }
        )
    }

    Scaffold(topBar = { MyTopAppBar() }) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
        ) {
            MainBackground(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            )

            ClippingButtonWithFile(
                context = context,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                isFromAssets = true,
                fileNameOrResId = MAP_BUTTON_BACKGROUND_OUTLINE_SVG,
                text = stringResource(R.string.home_navigate_to_map),
                textColor = Color.Black,
                imageResId = R.drawable.btn_home_menu_map_background,
                onClick = { /* TODO: Navigation 연결 */ }
            )

            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(24.dp)
            )

            NavigateContent(
                modifier = Modifier
                    .weight(1.17f)
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                permissionHandler = permissionHandler,
                onNavigateToAlbum = onNavigateToAlbum
            )
        }
    }

    PermissionDialogs(
        permissionDialogState = permissionDialogState,
        onDismiss = { permissionDialogState = PermissionDialogState.None },
        onRequestPermission = {
            permissionHandler.requestPermissions()
        },
        onGoToSettings = {
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", context.packageName, null)
                context.startActivity(this)
            }
        }
    )
}
