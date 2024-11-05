package com.and04.naturealbum

import android.content.pm.PackageManager
import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Build
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import android.Manifest
import android.content.Context
import androidx.activity.ComponentActivity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.core.app.ActivityCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.and04.naturealbum.ui.theme.NatureAlbumTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(homeViewModel: HomeViewModel = viewModel()) {
    val context = LocalContext.current
    val permissions = getRequestedPermissions()
    val dialogData by homeViewModel.dialogData.collectAsStateWithLifecycle()
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.all { it.value }
        if (allGranted) {
            // 권한이 허용됐을 때 작업 시작
            //onPermissionGranted()
            Log.d("FFFF", "권한 부여 성공 -> 실행")
        } else {
            //설정에서 직접 하기
            Log.d("FFFF", "설정에서 직접하기")
            homeViewModel.showDialog(
                DialogData(
                    text = "설정으로 이동해서 직접 권한을 부여하세요",
                    onNegative = { homeViewModel.closeDialog() },
                )
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.app_name))
                },
                actions = {
                    IconButton(onClick = { /* TODO */ }) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = null /* TODO */
                        )
                    }
                }
            )
        },
    ) { innerPadding ->
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
                modifier = Modifier
                    .weight(2f)
                    .fillMaxSize(),
                { onClickCamera(context, homeViewModel, permissions, requestPermissionLauncher) }
            )

            if (dialogData.text.isNotEmpty() && dialogData.isShow)  MyDialog(dialogData = dialogData)
        }
    }
}

@Composable
fun InfoContent(modifier: Modifier) {
    RoundedShapeButton("TODO", modifier, { /* TODO */ })
}

@Composable
fun NavigateContent(modifier: Modifier, onClickCamera: () -> Unit) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        RoundedShapeButton("TODO", modifier, { /* TODO */ })

        Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            RoundedShapeButton("TODO", modifier, { /* TODO */ })
            RoundedShapeButton("TODO", modifier) { onClickCamera() }
        }
    }

    Spacer(modifier = Modifier.padding(bottom = 72.dp))
}

@Composable
fun RoundedShapeButton(text: String, modifier: Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(10)
    ) {
        Text(
            text = text,
            modifier = Modifier
                .padding(top = 24.dp)
                .align(Alignment.Top)
        )
    }
}

fun onClickCamera(
    context: Context, homeViewModel: HomeViewModel, permissions: List<String>,
    launcher: ManagedActivityResultLauncher<Array<String>, Map<String, @JvmSuppressWildcards Boolean>>
) {
    if (!hasCameraHardware(context)) return
    val permissionsToRequest = permissions.filter { permission ->
        ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED
    }

    if (permissionsToRequest.isEmpty()) {
        // 모든 권한이 허용된 경우
        Log.d("FFFF", "이미 권한 다 있음")
    } else {
        Log.d("FFFF", "우리 앱의 권한 설명")
        homeViewModel.showDialog(
            DialogData(
                text = "우리 앱의 권한에 대해 설명",
                onPositive = {
                    homeViewModel.closeDialog()
                    requestPermissions(context, permissions, homeViewModel, launcher)
                },
                onNegative = { homeViewModel.closeDialog() },
            )
        )
    }
}

private fun hasCameraHardware(context: Context): Boolean {
    return context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)
}

private fun requestPermissions(
    context: Context,
    permissions: List<String>,
    homeViewModel: HomeViewModel,
    launcher: ManagedActivityResultLauncher<Array<String>, Map<String, @JvmSuppressWildcards Boolean>>
) {
    val permissionsToRequest = permissions.filter { permission ->
        ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED
    }

    val isAlreadyRequest = permissionsToRequest.any { permission ->
        (context as? ComponentActivity)?.let {
            ActivityCompat.shouldShowRequestPermissionRationale(it, permission)
        } ?: false
    }

    when {
        permissionsToRequest.isEmpty() -> {
            // 이미 모든 권한 허용
        }

        else -> {
            if (isAlreadyRequest) {
                Log.d("FFFF", "이미 물어봄. 이제 알아서 설정으로")
                homeViewModel.showDialog(
                    DialogData(
                        text = "설정으로 이동해서 직접 권한을 부여하세요",
                        onNegative = { homeViewModel.closeDialog() },
                    )
                )
            } else {
                Log.d("FFFF", "처음이니 물어보기^^")
                launcher.launch(permissionsToRequest.toTypedArray())
            }
        }
    }
}

private fun getRequestedPermissions(): List<String> {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
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


@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_NO)
@Composable
fun HomePreview() {
    NatureAlbumTheme {
        HomeScreen()
    }
}