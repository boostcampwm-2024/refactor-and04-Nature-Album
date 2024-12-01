package com.and04.naturealbum.data.repository.firebase

import android.net.Uri
import android.util.Log
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

interface FireBaseRepository {
    //SELECT
    suspend fun getLabels(uid: String): List<FirebaseLabelResponse>
    suspend fun getPhotos(uid: String): List<FirebasePhotoInfoResponse>
    suspend fun getLabels(uids: List<String>): Map<String, List<FirebaseLabelResponse>>
    suspend fun getPhotos(uids: List<String>): Map<String, List<FirebasePhotoInfoResponse>>

    //INSERT
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

class FireBaseRepositoryImpl @Inject constructor(
    private val firebaseDataSource: FirebaseDataSource
) : FireBaseRepository {
    override suspend fun getLabels(uid: String): List<FirebaseLabelResponse> {
        return firebaseDataSource
            .getUserLabels(uid)
            .documents
            .mapNotNull { document ->
                document.toObject(FirebaseLabelResponse::class.java)?.copy(
                    labelName = document.id
                )
            }
    }

    override suspend fun getLabels(uids: List<String>): Map<String, List<FirebaseLabelResponse>> {
        return try {
            withContext(Dispatchers.IO) {
                val labels = uids.map { uid ->
                    async {
                        getLabels(uid)
                    }
                }.awaitAll()
                uids.zip(labels).toMap()
            }
        } catch (e: Exception) {
            emptyMap()
        }
    }

    override suspend fun getPhotos(uid: String): List<FirebasePhotoInfoResponse> {
        val photosQuerySet = firebaseDataSource.getUserPhotos(uid)

        return photosQuerySet.documents.mapNotNull { document ->
            document.toObject(FirebasePhotoInfoResponse::class.java)?.copy(
                fileName = document.id
            )
        }
    }

    override suspend fun getPhotos(uids: List<String>): Map<String, List<FirebasePhotoInfoResponse>> {
        return try {
            withContext(Dispatchers.IO) {
                val photos = uids.map { uid ->
                    async {
                        getPhotos(uid)
                    }
                }.awaitAll()
                uids.zip(photos).toMap()
            }
        } catch (e: Exception) {
            Log.e("getPhotos", "Error fetching photos: ${e.message}")
            emptyMap()
        }
    }

    override suspend fun saveImageFile(
        uid: String,
        label: String,
        fileName: String,
        uri: Uri,
    ): Uri {

        return firebaseDataSource.saveImage(uid, label, fileName, uri)
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
