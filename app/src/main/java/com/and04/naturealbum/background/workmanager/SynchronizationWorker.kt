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
import com.and04.naturealbum.data.dto.FirebaseLabelResponse
import com.and04.naturealbum.data.dto.FirebasePhotoInfo
import com.and04.naturealbum.data.dto.FirebasePhotoInfoResponse
import com.and04.naturealbum.data.dto.SyncAlbumsDto
import com.and04.naturealbum.data.dto.SyncPhotoDetailsDto
import com.and04.naturealbum.data.repository.DataRepository
import com.and04.naturealbum.data.repository.FireBaseRepository
import com.and04.naturealbum.data.room.Album
import com.and04.naturealbum.data.room.Label
import com.and04.naturealbum.data.room.PhotoDetail
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
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

    override suspend fun doWork(): Result = coroutineScope {
        try {
            val currentUser = Firebase.auth.currentUser ?: return@coroutineScope Result.failure()
            val uid = currentUser.uid

            val label = async {
                val labels = fireBaseRepository.getLabels(uid)
                val allLocalLabels = roomRepository.getSyncCheckAlbums()

                // 서버에 없는 Local Data
                val unSynchronizedLabelsToServer = allLocalLabels.filter { label ->
                    labels.none { firebaseLabel ->
                        firebaseLabel.labelName == label.labelName
                    }
                }
                // 로컬에 없는 서버 데이터
                val unSynchronizedLabelsToLocal = labels.filter { label ->
                    allLocalLabels.none { localLabel ->
                        localLabel.labelName == label.labelName
                    }
                }


                // 로컬 > 서버
                unSynchronizedLabelsToServer.forEach { label ->
                    launch {
                        insertLabelToServer(uid, label)
                    }
                }

                // 서버 > 로컬
                unSynchronizedLabelsToLocal.forEach { label ->
                    launch {
                        insertLabelToLocal(label)
                    }
                }
            }

            val photoDetail = async {
                val photos = fireBaseRepository.getPhotos(uid)
                val allLocalPhotos = roomRepository.getUnSynchronizedPhotoDetails()

                val unSynchronizedPhotoDetailsToServer = allLocalPhotos.filter { photo ->
                    photos.none { firebasePhoto ->
                        firebasePhoto.fileName == photo.fileName
                    }
                }

                // 로컬 > 서버
                unSynchronizedPhotoDetailsToServer.forEach { photo ->
                    launch {
                        insertPhotoDetail(uid, photo)
                    }
                }

//                unSynchronizedPhotoDetailsToLocal = photos.filter { photo ->
//                    allLocalPhotos.none { localPhoto ->
//                        localPhoto.fileName == photo.fileName
//                    }
//                }
            }

            label.await()
            photoDetail.await()

            // 서버 > 로컬
//            unSynchronizedPhotoDetailsToLocal?.forEach { photo ->
//                launch {
//                    insertPhotoDetailToLocal(photo)
//                }
//            }
//            insertLocalAlbum()
//            roomRepository.insertPhotoInAlbum(
//                Album(
//                    labelId = labelId,
//                    photoDetailId = photoDetailId.await().toInt()
//                )
//            )

            Result.success()
        } catch (e: Exception) {
            //TODO FireStore와 LocalDB 비교 후 같이면 Result.success() 다르면 retry()
            Result.retry()
        } finally {
            runSync(applicationContext)
        }
    }

    private suspend fun insertLabelToServer(uid: String, label: SyncAlbumsDto) {
        val storageUri = fireBaseRepository
            .saveImageFile(
                uid = uid,
                label = label.labelName,
                fileName = label.fileName,
                uri = label.photoDetailUri.toUri(),
            )

        fireBaseRepository
            .insertLabel(
                uid = uid,
                labelName = label.labelName,
                labelData = FirebaseLabel(
                    backgroundColor = label.labelBackgroundColor,
                    thumbnailUri = storageUri.toString(),
                    fileName = label.fileName
                )
            )
    }

    private suspend fun insertLabelToLocal(label: FirebaseLabelResponse) =
        withContext(Dispatchers.IO + SupervisorJob()) {
            val localLabelData = Label(
                backgroundColor = label.backgroundColor,
                name = label.labelName
            )

            roomRepository.insertLabel(localLabelData)
        }

    private suspend fun insertPhotoDetail(uid: String, photo: SyncPhotoDetailsDto) {
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

//    private suspend fun insertPhotoDetailToLocal(photo: FirebasePhotoInfoResponse) =
//        withContext(Dispatchers.IO + SupervisorJob()) {
//            val photoDetailId = async {
//                roomRepository.insertPhoto(
//                    PhotoDetail(
//                        labelId = labelId,
//                        photoUri = uri,
//                        fileName = fileName,
//                        latitude = location.latitude,
//                        longitude = location.longitude,
//                        description = description,
//                        datetime = time,
//                    )
//                )
//            }
//        }
}
