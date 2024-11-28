package com.and04.naturealbum.di

import com.and04.naturealbum.data.repository.RetrofitRepository
import com.and04.naturealbum.data.repository.RetrofitRepositoryImpl
import com.and04.naturealbum.data.retorifit.NaverApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RetrofitRepositoryModule {

    @Provides
    @Singleton
    fun provideReverseGeocodeRepository(
        @ReverseGeocode reverseGeocodeAPI: NaverApi,
        @GreenEye greenEyeAPI: NaverApi,
    ): RetrofitRepository {
        return RetrofitRepositoryImpl(reverseGeocodeAPI, greenEyeAPI)
    }
}
