package com.and04.naturealbum.data.repository

import com.and04.naturealbum.data.room.Album
import com.and04.naturealbum.data.room.AlbumDao
import com.and04.naturealbum.data.room.Label
import com.and04.naturealbum.data.room.LabelDao
import com.and04.naturealbum.data.room.PhotoDetail
import com.and04.naturealbum.data.room.PhotoDetailDao
import javax.inject.Inject

interface DataRepository {
    suspend fun getIdByLabelName(label: Label): Int
    suspend fun insertPhoto(photoDetail: PhotoDetail)
    suspend fun insertPhotoInAlbum(album: Album)
    suspend fun getAllPhotoDetail(): List<PhotoDetail>
    suspend fun getPhotoDetailUriByLabelId(labelId: Int): String
    suspend fun getALLAlbum(): List<Album>
    suspend fun getLabelNameById(id: Int): String
    suspend fun getPhotoDetailUriById(id: Int): String
    suspend fun getLabelBackgroundColorById(id: Int): String
    suspend fun insertLabel(label: Label)
}

class DataRepositoryImpl @Inject constructor(
    private val labelDao: LabelDao,
    private val albumDao: AlbumDao,
    private val photoDetailDao: PhotoDetailDao
) : DataRepository {
    override suspend fun getIdByLabelName(label: Label): Int {
        return labelDao.getIdByName(label.name) ?: run {
            labelDao.insertLabel(label)
            labelDao.getIdByName(label.name) ?: error("INSERT LABEL FAIL")
        }
    }

    override suspend fun insertLabel(label: Label) {
        labelDao.insertLabel(label)
    }

    override suspend fun insertPhoto(photoDetail: PhotoDetail) {
        photoDetailDao.insertPhotoDetail(photoDetail)
    }

    override suspend fun insertPhotoInAlbum(album: Album) {
        albumDao.insertAlbum(album)
    }

    override suspend fun getAllPhotoDetail(): List<PhotoDetail> {
        return photoDetailDao.getAllPhotoDetail()
    }

    override suspend fun getPhotoDetailUriByLabelId(labelId: Int): String {
        return photoDetailDao.getAllPhotoDetailUriByLabelId(labelId)
    }

    override suspend fun getALLAlbum(): List<Album> {
        return albumDao.getALLAlbum()
    }

    override suspend fun getLabelNameById(id: Int): String {
        return labelDao.getLabelNameById(id)
    }

    override suspend fun getLabelBackgroundColorById(id: Int): String {
        return labelDao.getLabelBackgroundColorById(id)
    }

    override suspend fun getPhotoDetailUriById(id: Int): String {
        return photoDetailDao.getPhotoDetailUriById(id)
    }
}
