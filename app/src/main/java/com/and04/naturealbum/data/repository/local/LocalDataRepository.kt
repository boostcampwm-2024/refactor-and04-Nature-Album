package com.and04.naturealbum.data.repository.local

import com.and04.naturealbum.data.dto.AlbumDto
import com.and04.naturealbum.data.dto.SyncAlbumsDto
import com.and04.naturealbum.data.dto.SyncPhotoDetailsDto
import com.and04.naturealbum.data.room.Album
import com.and04.naturealbum.data.room.AlbumDao
import com.and04.naturealbum.data.room.HazardAnalyzeStatus
import com.and04.naturealbum.data.room.Label
import com.and04.naturealbum.data.room.LabelDao
import com.and04.naturealbum.data.room.PhotoDetail
import com.and04.naturealbum.data.room.PhotoDetailDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface LocalDataRepository {
    suspend fun getLabels(): List<Label>
    suspend fun getLabelById(id: Int): Label
    suspend fun getIdByName(name: String): Int?
    suspend fun getAllPhotoDetail(): List<PhotoDetail>
    suspend fun getPhotoDetailById(id: Int): PhotoDetail
    suspend fun getPhotoDetailsUriByLabelId(labelId: Int): List<PhotoDetail>
    fun getAllAlbum(): Flow<List<AlbumDto>>
    suspend fun getSyncCheckAlbums(): List<SyncAlbumsDto>
    suspend fun getSyncCheckPhotos(): List<SyncPhotoDetailsDto>
    suspend fun getAlbumByLabelId(labelId: Int): List<Album>
    suspend fun insertPhoto(photoDetail: PhotoDetail): Long
    suspend fun insertPhotoInAlbum(album: Album): Long
    suspend fun insertLabel(label: Label): Long
    suspend fun updateAlbum(album: Album)
    suspend fun deleteImage(photoDetail: PhotoDetail)
    suspend fun updateAlbumPhotoDetailByAlbumId(photoDetailId: Int)
    suspend fun getHazardCheckResultByFileName(fileName: String): HazardAnalyzeStatus
    suspend fun updateHazardCheckResultByFIleName(
        hazardAnalyzeStatus: HazardAnalyzeStatus,
        fileName: String,
    )

    suspend fun getAddressByPhotoDetailId(photoDetailId: Int): String
    suspend fun updateAddressByPhotoDetailId(address: String, photoDetailId: Int)
}

class LocalDataRepositoryImpl @Inject constructor(
    private val labelDao: LabelDao,
    private val albumDao: AlbumDao,
    private val photoDetailDao: PhotoDetailDao,
) : LocalDataRepository {
    override suspend fun getLabels(): List<Label> {
        return labelDao.getAllLabel()
    }

    override suspend fun getLabelById(id: Int): Label {
        return labelDao.getLabelById(id)
    }

    override suspend fun getIdByName(name: String): Int? {
        return labelDao.getIdByName(name)
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

    override fun getAllAlbum(): Flow<List<AlbumDto>> {
        return albumDao.getAllAlbum()
    }

    override suspend fun getSyncCheckAlbums(): List<SyncAlbumsDto> {
        return albumDao.getSyncCheckAlbums()
    }

    override suspend fun getSyncCheckPhotos(): List<SyncPhotoDetailsDto> {
        return albumDao.getSyncCheckPhotos()
    }

    override suspend fun updateAlbum(album: Album) {
        return albumDao.updateAlbum(album)
    }

    override suspend fun deleteImage(photoDetail: PhotoDetail) {
        val album = albumDao.getAlbumByLabelId(photoDetail.labelId).first()
        val isRepresentedImage = album.photoDetailId == photoDetail.id
        val nextRepresentedImage =
            photoDetailDao.getAllPhotoDetailsUriByLabelId(photoDetail.labelId)
                .firstOrNull { it != photoDetail }

        if (isRepresentedImage && nextRepresentedImage != null) {
            albumDao.updateAlbum(
                Album(
                    id = album.id,
                    labelId = photoDetail.labelId,
                    photoDetailId = nextRepresentedImage.id
                )
            )
        }
        return photoDetailDao.deleteImage(photoDetail)
    }

    override suspend fun updateAlbumPhotoDetailByAlbumId(photoDetailId: Int) {
        return albumDao.updateAlbumPhotoDetailByAlbumId(photoDetailId)
    }

    override suspend fun getHazardCheckResultByFileName(fileName: String): HazardAnalyzeStatus {
        return photoDetailDao.getHazardCheckResultByFileName(fileName)
    }

    override suspend fun updateHazardCheckResultByFIleName(
        hazardAnalyzeStatus: HazardAnalyzeStatus,
        fileName: String,
    ) {
        return photoDetailDao.updateHazardCheckResultByFIleName(hazardAnalyzeStatus, fileName)
    }

    override suspend fun getAddressByPhotoDetailId(photoDetailId: Int): String {
        return photoDetailDao.getAddress(id = photoDetailId)
    }

    override suspend fun updateAddressByPhotoDetailId(address: String, photoDetailId: Int) {
        photoDetailDao.updateAddressById(address = address, id = photoDetailId)
    }
}
