package com.and04.naturealbum.data.repository

import com.and04.naturealbum.data.dto.AlbumDto
import com.and04.naturealbum.data.dto.SynchronizedAlbumsDto
import com.and04.naturealbum.data.dto.SynchronizedPhotoDetailsDto
import com.and04.naturealbum.data.room.Album
import com.and04.naturealbum.data.room.AlbumDao
import com.and04.naturealbum.data.room.Label
import com.and04.naturealbum.data.room.LabelDao
import com.and04.naturealbum.data.room.PhotoDetail
import com.and04.naturealbum.data.room.PhotoDetailDao
import javax.inject.Inject

interface DataRepository {
    suspend fun getLabels(): List<Label>
    suspend fun getLabelById(id: Int): Label
    suspend fun getAllPhotoDetail(): List<PhotoDetail>
    suspend fun getPhotoDetailById(id: Int): PhotoDetail
    suspend fun getPhotoDetailsUriByLabelId(labelId: Int): List<PhotoDetail>
    suspend fun getAllAlbum(): List<AlbumDto>
    suspend fun getSynchronizedAlbums(labels: List<String>): List<SynchronizedAlbumsDto>
    suspend fun getSynchronizedPhotoDetails(fileNames: List<String>): List<SynchronizedPhotoDetailsDto>
    suspend fun getAlbumByLabelId(labelId: Int): List<Album>
    suspend fun insertPhoto(photoDetail: PhotoDetail): Long
    suspend fun insertPhotoInAlbum(album: Album): Long
    suspend fun insertLabel(label: Label): Long
    suspend fun updateAlbum(album: Album)
}

class DataRepositoryImpl @Inject constructor(
    private val labelDao: LabelDao,
    private val albumDao: AlbumDao,
    private val photoDetailDao: PhotoDetailDao,
) : DataRepository {
    override suspend fun getLabels(): List<Label> {
        return labelDao.getAllLabel()
    }

    override suspend fun getLabelById(id: Int): Label {
        return labelDao.getLabelById(id)
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

    override suspend fun getAlbumByLabelId(labelId: Int): List<Album> {
        return albumDao.getAlbumByLabelId(labelId)
    }

    override suspend fun getAllAlbum(): List<AlbumDto> {
        return albumDao.getAllAlbum()
    }

    override suspend fun getSynchronizedAlbums(labels: List<String>): List<SynchronizedAlbumsDto> {
        return albumDao.getSynchronizedAlbums(labels)
    }

    override suspend fun getSynchronizedPhotoDetails(fileNames: List<String>): List<SynchronizedPhotoDetailsDto> {
        return albumDao.getSynchronizedPhotos(fileNames)
    }

    override suspend fun updateAlbum(album: Album) {
        return albumDao.updateAlbum(album)
    }
}
