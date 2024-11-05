package com.and04.naturealbum

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(
    entities = [User::class, Label::class, Album::class, PhotoDetail::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
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
                    .also { Instance = it }
            }
        }
    }
}
