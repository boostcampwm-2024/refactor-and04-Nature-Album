package com.and04.naturealbum

import android.app.Application
import com.naver.maps.map.NaverMapSdk
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class NatureAlbum : Application() {
    override fun onCreate() {
        super.onCreate()
        myApplication = this

        NaverMapSdk.getInstance(this).client =
            NaverMapSdk.NaverCloudPlatformClient(BuildConfig.NAVER_MAP_CLIENT_ID)
    }

    companion object {
        private lateinit var myApplication: NatureAlbum
        fun getInstance(): NatureAlbum = myApplication
    }
}
