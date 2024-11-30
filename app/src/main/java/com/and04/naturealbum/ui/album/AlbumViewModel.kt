package com.and04.naturealbum.ui.album

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.and04.naturealbum.data.dto.AlbumDto
import com.and04.naturealbum.data.repository.DataRepository
import com.and04.naturealbum.ui.model.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlbumViewModel @Inject constructor(
    private val repository: DataRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<List<AlbumDto>>>(UiState.Idle)
    val uiState: StateFlow<UiState<List<AlbumDto>>> = _uiState

    fun loadAlbums() {
        viewModelScope.launch {
            _uiState.emit(UiState.Loading)
            Log.d("getAllAlbum", "!!!:${repository.getAllAlbum()}")
            _uiState.emit(UiState.Success(data = repository.getAllAlbum()))
        }
    }
}
