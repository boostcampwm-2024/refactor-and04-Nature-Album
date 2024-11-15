package com.and04.naturealbum.service

import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.IBinder
import androidx.core.net.toUri
import com.and04.naturealbum.data.dto.FirebaseLabel
import com.and04.naturealbum.data.dto.FirebasePhotoInfo
import com.and04.naturealbum.data.repository.FireBaseRepository
import com.and04.naturealbum.data.room.Label
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject

@AndroidEntryPoint
class FirebaseInsertService : Service() {
    @Inject
    lateinit var fireBaseRepository: FireBaseRepository
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var job: Job? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val uid = Firebase.auth.currentUser!!.uid
        val uri = intent?.getStringExtra("uri") as String
        val label = intent.getParcelableExtra<Label>("label")
        val location = intent.getParcelableExtra<Location>("location")
        val description = intent.getStringExtra("description") as String

        val storageJob = scope.launch {
            val storageUri = fireBaseRepository
                .saveImageFile(
                    uid = uid,
                    label = "Test",
                    fileName = "TestFile12",
                    uri = uri.toUri()
                )

            val labelJob = launch {
                if (label != null) {
                    fireBaseRepository
                        .insertLabel(
                            uid = uid,
                            labelName = label.name,
                            labelData = FirebaseLabel(
                                backgroundColor = "FFFFFF",
                                thumbnail = storageUri.toString()
                            )
                        )
                }
            }

            val photoJob = launch {
                fireBaseRepository
                    .insertPhotoInfo(
                        uid = uid,
                        fileName = "파일이름",
                        photoData = FirebasePhotoInfo(
                            uri = uri,
                            label = "라벨명",
                            latitude = location!!.latitude,
                            longitude = location.longitude,
                            description = description,
                            datetime = LocalDateTime.now(ZoneId.of("UTC"))
                        )
                    )
            }

            labelJob.join()
            photoJob.join()

            stopService(intent)
        }

        job = storageJob

        return START_NOT_STICKY
    }

    override fun onBind(p0: Intent?): IBinder? {
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun onDestroy() {
        super.onDestroy()
        job?.cancel()
    }
}