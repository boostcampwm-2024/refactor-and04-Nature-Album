package com.and04.naturealbum.di

import com.and04.naturealbum.BuildConfig.NAVER_MAP_CLIENT_ID
import com.and04.naturealbum.BuildConfig.NAVER_MAP_CLIENT_SECRET
import com.and04.naturealbum.data.retorifit.ReverseGeocodeAPI
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RetrofitModule {
    private const val BASE_URL = "https://naveropenapi.apigw.ntruss.com/"

    @OptIn(ExperimentalSerializationApi::class)
    private val json = Json {
        ignoreUnknownKeys = true // data class에 없는 key 무시
        namingStrategy = JsonNamingStrategy.SnakeCase   // 스네이크 -> 카멜 자동 변환
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("x-ncp-apigw-api-key-id", NAVER_MAP_CLIENT_ID)
                .addHeader("x-ncp-apigw-api-key", NAVER_MAP_CLIENT_SECRET)
                .build()
            chain.proceed(request)
        }
        .build()

    private val mediaType =
        requireNotNull("application/json".toMediaTypeOrNull()) { Exception("mediaType null") }

    @OptIn(ExperimentalSerializationApi::class)
    @Provides
    @Singleton
    fun convertCoordsToAddress(): ReverseGeocodeAPI = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(json.asConverterFactory(mediaType))
        .build()
        .create(ReverseGeocodeAPI::class.java)
}
