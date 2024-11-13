package com.and04.naturealbum.ui.albumfolder

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.and04.naturealbum.data.repository.DataRepository
import com.and04.naturealbum.data.room.Label
import com.and04.naturealbum.data.room.PhotoDetail
import com.and04.naturealbum.ui.savephoto.UiState
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
    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState

    private val _label = MutableStateFlow(Label.emptyLabel())
    val label: StateFlow<Label> = _label

    private val _photoDetails = MutableStateFlow<List<PhotoDetail>>(emptyList())
    val photoDetails: StateFlow<List<PhotoDetail>> = _photoDetails


    fun loadFolderData(labelId: Int) {
        Log.d("FFFF", "loadFolderData")
        viewModelScope.launch {
            _uiState.emit(UiState.Loading)

            val labelData = async {
                _label.emit(roomRepository.getLabelById(id = labelId))
            }

            val photoDetailsData = async {
                _photoDetails.emit(
                    roomRepository.getPhotoDetailsUriByLabelId(labelId = labelId).reversed()
                )
            }

            labelData.await()
            photoDetailsData.await()

            if (labelData.isCompleted && photoDetailsData.isCompleted) {
                _uiState.emit(UiState.Success)
            }
        }
    }
}
