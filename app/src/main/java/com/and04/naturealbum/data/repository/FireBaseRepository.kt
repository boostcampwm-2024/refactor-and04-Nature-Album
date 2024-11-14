package com.and04.naturealbum.data.repository

import android.net.Uri
import com.and04.naturealbum.data.dto.FirebaseLabel
import com.and04.naturealbum.data.dto.FirebasePhotoInfo
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import javax.inject.Inject

interface FireBaseRepository {
    //SELECT
    suspend fun getLabel(uid: String, label: String): Task<DocumentSnapshot>
    suspend fun getLabels(uid: String): Task<QuerySnapshot>

    //INSERT
    suspend fun saveImageFile(uid: String?, label: String, fileName: String, uri: Uri): UploadTask
    suspend fun insertLabel(
        uid: String,
        labelName: String,
        labelData: FirebaseLabel
    ): Task<Void>

    suspend fun insertPhotoInfo(
        uid: String,
        uri: String,
        photoData: FirebasePhotoInfo
    ): Task<Void>

    //UPDATE

}

class FireBaseRepositoryImpl @Inject constructor(
    private val fireStore: FirebaseFirestore,
    private val fireStorage: FirebaseStorage,
) : FireBaseRepository {
    override suspend fun getLabel(uid: String, label: String): Task<DocumentSnapshot> {

        return fireStore.collection(USER).document(uid).collection(LABEL).document(label).get()
    }

    override suspend fun getLabels(uid: String): Task<QuerySnapshot> {

        return fireStore.collection(USER).document(uid).collection(LABEL).get()
    }

    override suspend fun saveImageFile(
        uid: String?,
        label: String,
        fileName: String,
        uri: Uri,
    ): UploadTask {

        return fireStorage.getReference("$uid/$label/$fileName").putFile(uri)
    }

    override suspend fun insertLabel(
        uid: String,
        labelName: String,
        labelData: FirebaseLabel
    ): Task<Void> {

        return fireStore.collection(USER).document(uid).collection(LABEL).document(labelName).set(labelData)
    }

    override suspend fun insertPhotoInfo(
        uid: String,
        fileName: String,
        photoData: FirebasePhotoInfo
    ): Task<Void> {

        return fireStore.collection(USER).document(uid).collection(PHOTOS).document(fileName).set(photoData)
    }

    companion object {
        private const val USER = "USER"
        private const val LABEL = "LABEL"
        private const val PHOTOS = "PHOTOS"
    }
}