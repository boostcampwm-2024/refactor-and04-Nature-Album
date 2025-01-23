package com.and04.naturealbum.background.sync

import android.content.Context
import android.util.Log
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.work.Configuration
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import androidx.work.testing.SynchronousExecutor
import androidx.work.testing.TestListenableWorkerBuilder
import androidx.work.testing.WorkManagerTestInitHelper
import com.and04.naturealbum.background.workmanager.SynchronizationWorker
import com.and04.naturealbum.data.localdata.datastore.DataStoreManager
import com.and04.naturealbum.data.repository.TestAiRepoImpl
import com.and04.naturealbum.data.repository.firebase.TestAlbumRepoImpl
import com.and04.naturealbum.data.repository.local.TestLocalDataRepoImpl
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class SyncWorkManagerTest {

    @Test
    fun syncTest() {
        val request =
            TestListenableWorkerBuilder<SynchronizationWorker>(getApplicationContext())
                .setWorkerFactory(TestWorkerFactory(getApplicationContext()))
                .build()

        runBlocking {
            val result = request.doWork()
            assertThat(result).isEqualTo(ListenableWorker.Result.success())
        }
    }
}

class TestWorkerFactory(private val context: Context) : WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker {
        return SynchronizationWorker(
            appContext,
            workerParameters,
            TestLocalDataRepoImpl(),
            TestAlbumRepoImpl(),
            DataStoreManager(context),
            TestAiRepoImpl()
        )
    }
}
