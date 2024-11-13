package com.and04.naturealbum.ui.mypage

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel @Inject constructor(
    private val authenticationManager: AuthenticationManager
) : ViewModel() {

    fun signInWithGoogle(context: Context) {
        authenticationManager.signInWithGoogle(context).onEach { response ->
            when (response) {
                is AuthResponse.Success -> {
                    val token = response.token // token
                }
            }
        }.launchIn(viewModelScope)
    }
}