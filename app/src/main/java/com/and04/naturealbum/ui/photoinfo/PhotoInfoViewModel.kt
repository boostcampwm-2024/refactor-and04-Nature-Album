package com.and04.naturealbum.ui.photoinfo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.and04.naturealbum.data.repository.DataRepository
import com.and04.naturealbum.data.repository.ReverseGeocodeRepository
import com.and04.naturealbum.data.room.Label
import com.and04.naturealbum.data.room.PhotoDetail
import com.and04.naturealbum.ui.savephoto.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PhotoInfoViewModel @Inject constructor(
    private val roomRepository: DataRepository,
    private val reverseGeocodeRepository: ReverseGeocodeRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState

    private val _label = MutableStateFlow(Label.emptyLabel())
    val label: StateFlow<Label> = _label

    private val _photoDetail = MutableStateFlow(PhotoDetail.emptyPhotoDetail())
    val photoDetail: StateFlow<PhotoDetail> = _photoDetail

    private val _address = MutableStateFlow("")
    val address: StateFlow<String> = _address

    fun loadPhotoDetail(id: Int) {
        viewModelScope.launch {
            _uiState.emit(UiState.Loading)

            val photoDetailData = roomRepository.getPhotoDetailById(id)
            _photoDetail.emit(photoDetailData)

            convertCoordsToAddress(photoDetail = photoDetailData)

            _label.emit(roomRepository.getLabelById(photoDetailData.labelId))
            _uiState.emit(UiState.Success)
        }
    }

    private suspend fun convertCoordsToAddress(photoDetail: PhotoDetail) {
        val coords = "${photoDetail.longitude}%2C${photoDetail.latitude}"
        reverseGeocodeRepository.convertCoordsToAddress(coords = coords)
            .onSuccess { dto ->
                if (dto.results.isNullOrEmpty()) {
                    _address.emit("${photoDetail.latitude}, ${photoDetail.longitude}")
                    return
                }
                val region = dto.results[0].region
                val address = buildString {
                    append("${region?.area1?.name} ")
                    append("${region?.area2?.name} ")
                    append("${region?.area3?.name} ")
                    append(region?.area4?.name)
                }
                _address.emit(address)
            }
            .onFailure {
                _address.emit("${photoDetail.latitude}, ${photoDetail.longitude}")
            }
    }
}
