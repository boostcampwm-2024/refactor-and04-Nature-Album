package com.and04.naturealbum.di

import com.and04.naturealbum.data.repository.FireBaseRepository
import com.and04.naturealbum.data.repository.FireBaseRepositoryImpl
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {
    @Provides
    @Singleton
    fun providerFireStore() = Firebase.firestore

    @Provides
    @Singleton
    fun providerFireStorage() = Firebase.storage

    @Provides
    @Singleton
    fun providerFireBaseRepository(
        fireStore: FirebaseFirestore,
        fireStorage: FirebaseStorage
    ): FireBaseRepository = FireBaseRepositoryImpl(fireStore, fireStorage)
}