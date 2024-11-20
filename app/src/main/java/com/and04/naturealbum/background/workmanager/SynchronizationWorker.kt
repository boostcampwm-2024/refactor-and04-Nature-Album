package com.and04.naturealbum.background.workmanager

import android.content.Context
import androidx.core.net.toUri
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
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

@HiltWorker
class SynchronizationWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val roomRepository: DataRepository,
    private val fireBaseRepository: FireBaseRepository
) : CoroutineWorker(appContext, workerParams) {

    private val supervisor = SupervisorJob()
    private val job = CoroutineScope(Dispatchers.IO + supervisor)
    //TODO 이미지 중복 저장 안할 수 있는 좋은 방법 찾기
    override suspend fun doWork(): Result {
        val currentUser = Firebase.auth.currentUser ?: return Result.failure()

        withContext(Dispatchers.IO) {
            // Label
            launch {
                val labels = fireBaseRepository.getLabels(currentUser.uid)
                val albums = roomRepository.getSynchronizedAlbums() //TODO 더 유용한 쿼리 있나 찾아보기

                val synchronizedAlbums =
                    albums.filter { album -> !labels.contains(album.labelName) }

                synchronizedAlbums.forEach { album ->
                    job.launch {
                        insertLabel(currentUser.uid, album)
                    }
                }
            }
            // PhotoDetail
            launch {
                val fileNames = fireBaseRepository.getPhotos(currentUser.uid)
                val photoDetails = roomRepository.getSynchronizedPhotoDetails()

                val synchronizedPhotoDetails =
                    photoDetails.filter { photo -> !fileNames.contains(photo.fileName) }

                synchronizedPhotoDetails.forEach { photo ->
                    job.launch {
                        insertPhotoDetail(currentUser.uid, photo)
                    }
                }
            }
        }

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
