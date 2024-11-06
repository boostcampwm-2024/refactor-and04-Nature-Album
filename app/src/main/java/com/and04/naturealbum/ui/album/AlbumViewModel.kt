package com.and04.naturealbum.ui.album

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.and04.naturealbum.data.dto.AlbumDto
import com.and04.naturealbum.data.repository.DataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlbumViewModel @Inject constructor(
    private val repository: DataRepository
) : ViewModel() {

    private val _albumList = MutableLiveData<List<AlbumDto>>()
    val albumList: LiveData<List<AlbumDto>> = _albumList

    fun loadAlbums() {
        viewModelScope.launch(Dispatchers.IO) {
            val albums = repository.getALLAlbum()

            val albumDtos = albums.map { album ->
                val labelName = repository.getLabelNameById(album.labelId)
                val photoDetailUri = repository.getPhotoDetailUriByLabelId(album.photoDetailId)
                val labelBackgroundColor = repository.getLabelBackgroundColorById(album.labelId)
                AlbumDto(
                    labelId = album.labelId,
                    labelName = labelName,
                    labelBackgroundColor = labelBackgroundColor,
                    photoDetailUri = photoDetailUri
                )
            }

            _albumList.postValue(albumDtos)
        }
    }


}
