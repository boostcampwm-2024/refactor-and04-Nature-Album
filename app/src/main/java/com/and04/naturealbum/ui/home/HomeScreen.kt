package com.and04.naturealbum.ui.home

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import com.and04.naturealbum.R
import com.and04.naturealbum.ui.component.MyDialog
import com.and04.naturealbum.ui.component.MyTopAppBar
import com.and04.naturealbum.ui.component.RoundedShapeButton
import com.and04.naturealbum.ui.theme.NatureAlbumTheme

@Composable
fun HomeScreen(
    takePicture: () -> Unit,
    onNavigateToAlbum: () -> Unit = {},
) {
    val context = LocalContext.current
    val activity = context as? Activity ?: return

    var dialogPermissionGoToSettingsState by remember { mutableStateOf(false) }
    val dialogPermissionExplainState = remember { mutableStateOf(false) }

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
                isGrantedGPS(intentSenderRequest)
            },
            {
                takePicture()
            }
        ).startLocationUpdates()
    }

    val requestPermissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val deniedPermissions = permissions.filter { permission -> !permission.value }.keys
            if (deniedPermissions.isEmpty()) {
                allPermissionGranted()
            } else {
                val hasPreviouslyDeniedPermission = deniedPermissions.any { permission ->
                    ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)
                }
                if (hasPreviouslyDeniedPermission) {
                    dialogPermissionExplainState.value = true
                } else {
                    dialogPermissionGoToSettingsState = true
                }
            }
        }

    val onRequestPermission = { deniedPermissions: Array<String> ->
        requestPermissionLauncher.launch(deniedPermissions)
    }

    val permissionHandler = remember {
        PermissionHandler(
            context = context,
            activity = activity,
            allPermissionGranted = { allPermissionGranted() },
            onRequestPermission = onRequestPermission,
            dialogPermissionExplainState = dialogPermissionExplainState
        )
    }

    Scaffold(topBar = { MyTopAppBar() }) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            InfoContent(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
            )
            NavigateContent(
                permissionHandler = permissionHandler,
                modifier = Modifier
                    .weight(2f)
                    .fillMaxSize(),
                onNavigateToAlbum = onNavigateToAlbum
            )
        }
    }

    if (dialogPermissionExplainState.value) {
        MyDialog(
            onConfirmation = {
                dialogPermissionExplainState.value = false
                permissionHandler.requestPermissions()
            },
            onDismissRequest = { dialogPermissionExplainState.value = false },
            dialogText = R.string.Home_Screen_permission_explain,
        )
    }

    if (dialogPermissionGoToSettingsState) {
        MyDialog(
            onConfirmation = {
                dialogPermissionGoToSettingsState = false
                val intent =
                    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", context.packageName, null)
                    }
                context.startActivity(intent)
            },
            onDismissRequest = { dialogPermissionGoToSettingsState = false },
            dialogText = R.string.Home_Screen_permission_go_to_settings,
        )
    }
}

@Composable
fun InfoContent(modifier: Modifier) {
    RoundedShapeButton("TODO", modifier) { /* TODO */ }
}

@Composable
fun NavigateContent(
    permissionHandler: PermissionHandler,
    modifier: Modifier = Modifier,
    onNavigateToAlbum: () -> Unit,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        RoundedShapeButton("TODO", modifier) { /* TODO */ }

        Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            RoundedShapeButton("TODO", modifier) { onNavigateToAlbum() }
            RoundedShapeButton("TODO", modifier) {
                permissionHandler.onClickCamera()
            }
        }
    }

    Spacer(modifier = Modifier.padding(bottom = 72.dp))
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_NO)
@Composable
fun HomePreview() {
    NatureAlbumTheme {
        HomeScreen({})
    }
}
