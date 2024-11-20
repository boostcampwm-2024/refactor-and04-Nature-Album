package com.and04.naturealbum

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.and04.naturealbum.background.workmanager.SynchronizationWorkManager
import com.naver.maps.map.NaverMapSdk
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class NatureAlbum : Application(), Configuration.Provider {
    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        myApplication = this

        SynchronizationWorkManager(this).startWorkManager()

        NaverMapSdk.getInstance(this).client =
            NaverMapSdk.NaverCloudPlatformClient(BuildConfig.NAVER_MAP_CLIENT_ID)
    }

    companion object {
        private lateinit var myApplication: NatureAlbum
        fun getInstance(): NatureAlbum = myApplication
    }
}
