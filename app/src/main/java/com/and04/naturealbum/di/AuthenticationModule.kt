package com.and04.naturealbum.di

import android.content.Context
import com.and04.naturealbum.data.repository.FireBaseRepository
import com.and04.naturealbum.ui.mypage.AuthenticationManager
import com.and04.naturealbum.ui.mypage.UserManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthenticationModule {
    @Singleton
    @Provides
    fun providerAuthenticationManager(
        @ApplicationContext context: Context,
        fireBaseRepository: FireBaseRepository
    ): AuthenticationManager = AuthenticationManager(context, fireBaseRepository)

    @Provides
    fun providerUserManager() = UserManager
}
