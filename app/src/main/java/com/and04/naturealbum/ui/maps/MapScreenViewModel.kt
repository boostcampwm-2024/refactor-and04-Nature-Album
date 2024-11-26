package com.and04.naturealbum.ui.maps

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.and04.naturealbum.data.repository.DataRepository
import com.and04.naturealbum.data.room.Label
import com.and04.naturealbum.data.room.PhotoDetail
import com.naver.maps.geometry.LatLng
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
