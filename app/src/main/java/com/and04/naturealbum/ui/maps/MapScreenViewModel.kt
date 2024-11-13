package com.and04.naturealbum.ui.maps

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.and04.naturealbum.data.repository.DataRepository
import com.and04.naturealbum.data.room.PhotoDetail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapScreenViewModel @Inject constructor(
    private val repository: DataRepository
) : ViewModel() {
    private val _photos = MutableStateFlow(emptyList<PhotoDetail>())
    val photos: StateFlow<List<PhotoDetail>> = _photos

    init {
        fetchPhotos()
    }

    private fun fetchPhotos() {
        viewModelScope.launch(Dispatchers.IO) {
            _photos.emit(repository.getAllPhotoDetail())
        }
    }
}
