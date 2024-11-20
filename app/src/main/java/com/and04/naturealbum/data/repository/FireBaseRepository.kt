package com.and04.naturealbum.data.repository

import android.net.Uri
import android.util.Log
import com.and04.naturealbum.data.dto.FirebaseFriend
import com.and04.naturealbum.data.dto.FirebaseFriendRequest
import com.and04.naturealbum.data.dto.FirebaseLabel
import com.and04.naturealbum.data.dto.FirebasePhotoInfo
import com.and04.naturealbum.data.dto.FirestoreUser
import com.and04.naturealbum.data.dto.FirestoreUserWithStatus
import com.and04.naturealbum.data.dto.FriendStatus
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.time.LocalDateTime
import javax.inject.Inject

interface FireBaseRepository {

    // CREATE
    suspend fun createUserIfNotExists(
        uid: String,
        displayName: String?,
        email: String,
        photoUrl: String?
    ): Boolean

    //SELECT
    suspend fun getLabel(uid: String, label: String): Task<DocumentSnapshot>
    suspend fun getLabels(uid: String): Task<QuerySnapshot>
    suspend fun getFriendRequests(uid: String): List<FirebaseFriendRequest>
    suspend fun getFriends(uid: String): List<FirebaseFriend>
    suspend fun getAllUsers(): List<FirestoreUser>
    suspend fun getAllUsersInfo(uid: String): List<FirestoreUserWithStatus>
    suspend fun getReceivedFriendRequests(uid: String): List<FirebaseFriendRequest>

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
        photoData: FirebasePhotoInfo
    ): Boolean

    suspend fun sendFriendRequest(uid: String, targetUid: String): Boolean
    suspend fun acceptFriendRequest(uid: String, targetUid: String): Boolean
    suspend fun rejectFriendRequest(uid: String, targetUid: String): Boolean

    //UPDATE


}

class FireBaseRepositoryImpl @Inject constructor(
    private val fireStore: FirebaseFirestore,
    private val fireStorage: FirebaseStorage,
) : FireBaseRepository {

    override suspend fun createUserIfNotExists(
        uid: String,
        displayName: String?,
        email: String,
        photoUrl: String?
    ): Boolean {
        return try {
            val userDoc = fireStore.collection(USER).document(uid).get().await()
            if (!userDoc.exists()) {
                val firestoreUser = FirestoreUser(
                    uid = uid,
                    displayName = displayName ?: "NoName User",
                    email = email,
                    photoUrl = photoUrl ?: ""
                )
                fireStore.collection(USER).document(uid).set(firestoreUser).await()
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun getLabel(uid: String, label: String): Task<DocumentSnapshot> {

        return fireStore.collection(USER).document(uid).collection(LABEL).document(label).get()
    }

    override suspend fun getLabels(uid: String): Task<QuerySnapshot> {

        return fireStore.collection(USER).document(uid).collection(LABEL).get()
    }

    override suspend fun getFriends(uid: String): List<FirebaseFriend> {
        return try {
            fireStore.collection(USER)
                .document(uid)
                .collection(FRIENDS)
                .get()
                .await()
                .documents
                .mapNotNull { document ->
                    try {
                        document.toObject(FirebaseFriend::class.java) // 전체 객체 직렬화
                    } catch (e: Exception) {
                        Log.e(
                            "getFriends",
                            "Failed to deserialize friend: ${document.id}, ${e.message}"
                        )
                        null
                    }
                }
        } catch (e: Exception) {
            Log.e("getFriends", "Error fetching friends: ${e.message}")
            emptyList()
        }
    }

    override suspend fun getFriendRequests(uid: String): List<FirebaseFriendRequest> {
        val documents = fireStore.collection(USER)
            .document(uid)
            .collection(FRIEND_REQUESTS)
            .get()
            .await()
            .documents

        return documents.mapNotNull { document ->
            try {
                val friendRequest = document.toObject(FirebaseFriendRequest::class.java)
                if (friendRequest == null) {
                    Log.e("getFriendRequests", "Failed to map document: ${document.id}")
                }
                friendRequest
            } catch (e: Exception) {
                Log.e("getFriendRequests", "Error mapping document: ${document.id}, ${e.message}")
                null
            }
        }
    }

    override suspend fun getReceivedFriendRequests(uid: String): List<FirebaseFriendRequest> {
        Log.d("getReceivedFriendRequests", "$uid")
        val documents = fireStore.collection(USER)
            .document(uid)
            .collection(FRIEND_REQUESTS)
            .get()
            .await()
            .documents

        return documents.mapNotNull { document ->
            try {
                val friendRequest = document.toObject(FirebaseFriendRequest::class.java)
                if (friendRequest == null) {
                    Log.e("getReceivedFriendRequests", "Failed to map document: ${document.id}")
                }
                friendRequest
            } catch (e: Exception) {
                Log.e(
                    "getReceivedFriendRequests",
                    "Error mapping document: ${document.id}, ${e.message}"
                )
                null
            }
        }.filter { friendRequest ->
            Log.d("getReceivedFriendRequests", friendRequest.toString())
            Log.d("getReceivedFriendRequests","${friendRequest.status} ${FriendStatus.RECEIVED} ${friendRequest}")
            friendRequest.status == FriendStatus.RECEIVED
        }
    }

    override suspend fun getAllUsers(): List<FirestoreUser> {
        return fireStore.collection(USER).get().await().toObjects(FirestoreUser::class.java)
    }

    override suspend fun getAllUsersInfo(uid: String): List<FirestoreUserWithStatus> {
        val users = mutableListOf<FirestoreUserWithStatus>()

        try {
            val userDocs = fireStore.collection(USER).get().await()
            for (userDoc in userDocs.documents) {
                val user = userDoc.toObject(FirestoreUser::class.java) ?: continue
                var friendStatus = FriendStatus.NORMAL

                if (userDoc.id.isNotEmpty()) {
                    val friendRequestDoc = fireStore.collection(USER)
                        .document(userDoc.id)
                        .collection(FRIEND_REQUESTS)
                        .document(uid)
                        .get()
                        .await()

                    if (friendRequestDoc.exists()) {
                        val request = friendRequestDoc.toObject(FirebaseFriendRequest::class.java)
                        friendStatus = if (request?.user?.uid == uid) {
                            FriendStatus.RECEIVED
                        } else {
                            FriendStatus.SENT
                        }
                    }

                    val friendDoc = fireStore.collection(USER)
                        .document(uid)
                        .collection(FRIENDS)
                        .document(userDoc.id)
                        .get()
                        .await()

                    if (friendDoc.exists()) {
                        friendStatus = FriendStatus.FRIEND
                    }
                }
                users.add(FirestoreUserWithStatus(user = user, status = friendStatus))
            }
        } catch (e: Exception) {
            Log.e("FireBaseRepository", "getAllUsersInfo Error: ${e.message}")
        }

        return users
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

    // 친구 요청 보냈을 경우
    override suspend fun sendFriendRequest(uid: String, targetUid: String): Boolean {
        val requestTime = LocalDateTime.now().toString()

        // 요청자와 대상자의 사용자 정보 가져오기
        val currentUserSnapshot = fireStore.collection(USER).document(uid).get().await()
        val targetUserSnapshot = fireStore.collection(USER).document(targetUid).get().await()

        if (!currentUserSnapshot.exists() || !targetUserSnapshot.exists()) {
            Log.e("sendFriendRequest", "User data not found for uid: $uid or targetUid: $targetUid")
            return false
        }

        val currentUser = currentUserSnapshot.toObject(FirestoreUser::class.java)?.copy(uid = uid)
            ?: return false
        val targetUser =
            targetUserSnapshot.toObject(FirestoreUser::class.java)?.copy(uid = targetUid)
                ?: return false

        // 친구 요청 데이터 생성
        val friendRequest = FirebaseFriendRequest(
            user = targetUser,
            requestedAt = requestTime,
            status = FriendStatus.SENT
        )
        val targetFriendRequest = FirebaseFriendRequest(
            user = currentUser,
            requestedAt = requestTime,
            status = FriendStatus.RECEIVED
        )

        // Firestore 트랜잭션으로 요청 저장
        return try {
            fireStore.runTransaction { transaction ->
                // 요청자 -> 대상자 요청 생성
                transaction.set(
                    fireStore.collection(USER).document(uid).collection(FRIEND_REQUESTS)
                        .document(targetUid),
                    friendRequest
                )

                // 대상자 -> 요청자 요청 생성
                transaction.set(
                    fireStore.collection(USER).document(targetUid).collection(FRIEND_REQUESTS)
                        .document(uid),
                    targetFriendRequest
                )
            }.await()
            Log.d("sendFriendRequest", "Friend request successfully sent from $uid to $targetUid")
            true
        } catch (e: Exception) {
            Log.e("sendFriendRequest", "Error sending friend request: ${e.message}", e)
            false
        }
    }


    // 수락했을 경우
    override suspend fun acceptFriendRequest(uid: String, targetUid: String): Boolean {
        val addedTime = LocalDateTime.now().toString() // 문자열로 변환

        // 요청자와 대상자의 사용자 정보 가져오기
        val currentUser = fireStore.collection(USER).document(uid).get().await()
            .toObject(FirestoreUser::class.java) ?: return false
        val targetUser = fireStore.collection(USER).document(targetUid).get().await()
            .toObject(FirestoreUser::class.java) ?: return false

        // FirebaseFriend 데이터 생성
        val uidFriendData = FirebaseFriend(user = targetUser, addedAt = addedTime)
        val targetUidFriendData = FirebaseFriend(user = currentUser, addedAt = addedTime)

        return try {
            fireStore.runTransaction { transaction ->
                // 서로의 친구 리스트에 추가
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
                // 서로의 요청 상태 제거
                transaction.delete(
                    fireStore.collection(USER).document(uid).collection(FRIEND_REQUESTS)
                        .document(targetUid)
                )
                transaction.delete(
                    fireStore.collection(USER).document(targetUid).collection(FRIEND_REQUESTS)
                        .document(uid)
                )
            }.await()
            true
        } catch (e: Exception) {
            false
        }
    }


    // 거절했을 경우
    override suspend fun rejectFriendRequest(uid: String, targetUid: String): Boolean {
        return try {
            fireStore.runTransaction { transaction ->
                // 서로의 요청 상태 제거
                transaction.delete(
                    fireStore.collection(USER).document(uid).collection(FRIEND_REQUESTS)
                        .document(targetUid)
                )
                transaction.delete(
                    fireStore.collection(USER).document(targetUid).collection(FRIEND_REQUESTS)
                        .document(uid)
                )
            }.await()
            true
        } catch (e: Exception) {
            false
        }
    }

    companion object {
        private const val USER = "USER"
        private const val LABEL = "LABEL"
        private const val PHOTOS = "PHOTOS"
        private const val FRIENDS = "FRIENDS"
        private const val FRIEND_REQUESTS = "FRIEND_REQUESTS"
    }
}
