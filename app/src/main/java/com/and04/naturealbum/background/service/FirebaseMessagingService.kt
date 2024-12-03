package com.and04.naturealbum.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import com.and04.naturealbum.R
import com.and04.naturealbum.data.repository.FireBaseRepository
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class FirebaseMessagingService : FirebaseMessagingService() {
    @Inject
    lateinit var repository: FireBaseRepository

    // FCM 토큰이 갱신될 때 자동으로 호출
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "Refresh token $token")
        val uid = Firebase.auth.currentUser?.uid ?: return
        CoroutineScope(Dispatchers.IO).launch {
            val success = repository.saveFcmToken(uid, token)
            if (success) {
                Log.d("FCM", "FCM token successfully updated via Repository for user: $uid")
            } else {
                Log.e("FCM", "Failed to update FCM token via Repository for user: $uid")
            }
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val notification = remoteMessage.notification

        notification?.let { remoteNotification ->
            val notificationTitle =
                remoteNotification.title ?: getString(R.string.notification_default_title)
            val notificationBody =
                remoteNotification.body ?: getString(R.string.notification_default_body)

            showNotification(notificationTitle, notificationBody)
        }
    }

    private fun showNotification(title: String, body: String) {
        val notificationId = System.currentTimeMillis().toInt()
        val intent = Intent(Intent.ACTION_VIEW, MY_PAGE_URI.toUri())
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // NotificationChannel 설정 (Android 8.0 이상)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        notificationManager.notify(notificationId, notificationBuilder.build())
    }

    companion object {
        // 알림 채널의 고유 ID, Android 8.0 이상에서는 반드시 사용
        private const val CHANNEL_ID = "nature_album_channel_id"
        private const val MY_PAGE_URI = "naturealbum://my_page"
    }
}
