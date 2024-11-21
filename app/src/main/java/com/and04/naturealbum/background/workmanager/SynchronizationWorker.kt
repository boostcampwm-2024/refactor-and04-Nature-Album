package com.and04.naturealbum.background.workmanager

import android.content.Context
import androidx.core.net.toUri
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.and04.naturealbum.data.dto.FirebaseLabel
import com.and04.naturealbum.data.dto.FirebasePhotoInfo
import com.and04.naturealbum.data.dto.SynchronizedAlbumsDto
import com.and04.naturealbum.data.dto.SynchronizedPhotoDetailsDto
import com.and04.naturealbum.data.repository.DataRepository
import com.and04.naturealbum.data.repository.FireBaseRepository
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit

@HiltWorker
class SynchronizationWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val roomRepository: DataRepository,
    private val fireBaseRepository: FireBaseRepository
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        private const val WORKER_NAME = "MIDNIGHT_SYNCHRONIZATION"
        private const val HOUR = 0
        private const val MINUTE = 0

        fun runSync(context: Context) {
            val duration = getDurationTime()

            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.UNMETERED) //Wifi 연결 시 실행
                .build()

            val workRequest = OneTimeWorkRequestBuilder<SynchronizationWorker>()
                .setInitialDelay(duration.seconds, TimeUnit.SECONDS) // 지금부터 정각까지 지연 후 실행
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context)
                .enqueueUniqueWork(
                    WORKER_NAME,
                    ExistingWorkPolicy.REPLACE, //기존 작업을 새 작업으로 전환
                    workRequest
                )
        }

        fun cancel(context: Context) {
            WorkManager.getInstance(context)
                .cancelUniqueWork(WORKER_NAME)
        }

        private fun getDurationTime(): Duration {
            val triggerHour = HOUR
            val triggerMinute = MINUTE

            val newSyncTime = LocalTime.of(triggerHour, triggerMinute)
            val now: LocalDateTime = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES)
            val nowTime: LocalTime = now.toLocalTime()

            val plusDay = if (nowTime == newSyncTime || nowTime.isAfter(newSyncTime)) 1 else 0

            val nextTriggerTime = now.plusDays(plusDay.toLong())
                .withHour(newSyncTime.hour)
                .withMinute(newSyncTime.minute)

            return Duration.between(LocalDateTime.now(), nextTriggerTime) //다음 정각까지 남은 시간
        }
    }

    private val supervisor = SupervisorJob()
    private val job = CoroutineScope(Dispatchers.IO + supervisor)

    //TODO 이미지 중복 저장 안할 수 있는 좋은 방법 찾기
    override suspend fun doWork(): Result {
        val currentUser = Firebase.auth.currentUser ?: return Result.failure()
        val uid = currentUser.uid
        withContext(Dispatchers.IO) {
            // Label
            launch {
                val labels = fireBaseRepository.getLabels(uid)
                val synchronizedAlbums = roomRepository.getSynchronizedAlbums(labels)

                synchronizedAlbums.forEach { album ->
                    job.launch {
                        insertLabel(uid, album)
                    }
                }
            }
            // PhotoDetail
            launch {
                val fileNames = fireBaseRepository.getPhotos(uid)
                val synchronizedPhotoDetails =
                    roomRepository.getSynchronizedPhotoDetails(fileNames)

                synchronizedPhotoDetails.forEach { photo ->
                    job.launch {
                        insertPhotoDetail(uid, photo)
                    }
                }
            }
        }
        
        supervisor.cancel()
        runSync(applicationContext)
        return Result.success()
    }

    private suspend fun insertLabel(uid: String, album: SynchronizedAlbumsDto) {
        val storageUri = fireBaseRepository
            .saveImageFile(
                uid = uid,
                label = album.labelName,
                fileName = album.fileName,
                uri = album.photoDetailUri.toUri(),
            )

        fireBaseRepository
            .insertLabel(
                uid = uid,
                labelName = album.labelName,
                labelData = FirebaseLabel(
                    backgroundColor = album.labelBackgroundColor,
                    thumbnailUri = storageUri.toString()
                )
            )
    }

    private suspend fun insertPhotoDetail(uid: String, photo: SynchronizedPhotoDetailsDto) {
        val storageUri = fireBaseRepository
            .saveImageFile(
                uid = uid,
                label = photo.labelName,
                fileName = photo.fileName,
                uri = photo.photoDetailUri.toUri(),
            )

        fireBaseRepository
            .insertPhotoInfo(
                uid = uid,
                fileName = photo.fileName,
                photoData = FirebasePhotoInfo(
                    uri = storageUri.toString(),
                    label = photo.labelName,
                    latitude = photo.latitude,
                    longitude = photo.longitude,
                    description = photo.description,
                    datetime = photo.datetime
                )
            )
    }
}
