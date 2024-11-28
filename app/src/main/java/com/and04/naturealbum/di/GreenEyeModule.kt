package com.and04.naturealbum.di

import com.and04.naturealbum.BuildConfig.NAVER_EYE_SECRET
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
object GreenEyeModule {
    @Provides
    @GreenEye
    fun provideGreenEyeBaseUrl(): String = "https://clovagreeneye.apigw.ntruss.com/"

    @Singleton
    @Provides
    @GreenEye
    fun provideGreenEyeOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("X-GREEN-EYE-SECRET", NAVER_EYE_SECRET)
                    .addHeader("Content-Type", "application/json")
                    .build()
                chain.proceed(request)
            }
            .build()
    }

    @Singleton
    @Provides
    @GreenEye
    fun provideGreenEyeRetrofit(
        @GreenEye okHttpClient: OkHttpClient,
        @GreenEye provideGreenEyeBaseUrl: String,
    ): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .baseUrl(provideGreenEyeBaseUrl)
            .build()
    }

    @Provides
    @Singleton
    @GreenEye
    fun provideGreenEyeApi(@GreenEye retrofit: Retrofit): NaverApi {
        return retrofit.create(NaverApi::class.java)
    }
}

