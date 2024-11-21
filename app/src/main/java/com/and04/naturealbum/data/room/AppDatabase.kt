package com.and04.naturealbum.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [Label::class, Album::class, PhotoDetail::class],
    version = 2,
    exportSchema = false
)

@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun labelDao(): LabelDao
    abstract fun albumDao(): AlbumDao
    abstract fun photoDetailDao(): PhotoDetailDao

    companion object {
        @Volatile
        private var Instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, AppDatabase::class.java, "nature_album_database")
                    .build()
                    .also { appDataBase ->
                        Instance = appDataBase
                    }
            }
        }
    }
}
