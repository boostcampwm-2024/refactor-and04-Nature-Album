package com.and04.naturealbum.ui.savephoto

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.and04.naturealbum.data.repository.DataRepository
import com.and04.naturealbum.data.room.Album
import com.and04.naturealbum.data.room.Label
import com.and04.naturealbum.data.room.PhotoDetail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject

sealed class UiState {
    data object Idle : UiState()
    data object Loading : UiState()
    data object Success : UiState()
}

@HiltViewModel
class SavePhotoViewModel @Inject constructor(
    private val repository: DataRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState

    fun savePhoto(uri: String, label: Label, description: String) {
        _uiState.value = UiState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            val labelId =
                if (label.id == 0) repository.insertLabel(label).toInt()
                else label.id

            val album = async { repository.getAlbumByLabelId(labelId) }
            val photoDetailId = async {
                repository.insertPhoto(
                    PhotoDetail(
                        labelId = labelId,
                        photoUri = uri,
                        location = "", // TODO
                        description = description,
                        datetime = LocalDateTime.now(ZoneId.of("UTC")),
                    )
                )
            }
            album.await().run {
                val t = toString()
                if (isEmpty()) repository.insertPhotoInAlbum(
                    Album(
                        labelId = labelId,
                        photoDetailId = photoDetailId.await().toInt()
                    )
                )
                // TODO: 대표 이미지 수정
            }
            _uiState.emit(UiState.Success)
        }
    }

}
