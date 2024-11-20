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
    val status: FriendStatus = FriendStatus.NORMAL,     // "sent", "received"
) {
    // Firestore에서 사용 가능한 `LocalDateTime` 변환 메서드
    companion object {
        fun fromLocalDateTime(localDateTime: LocalDateTime): String {
            return localDateTime.toString() // ISO-8601 형식
        }

        fun toLocalDateTime(string: String): LocalDateTime {
            return LocalDateTime.parse(string)
        }
    }
}

data class FirestoreUser(
    val uid: String = "",
    val displayName: String = "",
    val email: String = "",
    val photoUrl: String = "",
)

data class FirestoreUserWithStatus(
    val user: FirestoreUser = FirestoreUser(),
    val status: FriendStatus = FriendStatus.NORMAL, // normal, sent, received, friend
)

enum class FriendStatus(val status: String) {
    NORMAL("normal"),
    SENT("sent"),
    RECEIVED("received"),
    FRIEND("friend");

    companion object {
        fun toString(status: FriendStatus): String {
            return status.status
        }
    }
}
