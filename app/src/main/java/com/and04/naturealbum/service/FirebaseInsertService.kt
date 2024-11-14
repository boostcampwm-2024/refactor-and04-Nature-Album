package com.and04.naturealbum.service

import android.app.Service
import android.content.Intent
import android.location.Location
import android.net.Uri
import android.os.IBinder
import android.util.Log
import androidx.core.net.toUri
import com.and04.naturealbum.data.dto.FirebaseLabel
import com.and04.naturealbum.data.dto.FirebasePhotoInfo
import com.and04.naturealbum.data.repository.FireBaseRepository
import com.and04.naturealbum.data.room.Label
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
    private var doneInsertLabel = false
    private var doneInsertPhotoInfo = false
    private var storageUri: Uri? = null
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var jobs = mutableListOf<Job>()

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val uid = "uid"
        val uri = intent?.getStringExtra("uri") as String
        val label = intent.getParcelableExtra<Label>("label")
        val location = intent.getParcelableExtra<Location>("location")
        val description = intent.getStringExtra("description") as String

        val storageJob = scope.launch {
            fireBaseRepository.saveImageFile(
                uid = uid,
                label = "Test",
                fileName = "TestFile12",
                uri = uri.toUri()
            ).addOnSuccessListener { imageTask ->
                imageTask.storage.downloadUrl
                    .addOnSuccessListener { downloadUrl ->
                        storageUri = downloadUrl
                    }
            }.addOnFailureListener { exception ->
                stopService(intent)
            }
        }

        val storeJop = scope.launch {
            while(true){
                if(storageUri != null) break
            }
            launch {
                if (label != null) {
                    fireBaseRepository.insertLabel(
                        uid = uid,
                        labelName = label.name,
                        labelData = FirebaseLabel(
                            backgroundColor = "FFFFFF",
                            thumbnail = storageUri.toString()
                        )
                    ).addOnSuccessListener { doneInsertLabel = true }
                    .addOnFailureListener { doneInsertLabel = true }
                }
            }

            launch {
                fireBaseRepository.insertPhotoInfo(
                    uid = uid,
                    uri = "파일이름",
                    photoData = FirebasePhotoInfo(
                        uri = uri,
                        label = "라벨명",
                        latitude = location!!.latitude,
                        longitude = location.longitude,
                        description = description,
                        datetime = LocalDateTime.now(ZoneId.of("UTC"))
                    )
                ).addOnSuccessListener { doneInsertPhotoInfo = true }
                .addOnFailureListener { doneInsertPhotoInfo = true }
            }
        }


        val checkJob = scope.launch {
            while (true) {
                if (doneInsertLabel && doneInsertPhotoInfo) break
            }
            stopService(intent)
        }

        jobs.add(storageJob)
        jobs.add(storeJop)
        jobs.add(checkJob)

        return START_NOT_STICKY
    }

    override fun onBind(p0: Intent?): IBinder? {
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun onDestroy() {
        super.onDestroy()
        jobs.forEach { job -> job.cancel() }
    }
}