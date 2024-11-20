package com.and04.naturealbum.ui.album

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.and04.naturealbum.data.dto.AlbumDto
import com.and04.naturealbum.data.repository.DataRepository
import com.and04.naturealbum.ui.savephoto.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlbumViewModel @Inject constructor(
    private val repository: DataRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState

    private val _albumList = MutableStateFlow<List<AlbumDto>>(emptyList())
    val albumList: StateFlow<List<AlbumDto>> = _albumList

    init {
        loadAlbums()
    }


    fun loadAlbums() {
        viewModelScope.launch {
            _uiState.emit(UiState.Loading)
            _albumList.emit(repository.getAllAlbum())
            _uiState.emit(UiState.Success)
        }
    }
}
