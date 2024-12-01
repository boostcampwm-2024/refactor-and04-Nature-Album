package com.and04.naturealbum.data.datasource

import android.net.Uri
import com.and04.naturealbum.data.dto.FirebaseFriend
import com.and04.naturealbum.data.dto.FirebaseFriendRequest
import com.and04.naturealbum.data.dto.FirebaseLabel
import com.and04.naturealbum.data.dto.FirebasePhotoInfo
import com.and04.naturealbum.data.dto.FirestoreUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.Transaction
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseDataSource @Inject constructor(
    private val fireStore: FirebaseFirestore,
    private val fireStorage: FirebaseStorage,
) {
    suspend fun saveImage(
        uid: String,
        label: String,
        fileName: String,
        uri: Uri,
    ): Uri {
        val task = fireStorage.getReference("$uid/$label/$fileName").putFile(uri).await()
        return task.storage.downloadUrl.await()
    }

    suspend fun getUser(uid: String): DocumentSnapshot {
        return fireStore.collection(USER).document(uid).get().await()
    }

    suspend fun setUser(uid: String, firestoreUser: FirestoreUser) {
        fireStore.collection(USER).document(uid).set(firestoreUser)
    }

    suspend fun updateUser(uid: String, token: String): Result<Void> {
        return runCatching {
            fireStore.collection(USER).document(uid)
                .update(FCM_TOKEN, token)
                .await()
        }
    }

    suspend fun getUserLabels(uid: String): QuerySnapshot {
        return fireStore.collection(USER).document(uid).collection(LABEL).get().await()
    }

    suspend fun setUserLabel(
        uid: String,
        labelName: String,
        labelData: FirebaseLabel,
    ): Result<Void> {
        return runCatching {
            fireStore.collection(USER).document(uid).collection(LABEL).document(labelName)
                .set(labelData).await()
        }
    }

    suspend fun getUserPhotos(uid: String): QuerySnapshot {
        return fireStore.collection(USER).document(uid)
            .collection(PHOTOS)
            .get()
            .await()
    }

    suspend fun setUserPhoto(
        uid: String,
        fileName: String,
        photoData: FirebasePhotoInfo,
    ): Result<Void> {
        return runCatching {
            fireStore.collection(USER).document(uid)
                .collection(PHOTOS).document(fileName)
                .set(photoData)
                .await()
        }
    }

    suspend fun getUserFriends(uid: String): CollectionReference {
        return fireStore.collection(USER).document(uid).collection(FRIENDS)
    }

    suspend fun getReceivedFriendRequests(uid: String): CollectionReference {
        return fireStore.collection(USER).document(uid).collection(FRIEND_REQUESTS)
    }

    fun getFriendRequestDoc(uid: String, targetUid: String): DocumentReference {
        return fireStore.collection(USER).document(uid).collection(FRIEND_REQUESTS)
            .document(targetUid)
    }

    suspend fun setTransactionFriendRequest(
        uid: String,
        targetUid: String,
        friendRequest: FirebaseFriendRequest,
        targetFriendRequest: FirebaseFriendRequest
    ): Result<Transaction> {
        return runCatching {
            fireStore.runTransaction { transaction ->
                transaction.set(
                    getFriendRequestDoc(uid, targetUid),
                    friendRequest
                )

                transaction.set(
                    getFriendRequestDoc(targetUid, uid),
                    targetFriendRequest
                )
            }.await()
        }
    }

    suspend fun deleteTransactionFriendRequest(
        uid: String,
        targetUid: String
    ): Result<Transaction> {
        return runCatching {
            fireStore.runTransaction { transaction ->
                transaction.delete(
                    fireStore.collection(USER).document(uid).collection(FRIEND_REQUESTS)
                        .document(targetUid)
                )

                transaction.delete(
                    fireStore.collection(USER).document(targetUid).collection(FRIEND_REQUESTS)
                        .document(uid)
                )
            }.await()
        }
    }

    suspend fun acceptTransactionFriendRequest(
        uid: String,
        targetUid: String,
        uidFriendData: FirebaseFriend,
        targetUidFriendData: FirebaseFriend
    ): Result<Transaction> {
        return runCatching {
            fireStore.runTransaction { transaction ->
                transaction.set(
                    fireStore.collection(USER).document(uid).collection(FRIENDS)
                        .document(targetUid),
                    uidFriendData
                )

                transaction.set(
                    fireStore.collection(USER).document(targetUid).collection(FRIENDS)
                        .document(uid),
                    targetUidFriendData
                )

                transaction.delete(
                    fireStore.collection(USER).document(uid).collection(FRIEND_REQUESTS)
                        .document(targetUid)
                )

                transaction.delete(
                    fireStore.collection(USER).document(targetUid).collection(FRIEND_REQUESTS)
                        .document(uid)
                )
            }.await()
        }
    }

    suspend fun searchUsers(query: String): Query {
        return fireStore.collection(USER)
            .whereGreaterThanOrEqualTo(EMAIL, query)
            .whereLessThanOrEqualTo(EMAIL, query + QUERY_SUFFIX)
    }

    companion object {
        private const val USER = "USER"
        private const val LABEL = "LABEL"
        private const val PHOTOS = "PHOTOS"
        private const val FRIENDS = "FRIENDS"
        private const val FRIEND_REQUESTS = "FRIEND_REQUESTS"
        private const val FCM_TOKEN = "fcmToken"
        private const val EMAIL = "email"
        private const val QUERY_SUFFIX = "\uf8ff"
    }
}