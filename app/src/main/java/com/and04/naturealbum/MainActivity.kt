package com.and04.naturealbum

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.and04.naturealbum.ui.theme.NatureAlbumTheme
import java.io.File

class MainActivity : ComponentActivity() {
    private val homeViewModel: HomeViewModel by viewModels()
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions: Map<String, Boolean> ->
            val deniedPermissions = permissions.filter { !it.value }.keys
            if (deniedPermissions.isEmpty()) {
                // 권한이 허용됐을 때 작업 시작
                //onPermissionGranted()
                Log.d("FFFF", "권한 부여 성공 -> 실행")

            } else {
                // 사용자가 권한을 거절했을 때
                val hasPreviouslyDeniedPermission = deniedPermissions.any { permission: String ->
                    ActivityCompat.shouldShowRequestPermissionRationale(this, permission)
                }
                if (hasPreviouslyDeniedPermission) {
                    // 사용자가 단순히 권한을 거절했을 때  -> 교육용 팝업
                    Log.d("FFFF","사용자가 단순히 권한을 거절했을 때  -> 교육용 팝업")
                    homeViewModel.showDialog(
                        DialogData(
                            text = "우리 앱의 권한에 대해 설명",
                            onPositive = {
                                homeViewModel.closeDialog()
                                requestPermissions()
                            },
                            onNegative = { homeViewModel.closeDialog() },
                        )
                    )

                } else {
                    // 사용자가 '다시 묻지 않기' 를 선택 했을 때 -> 설정으로 안내
                    Log.d("FFFF", "설정에서 직접하기")
                    homeViewModel.showDialog(
                        DialogData(
                            text = "설정으로 이동해서 직접 권한을 부여하세요",
                            onNegative = { homeViewModel.closeDialog() },
                        )
                    )
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
            NatureAlbumTheme {
                MyNav(onClickCamera = { onClickCamera() }, homeViewModel = homeViewModel)
            }
        }
    }

    private fun onClickCamera() {
        if (!hasCameraHardware()) return
        val permissionsToRequest = REQUESTED_PERMISSIONS.filter { permission ->
            ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED
        }
        if (permissionsToRequest.isEmpty()) {
            // 모든 권한이 허용된 경우
            Log.d("FFFF", "이미 권한 다 있음")
            dispatchTakePictureIntent()
        } else {
            val hasPreviouslyDeniedPermission = permissionsToRequest.any { permission: String ->
                ActivityCompat.shouldShowRequestPermissionRationale(this, permission)
            }
            if (hasPreviouslyDeniedPermission) {
                // 이전에 권한을 거절한 경험이 있음 -> 권한이 필요한 이유 설명 -> 다시 권한 요청
                Log.d("FFFF","이전에 권한을 거절한 경험이 있음 -> 권한이 필요한 이유 설명 -> 다시 권한 요청")
                homeViewModel.showDialog(
                    DialogData(
                        text = "우리 앱의 권한에 대해 설명",
                        onPositive = {
                            homeViewModel.closeDialog()
                            requestPermissions()
                        },
                        onNegative = { homeViewModel.closeDialog() },
                    )
                )
            } else {
                // 사용자가 이전에 권한을 거절한 경험이 없거나 '다시 묻지 않음` 을 선택했을 경우
                requestPermissions()
            }
        }
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

    private fun hasCameraHardware(): Boolean {
        return applicationContext.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)
    }

    private fun requestPermissions() {
        val permissionsToRequest = REQUESTED_PERMISSIONS.filter { permission ->
            ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED
        }
        when {
            permissionsToRequest.isEmpty() -> {
                // 이미 모든 권한 허용
            }

            else -> {
                requestPermissionLauncher.launch(permissionsToRequest.toTypedArray())
            }
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
