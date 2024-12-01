package com.and04.naturealbum.di

import com.and04.naturealbum.data.datasource.FirebaseDataSource
import com.and04.naturealbum.data.repository.firebase.FireBaseRepository
import com.and04.naturealbum.data.repository.firebase.FireBaseRepositoryImpl
import com.and04.naturealbum.data.repository.firebase.FriendRepository
import com.and04.naturealbum.data.repository.firebase.UserRepository
import com.google.firebase.firestore.FirebaseFirestore
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
    fun providerFirebaseDataSource(
        fireStore: FirebaseFirestore,
        fireStorage: FirebaseStorage
    ): FirebaseDataSource = FirebaseDataSource(fireStore, fireStorage)

    @Provides
    @Singleton
    fun providerFireBaseRepository(
        firebaseDataSource: FirebaseDataSource
    ): FireBaseRepository = FireBaseRepositoryImpl(firebaseDataSource)

    @Provides
    @Singleton
    fun providerFriendRepository(
        firebaseDataSource: FirebaseDataSource
    ): FriendRepository = FriendRepository(firebaseDataSource)

    @Provides
    @Singleton
    fun providerUserRepository(
        firebaseDataSource: FirebaseDataSource
    ): UserRepository = UserRepository(firebaseDataSource)
}
