package com.and04.naturealbum.service

import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.net.toUri
import com.and04.naturealbum.data.dto.FirebasePhotoInfo
import com.and04.naturealbum.data.dto.LabelData
import com.and04.naturealbum.data.dto.LabelDocument
import com.and04.naturealbum.data.repository.FireBaseRepository
import com.and04.naturealbum.data.room.Label
import com.and04.naturealbum.data.room.Label.Companion.NEW_LABEL
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
        try {
            val uid = Firebase.auth.currentUser!!.uid
            val uri = intent?.getStringExtra(SERVICE_URI) as String
            val fileName = intent.getStringExtra(SERVICE_FILENAME)!!
            val label = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra(SERVICE_LABEL, Label::class.java)!!
            } else {
                intent.getParcelableExtra<Label>(SERVICE_LABEL)!!
            }
            val latitude = intent.getDoubleExtra(SERVICE_LOCATION_LATITUDE, 0.0)
            val longitude = intent.getDoubleExtra(SERVICE_LOCATION_LONGITUDE, 0.0)
            val description = intent.getStringExtra(SERVICE_DESCRIPTION) as String

            val storageJob = scope.launch {
                val storageUri = fireBaseRepository
                    .saveImageFile(
                        uid = uid,
                        label = label.name,
                        fileName = fileName,
                        uri = uri.toUri()
                    )

                if (label.id == NEW_LABEL) {
                    fireBaseRepository
                        .insertLabel(
                            uid = uid,
                            label = LabelDocument(
                                labelName = label.name,
                                labelData = LabelData(
                                    backgroundColor = label.backgroundColor,
                                    thumbnailUri = storageUri.toString()
                                )
                            )
                        )
                }

                fireBaseRepository
                    .insertPhotoInfo(
                        uid = uid,
                        fileName = fileName,
                        photoData = FirebasePhotoInfo(
                            uri = storageUri.toString(),
                            label = label.name,
                            latitude = latitude,
                            longitude = longitude,
                            description = description,
                            datetime = LocalDateTime.now(ZoneId.of("UTC"))
                        )
                    )

                stopService(intent)
            }

            job = storageJob
        } catch (e: NullPointerException) {
            Log.e("FirebaseInsertService", e.toString())
            stopService(intent)
        }

        return START_NOT_STICKY
    }

    override fun onBind(p0: Intent?): IBinder? {
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun onDestroy() {
        super.onDestroy()
        job?.cancel()
    }

    companion object {
        const val SERVICE_URI = "service_uri"
        const val SERVICE_FILENAME = "service_filename"
        const val SERVICE_LABEL = "service_label"
        const val SERVICE_LOCATION_LATITUDE = "service_location_latitude"
        const val SERVICE_LOCATION_LONGITUDE = "service_location_longitude"
        const val SERVICE_DESCRIPTION = "service_location"
    }
}
