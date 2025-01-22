package com.and04.naturealbum.di

import android.content.Context
import com.and04.naturealbum.data.repository.local.LocalDataRepository
import com.and04.naturealbum.data.repository.local.LocalDataRepositoryImpl
import com.and04.naturealbum.data.localdata.room.AlbumDao
import com.and04.naturealbum.data.localdata.room.AppDatabase
import com.and04.naturealbum.data.localdata.room.LabelDao
import com.and04.naturealbum.data.localdata.room.PhotoDetailDao
import com.and04.naturealbum.data.repository.local.SyncRepository
import com.and04.naturealbum.data.repository.local.SyncRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomModule {

    @Singleton
    @Provides
    fun providerAppDatabase(
        @ApplicationContext context: Context
    ) = AppDatabase.getDatabase(context)

    @Singleton
    @Provides
    fun providerLabelDao(
        database: AppDatabase
    ) = database.labelDao()

    @Singleton
    @Provides
    fun providerAlbumDao(
        database: AppDatabase
    ) = database.albumDao()

    @Singleton
    @Provides
    fun providerPhotoDetailDao(
        database: AppDatabase
    ) = database.photoDetailDao()

    @Singleton
    @Provides
    fun providerRepository(
        labelDao: LabelDao,
        albumDao: AlbumDao,
        photoDetailDao: PhotoDetailDao
    ): LocalDataRepository = LocalDataRepositoryImpl(labelDao, albumDao, photoDetailDao)

    @Singleton
    @Provides
    fun providerSyncRepo(
        labelDao: LabelDao,
        albumDao: AlbumDao,
        photoDetailDao: PhotoDetailDao
    ): SyncRepository = SyncRepositoryImpl(labelDao, albumDao, photoDetailDao)
}
