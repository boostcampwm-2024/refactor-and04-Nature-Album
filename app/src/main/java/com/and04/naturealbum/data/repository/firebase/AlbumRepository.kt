package com.and04.naturealbum.data.repository.firebase

import android.net.Uri
import com.and04.naturealbum.data.datasource.FirebaseDataSource
import com.and04.naturealbum.data.dto.FirebaseLabel
import com.and04.naturealbum.data.dto.FirebaseLabelResponse
import com.and04.naturealbum.data.dto.FirebasePhotoInfo
import com.and04.naturealbum.data.dto.FirebasePhotoInfoResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface AlbumRepository {
    suspend fun getLabelsToList(uid: String): Result<List<FirebaseLabelResponse>>
    suspend fun getPhotosToList(uid: String): Result<List<FirebasePhotoInfoResponse>>
    suspend fun getLabelsToMap(uids: List<String>): Map<String, List<FirebaseLabelResponse>>
    suspend fun getPhotos(uids: List<String>): Map<String, List<FirebasePhotoInfoResponse>>

    suspend fun saveImageFile(uid: String, label: String, fileName: String, uri: Uri): Uri
    suspend fun insertLabel(
        uid: String,
        labelName: String,
        labelData: FirebaseLabel,
    ): Boolean

    suspend fun insertPhotoInfo(
        uid: String,
        fileName: String,
        photoData: FirebasePhotoInfo,
    ): Boolean
}

class AlbumRepositoryImpl @Inject constructor(
    private val firebaseDataSource: FirebaseDataSource
) : AlbumRepository {
    override suspend fun getLabelsToList(uid: String): Result<List<FirebaseLabelResponse>> {
        return firebaseDataSource
            .getUserLabels(uid)
            .mapCatching { querySnapshot ->
                querySnapshot
                    .documents
                    .mapNotNull { document ->
                        document.toObject(FirebaseLabelResponse::class.java)?.copy(
                            labelName = document.id
                        )
                    }
            }
    }

    override suspend fun getLabelsToMap(uids: List<String>): Map<String, List<FirebaseLabelResponse>> {
        return withContext(Dispatchers.IO) {
            try {
                val labels = uids.map { uid ->
                    async {
                        getLabelsToList(uid).getOrThrow()
                    }
                }.awaitAll()
                uids.zip(labels).toMap()
            } catch (e: Exception) {
                emptyMap()
            }
        }
    }

    override suspend fun getPhotosToList(uid: String): Result<List<FirebasePhotoInfoResponse>> {
        return firebaseDataSource
            .getUserPhotos(uid)
            .mapCatching { querySnapshot ->
                querySnapshot
                    .documents
                    .mapNotNull { document ->
                        document.toObject(FirebasePhotoInfoResponse::class.java)?.copy(
                            fileName = document.id
                        )
                    }
            }
    }

    override suspend fun getPhotos(uids: List<String>): Map<String, List<FirebasePhotoInfoResponse>> {
        return withContext(Dispatchers.IO) {
            try {
                val photos = uids.map { uid ->
                    async {
                        getPhotosToList(uid).getOrThrow()
                    }
                }.awaitAll()
                uids.zip(photos).toMap()
            } catch (e: Exception) {
                emptyMap()
            }
        }
    }

    override suspend fun saveImageFile(
        uid: String,
        label: String,
        fileName: String,
        uri: Uri,
    ): Uri {

        return firebaseDataSource.saveImage(uid, label, fileName, uri).getOrThrow()
    }

    override suspend fun insertLabel(
        uid: String,
        labelName: String,
        labelData: FirebaseLabel,
    ): Boolean {
        return firebaseDataSource.setUserLabel(uid, labelName, labelData).isSuccess
    }

    override suspend fun insertPhotoInfo(
        uid: String,
        fileName: String,
        photoData: FirebasePhotoInfo,
    ): Boolean {
        return firebaseDataSource.setUserPhoto(uid, fileName, photoData).isSuccess
    }
}
