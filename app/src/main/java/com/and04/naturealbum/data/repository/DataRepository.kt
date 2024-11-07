package com.and04.naturealbum.data.repository

import com.and04.naturealbum.data.room.Album
import com.and04.naturealbum.data.room.AlbumDao
import com.and04.naturealbum.data.room.Label
import com.and04.naturealbum.data.room.LabelDao
import com.and04.naturealbum.data.room.PhotoDetail
import com.and04.naturealbum.data.room.PhotoDetailDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface DataRepository {
    suspend fun getLabels(): List<Label>
    suspend fun getIdByLabelName(label: Label): Int
    suspend fun insertPhoto(photoDetail: PhotoDetail)
    suspend fun insertPhotoInAlbum(album: Album)
    fun getAllPhotoDetail(): Flow<List<PhotoDetail>>
    suspend fun getPhotoDetailUriByLabelId(labelId: Int): List<String>
    fun getALLAlbum(): Flow<List<Album>>
    suspend fun getLabelNameById(id: Int): String
    suspend fun getPhotoDetailUriById(id: Int): String
}

class DataRepositoryImpl @Inject constructor(
    private val labelDao: LabelDao,
    private val albumDao: AlbumDao,
    private val photoDetailDao: PhotoDetailDao
) : DataRepository {
    override suspend fun getLabels(): List<Label> {
        return labelDao.getAllLabel()
    }

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

    override suspend fun getPhotoDetailUriByLabelId(labelId: Int): List<String> {
        return photoDetailDao.getAllPhotoDetailUriByLabelId(labelId)
    }

    override fun getALLAlbum(): Flow<List<Album>> {
        return albumDao.getALLAlbum()
    }

    override suspend fun getLabelNameById(id: Int): String {
        return albumDao.getLabelNameById(id)
    }

    override suspend fun getPhotoDetailUriById(id: Int): String {
        return albumDao.getPhotoDetailUriById(id)
    }
}
