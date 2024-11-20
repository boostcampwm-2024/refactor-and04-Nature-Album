package com.and04.naturealbum.data.dto

import java.time.LocalDateTime

data class FirebaseLabel(
    val backgroundColor: String,
    val thumbnailUri: String
)

data class FirebasePhotoInfo(
    val uri: String,
    val label: String,
    val latitude: Double?,
    val longitude: Double?,
    val description: String,
    val datetime: LocalDateTime
)

data class FirebaseFriend(
    val user: FirestoreUser = FirestoreUser(),
    val addedAt: String = ""
)

data class FirebaseFriendRequest(
    val user: FirestoreUser = FirestoreUser(),
    val requestedAt: String = "",
    val status: FriendStatus = FriendStatus.NORMAL,
)

data class FirestoreUser(
    val uid: String = "",
    val displayName: String = "",
    val email: String = "",
    val photoUrl: String = "",
)

data class FirestoreUserWithStatus(
    val user: FirestoreUser = FirestoreUser(),
    val status: FriendStatus = FriendStatus.NORMAL,
)

enum class FriendStatus() {
    NORMAL,
    SENT,
    RECEIVED,
    FRIEND;
}
