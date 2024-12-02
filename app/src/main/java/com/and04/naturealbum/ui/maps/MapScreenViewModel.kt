package com.and04.naturealbum.ui.maps

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.and04.naturealbum.data.dto.FirebaseFriend
import com.and04.naturealbum.data.repository.DataRepository
import com.and04.naturealbum.data.repository.FireBaseRepository
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
    private var myPhotos = listOf<PhotoItem>()
    private val _photosByUid = MutableStateFlow<Map<String,List<PhotoItem>>>(emptyMap())
    val photosByUid: StateFlow<Map<String,List<PhotoItem>>> = _photosByUid

    private val _friends = MutableStateFlow<List<FirebaseFriend>>(emptyList())
    val friends: StateFlow<List<FirebaseFriend>> = _friends

    init {
        viewModelScope.launch {
            val fetchPhotos = async { localRepository.getAllPhotoDetail() }
            val fetchLabels = localRepository.getLabels()
            myPhotos = fetchPhotos.await().toPhotoItems(fetchLabels)
            _photosByUid.emit(mapOf("" to myPhotos))
        }
    }

    fun fetchFriendsPhotos(friends: List<String>) {
        viewModelScope.launch {
            try {
                val photos = async { fireBaseRepository.getPhotos(friends) }
                val labels = fireBaseRepository.getLabels(friends)
                _photosByUid.emit(
                    mapOf("" to myPhotos) +
                    photos.await().mapValues { (uid, photos) ->
                        photos.toFriendPhotoItems(labels.getValue(uid))
                    }
                )
            } catch (e: Exception) {
                Log.e("MapScreenViewModel", e.toString())
            }
        }
    }

    fun fetchFriends(uid: String) {
        viewModelScope.launch {
            fireBaseRepository.getFriendsAsFlow(uid).collect { friends ->
                _friends.value = friends
            }
        }
    }
}
