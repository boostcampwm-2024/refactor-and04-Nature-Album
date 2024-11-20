package com.and04.naturealbum.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.and04.naturealbum.ui.friend.FriendTestScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            //NatureAlbumApp()
            FriendTestScreen() // TODO: 친구 기능 테스트 용도. 추후 UI 통합 후 제거 예정.
        }
    }
}
