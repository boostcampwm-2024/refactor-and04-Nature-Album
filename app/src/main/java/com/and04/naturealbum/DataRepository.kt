package com.and04.naturealbum

import kotlinx.coroutines.flow.Flow

interface DataRepository {
    suspend fun getIdByLabelName(label: Label): Int
    suspend fun insertPhoto(photoDetail: PhotoDetail)
    suspend fun insertPhotoInAlbum(album: Album)
    fun getAllPhotoDetail(): Flow<List<PhotoDetail>>
    suspend fun getPhotoDetailUriByLabelId(labelId: Int): List<PhotoDetail>
    fun getALLAlbum(): Flow<List<Album>>
    suspend fun getLabelNameById(id: Int): String
    suspend fun getPhotoDetailUriById(id: Int): PhotoDetail
}

class DataRepositoryImpl(
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

    override suspend fun insertPhoto(photoDetail: PhotoDetail) {
        photoDetailDao.insertPhotoDetail(photoDetail)
    }

    override suspend fun insertPhotoInAlbum(album: Album) {
        albumDao.insertAlbum(album)
    }

    override fun getAllPhotoDetail(): Flow<List<PhotoDetail>> {
        return photoDetailDao.getAllPhotoDetail()
    }

    override suspend fun getPhotoDetailUriByLabelId(labelId: Int): List<PhotoDetail> {
        return photoDetailDao.getAllPhotoDetailUriByLabelId(labelId)
    }

    override fun getALLAlbum(): Flow<List<Album>> {
        return albumDao.getALLAlbum()
    }

    override suspend fun getLabelNameById(id: Int): String {
        return albumDao.getLabelNameById(id)
    }

    override suspend fun getPhotoDetailUriById(id: Int): PhotoDetail {
        return albumDao.getPhotoDetailUriById(id)
    }
}