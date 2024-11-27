package com.and04.naturealbum.ui.maps

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.and04.naturealbum.data.dto.FirebaseFriend
import com.and04.naturealbum.data.dto.FirebaseLabelResponse
import com.and04.naturealbum.data.dto.FirebasePhotoInfoResponse
import com.and04.naturealbum.data.repository.DataRepository
import com.and04.naturealbum.data.repository.FireBaseRepository
import com.and04.naturealbum.data.room.Label
import com.and04.naturealbum.data.room.PhotoDetail
import com.and04.naturealbum.utils.toLocalDateTime
import com.naver.maps.geometry.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapScreenViewModel @Inject constructor(
    private val localRepository: DataRepository,
    private val fireBaseRepository: FireBaseRepository
) : ViewModel() {
    private val _photos = MutableStateFlow<List<PhotoItem>>(emptyList())
    val photos: StateFlow<List<PhotoItem>> = _photos

    private val _friends = MutableStateFlow<List<FirebaseFriend>>(emptyList())
    val friends: StateFlow<List<FirebaseFriend>> = _friends

    private val _friendsPhotos = MutableStateFlow<List<List<PhotoItem>>>(emptyList())
    val friendsPhotos: StateFlow<List<List<PhotoItem>>> = _friendsPhotos

    init {
        viewModelScope.launch {
            val fetchPhotos = async { localRepository.getAllPhotoDetail() }
            val fetchLabels = localRepository.getLabels()

            _photos.emit(fetchPhotos.await().toPhotoItems(fetchLabels))
        }
    }

    fun fetchFriendsPhotos(friends: List<String>) {
        viewModelScope.launch {
            try {
                val photos = async { fireBaseRepository.getPhotos(friends) }
                val labels = fireBaseRepository.getLabels(friends)
                _friendsPhotos.emit(
                    photos.await().map { (uid, photos) ->
                        photos.toFriendPhotoItems(labels.getValue(uid))
                    }
                )
            } catch (e: Exception) {
                Log.d("FriendViewModel", _friendsPhotos.value.toString())
            }
        }
    }

    fun fetchFriends(uid: String) {
        viewModelScope.launch {
            try {
                val friends = fireBaseRepository.getFriends(uid)
                _friends.value = friends
                Log.d("FriendViewModel", "친구 목록: ${_friends.value}")
            } catch (e: Exception) {
                Log.d("FriendViewModel", e.toString())
            }
        }
    }
}

// Room Data -> UI Data
fun Label.toLabelItem() = LabelItem(name, backgroundColor)
fun List<PhotoDetail>.toPhotoItems(labels: List<Label>): List<PhotoItem> {
    val labelMap = labels.associate { roomLabel -> roomLabel.id to roomLabel.toLabelItem() }
    return map { photoDetail ->
        PhotoItem(
            photoDetail.photoUri,
            LatLng(photoDetail.latitude, photoDetail.longitude),
            labelMap.getValue(photoDetail.labelId),
            photoDetail.datetime
        )
    }
}

// FireBase Data -> UI Data
fun FirebaseLabelResponse.toLabelItem() = LabelItem(labelName, backgroundColor)
fun List<FirebasePhotoInfoResponse>.toFriendPhotoItems(labels: List<FirebaseLabelResponse>): List<PhotoItem> {
    val labelMap =
        labels.associate { firebaseLabel -> firebaseLabel.labelName to firebaseLabel.toLabelItem() }
    return map { firebasePhotoInfo ->
        PhotoItem(
            firebasePhotoInfo.uri,
            LatLng(firebasePhotoInfo.latitude!!, firebasePhotoInfo.longitude!!),
            labelMap.getValue(firebasePhotoInfo.label),
            firebasePhotoInfo.datetime.toLocalDateTime()
        )
    }
}
