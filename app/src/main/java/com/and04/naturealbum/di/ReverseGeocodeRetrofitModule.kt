package com.and04.naturealbum.di

import com.and04.naturealbum.BuildConfig.NAVER_MAP_CLIENT_ID
import com.and04.naturealbum.BuildConfig.NAVER_MAP_CLIENT_SECRET
import com.and04.naturealbum.data.retorifit.ReverseGeocodeAPI
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
object ReverseGeocodeRetrofitModule {
    private const val BASE_URL = "https://naveropenapi.apigw.ntruss.com/"

    private val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("x-ncp-apigw-api-key-id", NAVER_MAP_CLIENT_ID)
                .addHeader("x-ncp-apigw-api-key", NAVER_MAP_CLIENT_SECRET)
                .build()
            chain.proceed(request)
        }
        .build()

    @Provides
    @Singleton
    fun provideReverseGeocodeAPI(): ReverseGeocodeAPI = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ReverseGeocodeAPI::class.java)
}
