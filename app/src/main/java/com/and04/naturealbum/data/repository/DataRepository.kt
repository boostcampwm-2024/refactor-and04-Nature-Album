package com.and04.naturealbum.data.repository

import com.and04.naturealbum.data.dto.AlbumDto
import com.and04.naturealbum.data.room.Album
import com.and04.naturealbum.data.room.AlbumDao
import com.and04.naturealbum.data.room.Label
import com.and04.naturealbum.data.room.LabelDao
import com.and04.naturealbum.data.room.PhotoDetail
import com.and04.naturealbum.data.room.PhotoDetailDao
import javax.inject.Inject

interface DataRepository {
    suspend fun getLabels(): List<Label>
    suspend fun getLabel(id: Int): Label
    suspend fun getIdByLabelName(label: Label): Int
    suspend fun insertPhoto(photoDetail: PhotoDetail): Long
    suspend fun insertPhotoInAlbum(album: Album): Long
    suspend fun getAllPhotoDetail(): List<PhotoDetail>
    suspend fun getPhotoDetailById(id: Int): PhotoDetail
    suspend fun getPhotoDetailsUriByLabelId(labelId: Int): List<PhotoDetail>
    suspend fun getALLAlbum(): List<Album>
    suspend fun getLabelNameById(id: Int): String
    suspend fun getPhotoDetailUriById(id: Int): String
    suspend fun getLabelBackgroundColorById(id: Int): String
    suspend fun insertLabel(label: Label): Long
    suspend fun getAllAlbum(): List<AlbumDto>
    suspend fun getAlbumByLabelId(labelId: Int): List<Album>
    suspend fun updateAlbum(album: Album)
}

class DataRepositoryImpl @Inject constructor(
    private val labelDao: LabelDao,
    private val albumDao: AlbumDao,
    private val photoDetailDao: PhotoDetailDao
) : DataRepository {
    override suspend fun getLabels(): List<Label> {
        return labelDao.getAllLabel()
    }

    override suspend fun getLabel(id: Int): Label {
        return labelDao.getLabelById(id)
    }

    override suspend fun getIdByLabelName(label: Label): Int {
        return labelDao.getIdByName(label.name) ?: run {
            labelDao.insertLabel(label)
            labelDao.getIdByName(label.name) ?: error("INSERT LABEL FAIL")
        }
    }

    override suspend fun insertLabel(label: Label): Long {
        return labelDao.insertLabel(label)
    }

    override suspend fun insertPhoto(photoDetail: PhotoDetail): Long {
        return photoDetailDao.insertPhotoDetail(photoDetail)
    }

    override suspend fun insertPhotoInAlbum(album: Album): Long {
        return albumDao.insertAlbum(album)
    }

    override suspend fun getAllPhotoDetail(): List<PhotoDetail> {
        return photoDetailDao.getAllPhotoDetail()
    }

    override suspend fun getPhotoDetailById(id: Int): PhotoDetail {
        return photoDetailDao.getPhotoDetailById(id)
    }

    override suspend fun getPhotoDetailsUriByLabelId(labelId: Int): List<PhotoDetail> {
        return photoDetailDao.getAllPhotoDetailsUriByLabelId(labelId)
    }

    override suspend fun getALLAlbum(): List<Album> {
        return albumDao.getALLAlbum()
    }

    override suspend fun getAlbumByLabelId(labelId: Int): List<Album> {
        return albumDao.getAlbumByLabelId(labelId)
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

    override suspend fun getAllAlbum(): List<AlbumDto> {
        return albumDao.getAllAlbum()
    }

    override suspend fun updateAlbum(album: Album) {
        return albumDao.updateAlbum(album)
    }
}
