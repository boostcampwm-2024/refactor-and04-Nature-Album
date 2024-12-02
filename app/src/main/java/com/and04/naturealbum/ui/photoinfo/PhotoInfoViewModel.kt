package com.and04.naturealbum.ui.photoinfo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.and04.naturealbum.data.repository.DataRepository
import com.and04.naturealbum.data.repository.RetrofitRepository
import com.and04.naturealbum.data.room.PhotoDetail
import com.and04.naturealbum.ui.model.AlbumData
import com.and04.naturealbum.ui.model.UiState
import com.and04.naturealbum.utils.NetworkState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class PhotoInfoViewModel @Inject constructor(
    private val roomRepository: DataRepository,
    private val retrofitRepository: RetrofitRepository,
    private val dataRepository: DataRepository,
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

    fun setAlbumThumbnail(photoDetailId: Int) {
        viewModelScope.launch {
            roomRepository.updateAlbumPhotoDetailByAlbumId(photoDetailId)
        }
    }

    private suspend fun convertCoordsToAddress(photoDetail: PhotoDetail) {
        val address = withContext(Dispatchers.IO) {
            getAddress(photoDetail)
        }
        _address.emit(address)
    }

    private suspend fun getAddress(photoDetail: PhotoDetail): String {
        val cachedAddress = dataRepository.getAddressByPhotoDetailId(photoDetail.id)
        if (cachedAddress.isNotEmpty()) {
            return cachedAddress
        }

        if (NetworkState.getNetWorkCode() == NetworkState.DISCONNECTED) {
            return cachedAddress
        }

        val newAddress = retrofitRepository.convertCoordsToAddress(
            latitude = photoDetail.latitude,
            longitude = photoDetail.longitude
        )
        dataRepository.updateAddressByPhotoDetailId(newAddress, photoDetail.id)
        return newAddress
    }
}
