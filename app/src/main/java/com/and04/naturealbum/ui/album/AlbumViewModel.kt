package com.and04.naturealbum.ui.album

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.and04.naturealbum.data.dto.AlbumDto
import com.and04.naturealbum.data.repository.DataRepository
import com.and04.naturealbum.ui.savephoto.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlbumViewModel @Inject constructor(
    private val repository: DataRepository,
) : ViewModel() {
    private val _uiState = MutableLiveData<UiState>(UiState.Idle)
    val uiState: LiveData<UiState> = _uiState

    private val _albumList = MutableLiveData<List<AlbumDto>>()
    val albumList: LiveData<List<AlbumDto>> = _albumList

    fun loadAlbums() {
        viewModelScope.launch {
            _uiState.postValue(UiState.Loading)
            _albumList.postValue(repository.getAllAlbum())
            _uiState.postValue(UiState.Success)
        }

    }
}
