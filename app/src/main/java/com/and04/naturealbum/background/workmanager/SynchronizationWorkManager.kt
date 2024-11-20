package com.and04.naturealbum.background.workmanager

import android.content.Context
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest

class SynchronizationWorkManager(
    private val context: Context
) {
    private val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.UNMETERED) //Wifi 연결 시 실행
        .build()

    private val workRequest: WorkRequest =
        OneTimeWorkRequestBuilder<SynchronizationWorker>()
            .setConstraints(constraints)
            .build()

    fun startWorkManager() {
        WorkManager
            .getInstance(context)
            .enqueue(workRequest)
    }
}
