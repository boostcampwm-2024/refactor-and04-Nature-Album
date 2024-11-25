package com.and04.naturealbum.di

import com.and04.naturealbum.data.repository.ReverseGeocodeRepository
import com.and04.naturealbum.data.repository.ReverseGeocodeRepositoryImpl
import com.and04.naturealbum.data.retorifit.ReverseGeocodeAPI
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun bindReverseGeocodeRepository(
        reverseGeocodeAPI: ReverseGeocodeAPI,
    ): ReverseGeocodeRepository {
        return ReverseGeocodeRepositoryImpl(reverseGeocodeAPI)
    }
}
