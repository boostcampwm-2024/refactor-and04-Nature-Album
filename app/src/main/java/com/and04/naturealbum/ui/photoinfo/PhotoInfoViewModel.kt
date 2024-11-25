package com.and04.naturealbum.ui.photoinfo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.and04.naturealbum.data.repository.DataRepository
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
) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<AlbumData>>(UiState.Idle)
    val uiState: StateFlow<UiState<AlbumData>> = _uiState

    fun loadPhotoDetail(id: Int) {
        viewModelScope.launch {
            _uiState.emit(UiState.Loading)

            val photoDetail = roomRepository.getPhotoDetailById(id)
            val label = roomRepository.getLabelById(photoDetail.labelId)

            _uiState.emit(UiState.Success(AlbumData(label, photoDetail)))
        }
    }
}
