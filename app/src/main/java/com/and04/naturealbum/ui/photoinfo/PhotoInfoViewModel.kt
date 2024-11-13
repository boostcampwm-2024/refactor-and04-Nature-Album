package com.and04.naturealbum.ui.photoinfo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.and04.naturealbum.data.repository.DataRepository
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
) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState

    private val _label = MutableStateFlow(Label.emptyLabel())
    val label: StateFlow<Label> = _label

    private val _photoDetail = MutableStateFlow(PhotoDetail.emptyPhotoDetail())
    val photoDetail: StateFlow<PhotoDetail> = _photoDetail

    fun loadPhotoDetail(id: Int) {
        viewModelScope.launch {
            _uiState.emit(UiState.Loading)

            val photoDetailData = roomRepository.getPhotoDetailById(id)
            _photoDetail.emit(photoDetailData)

            _label.emit(roomRepository.getLabelById(photoDetailData.labelId))

            _uiState.emit(UiState.Success)
        }
    }
}
