package com.and04.naturealbum.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.and04.naturealbum.data.repository.FireBaseRepository
import com.and04.naturealbum.ui.MainActivity
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

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "Refresh token $token")
        // TODO: 아래 새롭게 설정하는 부분 필요 없으면 제거
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
        remoteMessage.notification?.let {
            Log.d("FCM", "Notification Title: ${it.title}")
            Log.d("FCM", "Notification Body: ${it.body}")
            showNotification(it.title ?: "알림 제목 없음", it.body ?: "알림 내용 없음") // 포그라운드일 때도 보여주기 위함
        }
    }

    private fun showNotification(title: String, body: String) {
        val channelId = "default_channel_id"
        val notificationId = System.currentTimeMillis().toInt()

        // TODO: 현재는 앱 열기. 추후 MyPage로 여는 것으로 교체
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(android.R.drawable.star_on)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Android 8.0 이상 채널 설정
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "기본 알림 채널",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        // 알림 표시
        notificationManager.notify(notificationId, notificationBuilder.build())
    }
}
