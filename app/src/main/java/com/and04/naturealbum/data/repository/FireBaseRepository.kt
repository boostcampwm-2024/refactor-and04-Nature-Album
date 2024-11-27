package com.and04.naturealbum.data.repository

import android.net.Uri
import com.and04.naturealbum.data.dto.FirebaseLabel
import com.and04.naturealbum.data.dto.FirebaseLabelResponse
import com.and04.naturealbum.data.dto.FirebasePhotoInfo
import com.and04.naturealbum.data.dto.FirebasePhotoInfoResponse
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

interface FireBaseRepository {
    //SELECT
    suspend fun getLabel(uid: String, label: String): Task<DocumentSnapshot>
    suspend fun getLabels(uid: String): List<FirebaseLabelResponse>
    suspend fun getPhotos(uid: String): List<FirebasePhotoInfoResponse>

    //INSERT
    suspend fun saveImageFile(uid: String, label: String, fileName: String, uri: Uri): Uri
    suspend fun insertLabel(
        uid: String,
        labelName: String,
        labelData: FirebaseLabel
    ): Boolean

    suspend fun insertPhotoInfo(
        uid: String,
        fileName: String,
        photoData: FirebasePhotoInfo
    ): Boolean

    //UPDATE

}

class FireBaseRepositoryImpl @Inject constructor(
    private val fireStore: FirebaseFirestore,
    private val fireStorage: FirebaseStorage,
) : FireBaseRepository {
    override suspend fun getLabel(uid: String, label: String): Task<DocumentSnapshot> {

        return fireStore.collection(USER).document(uid).collection(LABEL).document(label).get()
    }

    override suspend fun getLabels(uid: String): List<FirebaseLabelResponse> {

        val querySnapshot = fireStore.collection(USER).document(uid).collection(LABEL).get().await()

        return querySnapshot.documents.mapNotNull { document ->
            document.toObject(FirebaseLabelResponse::class.java)?.copy(
                labelName = document.id
            )
        }
    }

    override suspend fun getPhotos(uid: String): List<FirebasePhotoInfoResponse> {
        val photosQuerySet =
            fireStore.collection(USER).document(uid).collection(PHOTOS).get().await()

        return photosQuerySet.documents.mapNotNull { document ->
            document.toObject(FirebasePhotoInfoResponse::class.java)?.copy(
                fileName = document.id
            )
        }
    }

    override suspend fun saveImageFile(
        uid: String,
        label: String,
        fileName: String,
        uri: Uri,
    ): Uri {
        val task = fireStorage.getReference("$uid/$label/$fileName").putFile(uri).await()
        return task.storage.downloadUrl.await()
    }

    override suspend fun insertLabel(
        uid: String,
        labelName: String,
        labelData: FirebaseLabel
    ): Boolean {
        var requestSuccess = false
        fireStore.collection(USER).document(uid).collection(LABEL).document(labelName)
            .set(labelData)
            .addOnSuccessListener {
                requestSuccess = true
            }.await()

        return requestSuccess
    }

    override suspend fun insertPhotoInfo(
        uid: String,
        fileName: String,
        photoData: FirebasePhotoInfo
    ): Boolean {
        var requestSuccess = false

        fireStore.collection(USER).document(uid).collection(PHOTOS).document(fileName)
            .set(photoData)
            .addOnSuccessListener {
                requestSuccess = true
            }.await()

        return requestSuccess
    }

    companion object {
        private const val USER = "USER"
        private const val LABEL = "LABEL"
        private const val PHOTOS = "PHOTOS"
    }
}
