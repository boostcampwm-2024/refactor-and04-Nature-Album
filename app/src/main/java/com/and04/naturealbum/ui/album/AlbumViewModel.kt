package com.and04.naturealbum.ui.album

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.and04.naturealbum.data.dto.AlbumDto
import com.and04.naturealbum.data.repository.DataRepository
import com.and04.naturealbum.ui.model.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlbumViewModel @Inject constructor(
    private val repository: DataRepository,
) : ViewModel() {

    val albumList: StateFlow<List<AlbumDto>> = repository.getAllAlbum().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )
}
