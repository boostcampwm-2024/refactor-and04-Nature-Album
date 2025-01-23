package com.and04.naturealbum.data.repository.firebase

import android.net.Uri
import androidx.core.net.toUri
import com.and04.naturealbum.data.dto.FirebaseLabel
import com.and04.naturealbum.data.dto.FirebaseLabelResponse
import com.and04.naturealbum.data.dto.FirebasePhotoInfo
import com.and04.naturealbum.data.dto.FirebasePhotoInfoResponse
import com.and04.naturealbum.data.localdata.room.Label

class TestAlbumRepoImpl: AlbumRepository {
    override suspend fun getLabelsToList(uid: String): Result<List<FirebaseLabelResponse>> {
        return Result.success(emptyList())
    }

    override suspend fun getPhotosToList(uid: String): Result<List<FirebasePhotoInfoResponse>> {
        return Result.success(emptyList())
    }

    override suspend fun getLabelsToMap(uids: List<String>): Map<String, List<FirebaseLabelResponse>> {
        return emptyMap()
    }

    override suspend fun getPhotos(uids: List<String>): Map<String, List<FirebasePhotoInfoResponse>> {
        return emptyMap()
    }

    override suspend fun saveImageFile(
        uid: String,
        label: String,
        fileName: String,
        uri: Uri
    ): Uri {
        return "".toUri()
    }

    override suspend fun insertLabel(
        uid: String,
        labelName: String,
        labelData: FirebaseLabel
    ): Boolean {
        return true
    }

    override suspend fun insertPhotoInfo(
        uid: String,
        fileName: String,
        photoData: FirebasePhotoInfo
    ): Boolean {
        return true
    }

    override suspend fun deleteImageFile(uid: String, label: Label, fileName: String): Boolean {
        return true
    }
}
