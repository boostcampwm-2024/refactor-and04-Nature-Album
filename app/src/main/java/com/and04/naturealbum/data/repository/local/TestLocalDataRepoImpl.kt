package com.and04.naturealbum.data.repository.local

import com.and04.naturealbum.data.dto.AlbumDto
import com.and04.naturealbum.data.dto.SyncAlbumsDto
import com.and04.naturealbum.data.dto.SyncPhotoDetailsDto
import com.and04.naturealbum.data.localdata.room.Album
import com.and04.naturealbum.data.localdata.room.HazardAnalyzeStatus
import com.and04.naturealbum.data.localdata.room.Label
import com.and04.naturealbum.data.localdata.room.PhotoDetail
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TestLocalDataRepoImpl: LocalDataRepository {
    override suspend fun getLabels(): List<Label> {
        return emptyList()
    }

    override suspend fun getLabelById(id: Int): Label {
        return Label.emptyLabel()
    }

    override suspend fun getIdByName(name: String): Int? {
        return null
    }

    override suspend fun getAllPhotoDetail(): List<PhotoDetail> {
        return emptyList()
    }

    override suspend fun getPhotoDetailById(id: Int): PhotoDetail {
        return PhotoDetail.emptyPhotoDetail()
    }

    override suspend fun getPhotoDetailsUriByLabelId(labelId: Int): List<PhotoDetail> {
        return emptyList()
    }

    override fun getAllAlbum(): Flow<List<AlbumDto>> {
        return flow { emit(emptyList()) }
    }

    override suspend fun getSyncCheckAlbums(): List<SyncAlbumsDto> {
        return emptyList()
    }

    override suspend fun getSyncCheckPhotos(): List<SyncPhotoDetailsDto> {
        return emptyList()
    }

    override suspend fun getAlbumByLabelId(labelId: Int): List<Album> {
        return emptyList()
    }

    override suspend fun insertPhoto(photoDetail: PhotoDetail): Long {
        return 0
    }

    override suspend fun insertPhotoInAlbum(album: Album): Long {
        return 0
    }

    override suspend fun insertLabel(label: Label): Long {
        return 0
    }

    override suspend fun updateAlbum(album: Album) {}

    override suspend fun deleteImage(photoDetail: PhotoDetail){}

    override suspend fun updateAlbumPhotoDetailByAlbumId(photoDetailId: Int){}

    override suspend fun getHazardCheckResultByFileName(fileName: String): HazardAnalyzeStatus {
        return HazardAnalyzeStatus.PASS
    }

    override suspend fun updateHazardCheckResultByFIleName(
        hazardAnalyzeStatus: HazardAnalyzeStatus,
        fileName: String
    ) {}

    override suspend fun getAddressByPhotoDetailId(photoDetailId: Int): String {
        return ""
    }

    override suspend fun updateAddressByPhotoDetailId(address: String, photoDetailId: Int) {}
}
