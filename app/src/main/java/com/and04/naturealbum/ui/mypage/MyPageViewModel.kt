package com.and04.naturealbum.ui.mypage

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
    private val authenticationManager: AuthenticationManager,
    private val userManager: UserManager
) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
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

    init {
        setInitUiState()
    }

    fun signInWithGoogle() {
        _uiState.value = UiState.Loading
        authenticationManager.signInWithGoogle().onEach { response ->
            when (response) {
                is AuthResponse.Success -> {
                    setUserInfo()
                    _uiState.emit(UiState.Success)
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun setInitUiState() {
        if (userManager.isSignIn()) {
            setUserInfo()
            _uiState.value = UiState.Success
        }
    }

    private fun setUserInfo() {
        val user = userManager.getUser()
        _userEmail.value = user?.email
        _userPhotoUrl.value = user?.photoUrl.toString()
        _userDisplayName.value = user?.displayName
        _userUid.value = user?.uid
    }
}
