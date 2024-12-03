package com.and04.naturealbum.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.and04.naturealbum.ui.navigation.NavigateDestination
import com.and04.naturealbum.ui.theme.NatureAlbumTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val startDestination = handleDeepLink(intent)
        val uri = intent?.data
        Log.d("YUJIN", "URI: ${uri} / URL HOST : ${uri?.host}")
        setContent {
            NatureAlbumTheme {
                NatureAlbumApp(startDestination = startDestination)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val startDestination = handleDeepLink(intent)

        setContent {
            NatureAlbumTheme {
                NatureAlbumApp(startDestination = startDestination)
            }
        }
    }

    private fun handleDeepLink(intent: Intent?): String {
        Log.d("YUJIN", "intent?.extras?.getString(\"deeplink\")")
        val deeplink = intent?.extras?.getString("deeplink") ?: intent?.data?.toString()
        return when (deeplink) {
            "naturealbum://my_page" -> NavigateDestination.MyPage.route
            else -> NavigateDestination.Home.route
        }
    }
}
