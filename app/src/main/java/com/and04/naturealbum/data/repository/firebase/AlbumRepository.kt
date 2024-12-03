package com.and04.naturealbum.data.repository.firebase

import android.net.Uri
import android.util.Log
import com.and04.naturealbum.data.datasource.FirebaseDataSource
import com.and04.naturealbum.data.dto.FirebaseLabel
import com.and04.naturealbum.data.dto.FirebaseLabelResponse
import com.and04.naturealbum.data.dto.FirebasePhotoInfo
import com.and04.naturealbum.data.dto.FirebasePhotoInfoResponse
import com.and04.naturealbum.data.repository.local.LocalDataRepository
import com.and04.naturealbum.data.room.Label
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
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

    suspend fun deleteImageFile(uid: String, label: Label, fileName: String): Boolean
}

class AlbumRepositoryImpl @Inject constructor(
    private val firebaseDataSource: FirebaseDataSource,
    private val localRepository: LocalDataRepository,
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

    override suspend fun deleteImageFile(uid: String, label: Label, fileName: String): Boolean {
        return try {
            FirebaseLock.deleteMutex.withLock {
                coroutineScope {
                    val isFileExist = withTimeoutOrNull(2_000) {
                        while (true) {
                            when (firebaseDataSource.checkFileExist(uid, label.name, fileName)) {
                                true -> return@withTimeoutOrNull true
                                false -> delay(500)
                            }
                        }
                    }
                    when (isFileExist) {
                        true -> {
                            val deleteFileJob =
                                async { firebaseDataSource.deleteImage(uid, label, fileName) }

                            val checkAlbumsJob = async {
                                val albums = localRepository.getAlbumByLabelId(label.id)
                                if (albums.isEmpty()) {
                                    firebaseDataSource.deleteUserLabel(uid, label)
                                }
                            }

                            val deletePhotoJob =
                                async { firebaseDataSource.deleteUserPhoto(uid, fileName) }

                            awaitAll(deleteFileJob, checkAlbumsJob, deletePhotoJob)
                            true
                        }

                        else -> {
                            false
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("FireBaseRepository", "deleteImageFile Error: ${e.message}")
            false
        }
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
        return FirebaseLock.insertMutex.withLock {
            return@withLock firebaseDataSource.setUserPhoto(uid, fileName, photoData).isSuccess
        }
    }

    object FirebaseLock {
        val insertMutex = Mutex()
        val deleteMutex = Mutex()
    }
}
