package com.and04.naturealbum.ui.albumfolder

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.and04.naturealbum.data.datastore.DataStoreManager
import com.and04.naturealbum.data.repository.DataRepository
import com.and04.naturealbum.data.room.PhotoDetail
import com.and04.naturealbum.ui.model.AlbumFolderData
import com.and04.naturealbum.ui.model.UiState
import com.and04.naturealbum.utils.NetworkState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlbumFolderViewModel @Inject constructor(
    private val roomRepository: DataRepository,
    private val syncDataStore: DataStoreManager,
) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<AlbumFolderData>>(UiState.Idle)
    val uiState: StateFlow<UiState<AlbumFolderData>> = _uiState

    fun loadFolderData(labelId: Int) {
        viewModelScope.launch {
            _uiState.emit(UiState.Loading)

            val labelJob = async {
                roomRepository.getLabelById(id = labelId)
            }

            val photoDetailsJob = async {
                roomRepository.getPhotoDetailsUriByLabelId(labelId = labelId).reversed()
            }

            val labelData = labelJob.await()
            val photoDetailsData = photoDetailsJob.await()

            _uiState.emit(UiState.Success(AlbumFolderData(labelData, photoDetailsData)))
        }
    }

    fun deletePhotos(photoDetails: Set<PhotoDetail>) {
        viewModelScope.launch {
            val currentData = (_uiState.value as? UiState.Success)?.data
            if (currentData != null) {
                val updatedPhotoDetails = currentData.photoDetails.toMutableList()
                photoDetails.forEach { photoDetail ->
                    roomRepository.deleteImage(photoDetail) // Room에서 삭제
                    syncDataStore.setDeletedFileName(photoDetail.fileName) // 삭제 정보를 DataStore에 저장
                    if (NetworkState.getNetWorkCode() != 0) {
                        //todo 네트워크 연결 시 파베에서도 사진 삭제
                    }
                    updatedPhotoDetails.remove(photoDetail)
                }
                _uiState.emit(UiState.Success(currentData.copy(photoDetails = updatedPhotoDetails)))
            }
        }
    }
}
