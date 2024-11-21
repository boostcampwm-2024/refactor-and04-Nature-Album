package com.and04.naturealbum.ui.maps

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.and04.naturealbum.data.repository.DataRepository
import com.and04.naturealbum.data.room.Label
import com.and04.naturealbum.data.room.PhotoDetail
import dagger.hilt.android.lifecycle.HiltViewModel
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

    private val _labels = MutableStateFlow(emptyList<Label>())
    val labels: StateFlow<List<Label>> = _labels

    init {
        fetchPhotos()
        fetchLabels()
    }

    private fun fetchPhotos() {
        viewModelScope.launch {
            _photos.emit(repository.getAllPhotoDetail())
        }
    }

    private fun fetchLabels() {
        viewModelScope.launch {
            _labels.emit(repository.getLabels())
        }
    }
}
