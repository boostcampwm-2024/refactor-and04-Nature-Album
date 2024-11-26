package com.and04.naturealbum.di

import com.and04.naturealbum.data.repository.RetrofitRepository
import com.and04.naturealbum.data.repository.RetrofitRepositoryImpl
import com.and04.naturealbum.data.retorifit.NaverAPI
import com.and04.naturealbum.data.retorifit.ReverseGeocodeAPIFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ReverseGeocode

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class GreenEye

@Module
@InstallIn(SingletonComponent::class)
object ReverseGeocodeModule {

    @Singleton
    @Provides
    fun provideReverseGeocodeRepository(
        @ReverseGeocode reverseGeocodeAPI: NaverAPI,
        @GreenEye greenEyeAPI: NaverAPI,
    ): RetrofitRepository {
        return RetrofitRepositoryImpl(reverseGeocodeAPI, greenEyeAPI)
    }

    @Provides
    @Singleton
    @ReverseGeocode
    fun provideReverseGeocodeAPI(): NaverAPI =
        ReverseGeocodeAPIFactory.create()

    @Provides
    @Singleton
    @GreenEye
    fun provideGreenEyeAPI(): NaverAPI =
        ReverseGeocodeAPIFactory.create()
}
