package com.and04.naturealbum.background.workmanager

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

/**
 * 전제 조건 - 로그인 상태
 */
@HiltWorker
class SynchronizationWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters
) : Worker(appContext, workerParams) {
    override fun doWork(): Result {
        //TODO 동기화 작업

        return Result.success()
    }
}
