package com.and04.naturealbum.ui.mypage

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.and04.naturealbum.data.dto.MyFriend
import com.and04.naturealbum.ui.savephoto.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel @Inject constructor(
    private val authenticationManager: AuthenticationManager
) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState>(setInitUiState())
    val uiState: StateFlow<UiState> = _uiState

    private val _myFriends = MutableStateFlow<List<MyFriend>>(emptyList())
    val myFriend: StateFlow<List<MyFriend>> = _myFriends

    fun signInWithGoogle(context: Context) {
        authenticationManager.signInWithGoogle(context).onEach { response ->
            when (response) {
                is AuthResponse.Success -> {
                    _uiState.emit(UiState.Success)
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun setInitUiState(): UiState {
        return if (UserManager.isSignIn()) {
            UiState.Success
        } else {
            UiState.Idle
        }
    }
}