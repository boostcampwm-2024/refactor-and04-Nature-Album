package com.and04.naturealbum.di

import android.content.Context
import com.and04.naturealbum.data.repository.DataRepository
import com.and04.naturealbum.data.repository.DataRepositoryImpl
import com.and04.naturealbum.data.repository.FireBaseRepository
import com.and04.naturealbum.data.room.AlbumDao
import com.and04.naturealbum.data.room.AppDatabase
import com.and04.naturealbum.data.room.LabelDao
import com.and04.naturealbum.data.room.PhotoDetailDao
import com.and04.naturealbum.ui.mypage.AuthenticationManager
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
    ): DataRepository = DataRepositoryImpl(labelDao, albumDao, photoDetailDao)
}
