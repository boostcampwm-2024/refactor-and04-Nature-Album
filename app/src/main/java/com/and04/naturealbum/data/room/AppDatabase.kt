package com.and04.naturealbum.data.room

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE `photo_detail` ADD COLUMN `file_name` TEXT NOT NULL DEFAULT ''")
    }
}

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
                    .addMigrations(MIGRATION_1_2)
                    .build()
                    .also { appDataBase ->
                        Instance = appDataBase
                    }
            }
        }
    }
}
