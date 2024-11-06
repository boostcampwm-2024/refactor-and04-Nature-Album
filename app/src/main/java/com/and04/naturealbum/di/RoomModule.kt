package com.and04.naturealbum.di

import android.content.Context
import com.and04.naturealbum.data.room.AppDatabase
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
}
