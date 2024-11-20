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

    private val _userEmail = MutableStateFlow<String?>(null)
    val userEmail: StateFlow<String?> = _userEmail

    private val _userPhotoUrl = MutableStateFlow<String?>(null)
    val userPhotoUrl: StateFlow<String?> = _userPhotoUrl

    private val _userDisplayName = MutableStateFlow<String?>(null)
    val userDisplayName: StateFlow<String?> = _userDisplayName

    private val _userUid = MutableStateFlow<String?>(null)
    val userUid: StateFlow<String?> = _userUid

    fun signInWithGoogle(context: Context) {
        authenticationManager.signInWithGoogle(context).onEach { response ->
            when (response) {
                is AuthResponse.Success -> {
                    val user = UserManager.getUser()
                    _userEmail.value = user?.email
                    _userPhotoUrl.value = user?.photoUrl.toString()
                    _userDisplayName.value = user?.displayName
                    _userUid.value = user?.uid
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
