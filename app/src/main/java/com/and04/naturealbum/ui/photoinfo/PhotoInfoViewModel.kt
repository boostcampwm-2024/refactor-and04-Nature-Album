package com.and04.naturealbum.ui.photoinfo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.and04.naturealbum.data.repository.DataRepository
import com.and04.naturealbum.data.repository.RetrofitRepository
import com.and04.naturealbum.data.room.Label
import com.and04.naturealbum.data.room.PhotoDetail
import com.and04.naturealbum.ui.model.AlbumData
import com.and04.naturealbum.ui.model.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PhotoInfoViewModel @Inject constructor(
    private val roomRepository: DataRepository,
    private val retrofitRepository: RetrofitRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<AlbumData>>(UiState.Idle)
    val uiState: StateFlow<UiState<AlbumData>> = _uiState

    private val _address = MutableStateFlow("")
    val address: StateFlow<String> = _address

    fun loadPhotoDetail(id: Int) {
        viewModelScope.launch {
            _uiState.emit(UiState.Loading)

            val photoDetail = roomRepository.getPhotoDetailById(id)
            val label = roomRepository.getLabelById(photoDetail.labelId)

            convertCoordsToAddress(photoDetail = photoDetail)

            _uiState.emit(UiState.Success(AlbumData(label, photoDetail)))
        }
    }

    private suspend fun convertCoordsToAddress(photoDetail: PhotoDetail) {
        val coords = "${photoDetail.longitude}%2C${photoDetail.latitude}"
        retrofitRepository.convertCoordsToAddress(coords = coords)
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
