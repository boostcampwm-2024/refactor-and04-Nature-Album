package com.and04.naturealbum.ui.friend

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.and04.naturealbum.data.dto.FirebaseFriend
import com.and04.naturealbum.data.dto.FirebaseFriendRequest
import com.and04.naturealbum.data.dto.FirestoreUser
import com.and04.naturealbum.data.dto.FirestoreUserWithStatus
import com.and04.naturealbum.data.repository.FireBaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FriendViewModel @Inject constructor(
    private val fireBaseRepository: FireBaseRepository,
) : ViewModel() {

    private val _friendRequests = MutableStateFlow<List<FirebaseFriendRequest>>(emptyList())
    val friendRequests: StateFlow<List<FirebaseFriendRequest>> = _friendRequests

    private val _receivedFriendRequests = MutableStateFlow<List<FirebaseFriendRequest>>(emptyList())
    val receivedFriendRequests: StateFlow<List<FirebaseFriendRequest>> = _receivedFriendRequests

    private val _friends = MutableStateFlow<List<FirebaseFriend>>(emptyList())
    val friends: StateFlow<List<FirebaseFriend>> = _friends

    private val _allUsersWithStatus = MutableStateFlow<List<FirestoreUserWithStatus>>(emptyList())
    val allUsersWithStatus: StateFlow<List<FirestoreUserWithStatus>> = _allUsersWithStatus

    private val _operationStatus = MutableStateFlow<String>("")
    val operationStatus: StateFlow<String> = _operationStatus

    fun fetchAllUsersInfo(uid: String) {
        viewModelScope.launch {
            try {
                val users = fireBaseRepository.getAllUsersInfo(uid)
                _allUsersWithStatus.value = users
                _operationStatus.value = "모든 사용자 정보 가져오기 성공."

                users.forEach { user ->
                    Log.d(
                        "FriendViewModel",
                        "User: ${user.user.displayName}, Email: ${user.user.email}, Status: ${user.status}"
                    )
                }
            } catch (e: Exception) {
                _operationStatus.value = "모든 사용자 정보를 가져오는 데 실패했습니다: ${e.message}"
                Log.d("FriendViewModel", _operationStatus.value)
            }
        }
    }

    fun fetchFriendRequests(uid: String) {
        viewModelScope.launch {
            try {
                val requests = fireBaseRepository.getFriendRequests(uid)
                _friendRequests.value = requests
                _operationStatus.value = "친구 요청 목록을 성공적으로 가져왔습니다."
                Log.d("FriendViewModel", "친구 요청 목록: ${_friendRequests.value}")
            } catch (e: Exception) {
                _operationStatus.value = "친구 요청 목록을 가져오는 데 실패했습니다: ${e.message}"
                Log.d("FriendViewModel", _operationStatus.value)
            }
        }
    }

    fun fetchReceivedFriendRequests(uid: String) {
        viewModelScope.launch {
            try {
                val requests = fireBaseRepository.getReceivedFriendRequests(uid)
                _receivedFriendRequests.value = requests
                _operationStatus.value = " 받은 친구 요청 목록을 성공적으로 가져왔습니다"
                Log.d("FriendViewModel", "받은 친구 요청 목록: ${_receivedFriendRequests.value}")
            } catch (e: Exception) {
                _operationStatus.value = "받은 친구 요청 목록을 가져오는 데 실패했습니다: ${e.message}"
                Log.d("FriendViewModel", _operationStatus.value)
            }
        }
    }

    fun fetchFriends(uid: String) {
        viewModelScope.launch {
            try {
                val friends = fireBaseRepository.getFriends(uid)
                _friends.value = friends
                _operationStatus.value = "친구 목록을 성공적으로 가져왔습니다"
                Log.d("FriendViewModel", "친구 목록: ${_friends.value}")
            } catch (e: Exception) {
                _operationStatus.value = "친구 목록을 가져오는 데 실패했습니다: ${e.message}"
                Log.d("FriendViewModel", _operationStatus.value)
            }
        }
    }

    fun sendFriendRequest(uid: String, targetUid: String) {
        viewModelScope.launch {
            val success = fireBaseRepository.sendFriendRequest(uid, targetUid)
            _operationStatus.value =
                if (success) "친구 요청이 성공적으로 전송되었습니다." else "친구 요청 전송에 실패했습니다."
            Log.d("FriendViewModel", _operationStatus.value)
        }
    }

    fun acceptFriendRequest(uid: String, targetUid: String) {
        viewModelScope.launch {
            val success = fireBaseRepository.acceptFriendRequest(uid, targetUid)
            _operationStatus.value =
                if (success) "친구 요청을 수락했습니다." else "친구 요청 수락에 실패했습니다."
            Log.d("FriendViewModel", _operationStatus.value)
        }
    }

    fun rejectFriendRequest(uid: String, targetUid: String) {
        viewModelScope.launch {
            val success = fireBaseRepository.rejectFriendRequest(uid, targetUid)
            _operationStatus.value =
                if (success) "친구 요청을 거절했습니다." else "친구 요청 거절에 실패했습니다."
            Log.d("FriendViewModel", _operationStatus.value)
        }
    }

    fun setupTestData() {
        viewModelScope.launch {
            // 더미 유저 추가
            listOf(
                FirestoreUser(
                    uid = "yujin",
                    displayName = "YujinDisplay",
                    email = "yujin@example.com",
                    photoUrl = "https://example.com/yujin.jpg"
                ),
                FirestoreUser(
                    uid = "and04",
                    displayName = "And04Display",
                    email = "and04@example.com",
                    photoUrl = "https://example.com/and04.jpg"
                ),
                FirestoreUser(
                    uid = "cat",
                    displayName = "CatDisplay",
                    email = "cat@example.com",
                    photoUrl = "https://example.com/cat.jpg"
                ),
                FirestoreUser(
                    uid = "jeong",
                    displayName = "JeongDisplay",
                    email = "jeong@example.com",
                    photoUrl = "https://example.com/jeong.jpg"
                )
            ).forEach { user ->
                fireBaseRepository.createUserIfNotExists(
                    uid = user.uid,
                    displayName = user.displayName,
                    email = user.email,
                    photoUrl = user.photoUrl
                )
            }
        }
    }
}
