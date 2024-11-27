package com.and04.naturealbum.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

class DataStoreManager(
    private val context: Context
) {
    private val Context.dataStore by preferencesDataStore(name = SYNC_TIME)
    val syncTime = context.dataStore.data.map { time ->
        time[SYNC_TIME_KEY] ?: NEVER_SYNC
    }

    suspend fun setSyncTime(dateTime: String) {
        context.dataStore.edit { store ->
            store[SYNC_TIME_KEY] = dateTime
        }
    }

    suspend fun clear() {
        context.dataStore.edit { store ->
            store.clear()
        }
    }

    companion object {
        private const val SYNC_TIME = "sync_time"
        const val NEVER_SYNC = "이력 없음"
        private val SYNC_TIME_KEY = stringPreferencesKey(SYNC_TIME)
    }
}
