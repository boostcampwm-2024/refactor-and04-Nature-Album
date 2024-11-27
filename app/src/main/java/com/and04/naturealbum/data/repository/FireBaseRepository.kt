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
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
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
    fun getFriendsAsFlow(uid: String): Flow<List<FirebaseFriend>>
    fun getReceivedFriendRequestsAsFlow(uid: String): Flow<List<FirebaseFriendRequest>>
    fun searchUsersAsFlow(uid: String, query: String): Flow<List<FirestoreUserWithStatus>>

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
    suspend fun saveFcmToken(uid: String, token: String): Boolean
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
                fireStore.collection(USER).document(uid).set(firestoreUser)
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

    override fun getFriendsAsFlow(uid: String): Flow<List<FirebaseFriend>> = callbackFlow {
        val listener = fireStore.collection(USER).document(uid).collection(FRIENDS)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.e("getFriendsAsFlow", "Listen failed: ${e.message}")
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                Log.d("getFriendsAsFlow", "Snapshot size: ${snapshot?.size() ?: 0}")
                val friendList = snapshot?.documents?.mapNotNull { documentSnapshot ->
                    try {
                        documentSnapshot.toObject(FirebaseFriend::class.java)
                    } catch (e: Exception) {
                        Log.e("getFriendsAsFlow", "Mapping failed: ${e.message}")
                        null
                    }

                } ?: emptyList()
                Log.d("getFriendsAsFlow", "Mapped friends: $friendList")
                trySend(friendList) // 데이터가 변경되면 Flow로 보냄
            }
        awaitClose { listener.remove() } // Flow가 닫힐 때 리스너 제거
    }

    override fun getReceivedFriendRequestsAsFlow(uid: String): Flow<List<FirebaseFriendRequest>> =
        callbackFlow {
            val listener = fireStore.collection(USER)
                .document(uid)
                .collection(FRIEND_REQUESTS).addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        Log.e("getReceivedFriendRequests", "Listen failed: ${e.message}")
                        trySend(emptyList())
                        return@addSnapshotListener
                    }
                    Log.d("getReceivedFriendRequests", "Snapshot size: ${snapshot?.size() ?: 0}")
                    val receivedFriendRequestList =
                        snapshot?.documents?.mapNotNull { documentSnapshot ->
                            try {
                                val friendRequest =
                                    documentSnapshot.toObject(FirebaseFriendRequest::class.java)
                                if (friendRequest?.status == FriendStatus.RECEIVED) {
                                    friendRequest
                                } else {
                                    null
                                }
                            } catch (e: Exception) {
                                Log.e("getReceivedFriendRequests", "Mapping failed: ${e.message}")
                                null
                            }
                        } ?: emptyList()
                    Log.d(
                        "getReceivedFriendRequests",
                        "Mapped receivedFriendRequestList: $receivedFriendRequestList"
                    )
                    trySend(receivedFriendRequestList)
                }
            awaitClose { listener.remove() }
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

        // TODO: 요청자와 대상자의 사용자 정보 가져오기 -> NoSQL 구조 확정 후 구조 동일하면 Firebase.auth.currentUser를 사용하는 방향 고려
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

    // 검색했을 경우
    override fun searchUsersAsFlow(
        uid: String,
        query: String
    ): Flow<List<FirestoreUserWithStatus>> =
        callbackFlow {
            val listener = fireStore.collection(USER)
                .whereGreaterThanOrEqualTo(EMAIL, query)
                .whereLessThanOrEqualTo(EMAIL, query + QUERY_SUFFIX)
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        Log.e("searchUsersAsFlow", "Listen failed: ${e.message}")
                        trySend(emptyList())
                        return@addSnapshotListener
                    }

                    val userList = mutableListOf<FirestoreUserWithStatus>()

                    snapshot?.documents?.forEach { userDoc ->
                        launch {
                            if (userDoc.id == uid) return@launch

                            val user = userDoc.toObject(FirestoreUser::class.java) ?: return@launch
                            var friendStatus = FriendStatus.NORMAL

                            try {
                                val (friendRequestDoc, friendDoc) = listOf(
                                    fireStore.collection(USER)
                                        .document(userDoc.id)
                                        .collection(FRIEND_REQUESTS)
                                        .document(uid)
                                        .get(),
                                    fireStore.collection(USER)
                                        .document(uid)
                                        .collection(FRIENDS)
                                        .document(userDoc.id)
                                        .get()
                                ).map { taskResult -> taskResult.await() }

                                // 친구 상태 결정
                                friendStatus = when {
                                    friendDoc.exists() -> FriendStatus.FRIEND
                                    friendRequestDoc.exists() -> {
                                        val request =
                                            friendRequestDoc.toObject(FirebaseFriendRequest::class.java)
                                        // 상대방(equest?.status) 기준 => 현재 uid 에게 보냈는지, 받았는지 확인
                                        if (request?.status == FriendStatus.RECEIVED) {
                                            FriendStatus.SENT // 현재 uid 기준 [상대방 RECEIVED : 나  SENT]
                                        } else {
                                            FriendStatus.RECEIVED // 현재 uid 기준 [상대방 SENT : 나  RECEIVED]
                                        }
                                    }

                                    else -> FriendStatus.NORMAL
                                }
                            } catch (ex: Exception) {
                                Log.e("searchUsersAsFlow", "Error: ${ex.message}")
                            }

                            userList.add(
                                FirestoreUserWithStatus(
                                    user = user,
                                    status = friendStatus
                                )
                            )
                            trySend(userList).isSuccess
                        }
                    }
                }

            awaitClose { listener.remove() }
        }

    override suspend fun saveFcmToken(uid: String, token: String): Boolean {
        return try {
            fireStore.collection(USER)
                .document(uid)
                .update("fcmToken", token)
                .await()
            Log.d("FireBaseRepository", "FCM token updated successfully for user: $uid")
            true
        } catch (e: Exception) {
            Log.e("FireBaseRepository", "Failed to update FCM token: ${e.message}", e)
            false
        }
    }

    companion object {
        private const val USER = "USER"
        private const val LABEL = "LABEL"
        private const val PHOTOS = "PHOTOS"
        private const val FRIENDS = "FRIENDS"
        private const val FRIEND_REQUESTS = "FRIEND_REQUESTS"
        private const val EMAIL = "email"
        private const val QUERY_SUFFIX = "\uf8ff" // Firestore 쿼리에서 startsWith 구현을 위한 문자열 끝 범위 문자
    }
}
