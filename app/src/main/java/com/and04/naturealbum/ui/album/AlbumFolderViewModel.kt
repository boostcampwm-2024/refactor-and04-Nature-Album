package com.and04.naturealbum.ui.album

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
class AlbumFolderViewModel @Inject constructor(
    private val roomRepository: DataRepository
) : ViewModel() {
    private val _label = MutableStateFlow<Label>(Label.emptyLabel())
    val label: StateFlow<Label> = _label

    private val _photoDetail = MutableStateFlow<PhotoDetail>(PhotoDetail.emptyPhotoDetail())
    val photoDetail: StateFlow<PhotoDetail> = _photoDetail


    fun loadFolderData(labelId: Int){
        viewModelScope.launch {
            launch {
                _label.emit(roomRepository.getLabel(id = labelId))
            }

            launch {
                _photoDetail.emit(roomRepository.getPhotoDetailUriByLabelId(labelId = labelId))
            }
        }
    }
}