package com.and04.naturealbum.data.retorifit

import com.and04.naturealbum.BuildConfig.NAVER_MAP_CLIENT_ID
import com.and04.naturealbum.BuildConfig.NAVER_MAP_CLIENT_SECRET
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ReverseGeocodeAPIFactory {
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

    fun create(): NaverAPI = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(NaverAPI::class.java)
}
