package com.and04.naturealbum.ui.mypage

import android.util.Log
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@Composable
fun MyPageScreen() {
    val context = LocalContext.current
    val authenticationManager = remember {
        AuthenticationManager(context)
    }

    val coroutineScope = rememberCoroutineScope()

    Button(onClick = {
        authenticationManager.signInWithGoogle()
            .onEach { response ->
                if (response is AuthResponse.Success) {
                    // TODO()
                }
            }
            .launchIn(coroutineScope)
    }) { }
}