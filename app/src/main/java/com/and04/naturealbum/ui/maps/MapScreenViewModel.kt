package com.and04.naturealbum.ui.maps

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.and04.naturealbum.data.repository.DataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapScreenViewModel @Inject constructor(
    private val repository: DataRepository
) : ViewModel() {
    private val _photos = MutableStateFlow<List<PhotoItem>>(emptyList())
    val photos: StateFlow<List<PhotoItem>> = _photos

    init {
        viewModelScope.launch {
            val fetchPhotos = async { repository.getAllPhotoDetail() }
            val fetchLabels = repository.getLabels()

            _photos.emit(fetchPhotos.await().toPhotoItems(fetchLabels))
        }
    }
}
