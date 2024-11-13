package com.and04.naturealbum.ui.mypage

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun MyPageScreen(
    myPageViewModel: MyPageViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    Button(onClick = {
        myPageViewModel.signInWithGoogle(context)
    }
    ) { Text("Test Button") }
}
