package com.and04.naturealbum

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "label")
data class Label(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Int=0,
    @ColumnInfo(name = "background_color") val backgroundColor: String,
    @ColumnInfo(name = "name") val name: String
)

@Entity(tableName = "album")
data class Album(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Int=0,
    @ColumnInfo(name = "label_id") val labelId: Int,
    @ColumnInfo(name = "photo_detail_id") val photoDetailId: Int
)

@Entity(tableName = "photo_detail")
data class PhotoDetail(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Int=0,
    @ColumnInfo(name = "label_id") val labelId: Int,
    @ColumnInfo(name = "photo_uri") val photoUri: String,
    @ColumnInfo(name = "location") val location: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "datetime") val datetime: String
)
