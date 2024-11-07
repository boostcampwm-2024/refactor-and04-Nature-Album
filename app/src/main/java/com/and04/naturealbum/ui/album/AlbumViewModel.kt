package com.and04.naturealbum.ui.album

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.and04.naturealbum.R
import com.and04.naturealbum.data.dto.AlbumDto
import com.and04.naturealbum.data.repository.DataRepository
import com.and04.naturealbum.data.room.Album
import com.and04.naturealbum.data.room.Label
import com.and04.naturealbum.data.room.PhotoDetail
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

    fun initialize(context: Context) {
        createDummyData(context) // TODO: 이후 Room DB에 데이터 들어가 있으면 삭제 예정
        loadAlbums()
    }

    fun loadAlbums() {
        viewModelScope.launch(Dispatchers.IO) {
            _albumList.postValue(repository.getAllAlbum())
        }
    }

    // TODO: 이후 Room DB에 데이터 들어가 있으면 삭제 예정
    fun getDrawableUriString(context: Context, drawableId: Int): String {
        return Uri.parse("android.resource://${context.packageName}/$drawableId").toString()
    }

    // TODO: 이후 Room DB에 데이터 들어가 있으면 삭제 예정
    fun createDummyData(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            // Sample Labels 삽입
            val labels = listOf(
                Label(backgroundColor = "E57373", name = "고양이"),
                Label(backgroundColor = "D1C4E9", name = "강아지"),
                Label(backgroundColor = "C5E1A5", name = "해오라기")
            )
            labels.forEach { label ->
                repository.insertLabel(label)
            }

            val uri01 = getDrawableUriString(context, R.drawable.sample_image_01)
            val uri02 = getDrawableUriString(context, R.drawable.sample_image_02)
            val uri03 = getDrawableUriString(context, R.drawable.sample_image_01)


            val photoDetails = listOf(
                PhotoDetail(
                    labelId = 1,
                    photoUri = uri01,
                    description = "고양이 이미지",
                    location = "Seoul",
                    datetime = "2024-11-06"
                ),
                PhotoDetail(
                    labelId = 2,
                    photoUri = uri02,
                    description = "강아지 이미지",
                    location = "Busan",
                    datetime = "2024-11-06"
                ),
                PhotoDetail(
                    labelId = 3,
                    photoUri = uri03,
                    description = "해오라기 이미지",
                    location = "Jeju",
                    datetime = "2024-11-06"
                )
            )
            photoDetails.forEach { photoDetail ->
                repository.insertPhoto(photoDetail)
            }


            val albums = listOf(
                Album(labelId = 1, photoDetailId = 1),
                Album(labelId = 2, photoDetailId = 2),
                Album(labelId = 3, photoDetailId = 3)
            )
            albums.forEach { album ->
                repository.insertPhotoInAlbum(album)
            }
        }
    }
}
