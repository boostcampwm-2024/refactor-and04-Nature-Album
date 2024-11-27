package com.and04.naturealbum.ui.mypage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.and04.naturealbum.data.dto.MyFriend
import com.and04.naturealbum.ui.model.UiState
import com.and04.naturealbum.ui.model.UserInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel @Inject constructor(
    private val authenticationManager: AuthenticationManager,
    private val userManager: UserManager
) : ViewModel() {
    private val _uiState = MutableStateFlow(setInitUiState())
    val uiState: StateFlow<UiState<UserInfo>> = _uiState

    fun signInWithGoogle() {
        authenticationManager.signInWithGoogle().onEach { response ->
            when (response) {
                is AuthResponse.Success -> {
                    _uiState.emit(
                        getUserInfoUiState()
                    )
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun getUserInfoUiState(): UiState.Success<UserInfo> {
        val user = UserManager.getUser()
        return UiState.Success(
            UserInfo(
                userEmail = user?.email,
                userPhotoUri = user?.photoUrl.toString(),
                userDisplayName = user?.displayName,
                userUid = user?.uid
            )
        )
    }

    private fun setInitUiState(): UiState<UserInfo> {
        return if (userManager.isSignIn()) {
            getUserInfoUiState()
        } else {
            UiState.Idle
        }
    }
}
