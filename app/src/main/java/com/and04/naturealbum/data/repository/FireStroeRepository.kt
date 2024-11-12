package com.and04.naturealbum.data.repository

import com.and04.naturealbum.data.room.Label
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

interface FireStoreRepository {
    //SELECT
    suspend fun getLabel(uid: String): Label

    //INSERT


    //UPDATE
}

class FireStoreRepositoryImpl @Inject constructor(
    private val fireStore: FirebaseFirestore
) : FireStoreRepository {

    override suspend fun getLabel(uid: String): Label {

    }

    companion object{
        private const val USER = "USER"
        private const val LABEL = "LABEL"
        private const val PHOTOS = "PHOTOS"
    }
}