package com.and04.naturealbum.ui.albumfolder

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.and04.naturealbum.data.repository.DataRepository
import com.and04.naturealbum.ui.model.AlbumFolderData
import com.and04.naturealbum.ui.model.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlbumFolderViewModel @Inject constructor(
    private val roomRepository: DataRepository,
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
}
