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
            Log.d("FFFF", "우리 앱의 권한 설명")
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

        val isAlreadyRequest = permissionsToRequest.any { permission ->
            ActivityCompat.shouldShowRequestPermissionRationale(this, permission)
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
                    requestPermissionLauncher.launch(permissionsToRequest.toTypedArray())
                }
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
