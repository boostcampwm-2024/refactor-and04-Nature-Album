package com.and04.naturealbum.di

import com.and04.naturealbum.BuildConfig.NAVER_MAP_CLIENT_ID
import com.and04.naturealbum.BuildConfig.NAVER_MAP_CLIENT_SECRET
import com.and04.naturealbum.data.retorifit.NaverApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ReverseGeocodeModule {

    @Provides
    @ReverseGeocode
    fun provideReverseGeocodeBaseUrl(): String = "https://naveropenapi.apigw.ntruss.com/"

    @Singleton
    @Provides
    @ReverseGeocode
    fun provideReverseGeocodeOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("x-ncp-apigw-api-key-id", NAVER_MAP_CLIENT_ID)
                    .addHeader("x-ncp-apigw-api-key", NAVER_MAP_CLIENT_SECRET)
                    .build()
                chain.proceed(request)
            }
            .build()
    }

    @Singleton
    @Provides
    @ReverseGeocode
    fun provideReverseGeocodeRetrofit(
        @ReverseGeocode okHttpClient: OkHttpClient,
        @ReverseGeocode provideReverseGeocodeBaseUrl: String,
    ): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .baseUrl(provideReverseGeocodeBaseUrl)
            .build()
    }

    @Provides
    @Singleton
    @ReverseGeocode
    fun provideReverseGeocodeApi(@ReverseGeocode retrofit: Retrofit): NaverApi {
        return retrofit.create(NaverApi::class.java)
    }
}
