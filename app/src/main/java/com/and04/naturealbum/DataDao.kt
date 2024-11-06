package com.and04.naturealbum

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LabelDao {
    @Insert
    fun insertLabel(label: Label)

    @Query("SELECT * FROM label")
    fun getAllLabel(): List<Label>

    @Query("SELECT name FROM label")
    fun getAllNameOfLabel(): List<String>

    @Query("SELECT name FROM label WHERE id = :id")
    fun getLabelById(id: Int): String

    @Query("SELECT id FROM label WHERE name = :name")
    fun getIdByName(name: String): Int?
}

@Dao
interface AlbumDao {
    @Insert
    fun insertAlbum(album: Album)

    @Query("SELECT * FROM album")
    fun getALLAlbum(): Flow<List<Album>>

    @Query("SELECT name FROM label WHERE id = :id")
    fun getLabelNameById(id: Int): String

    @Query("SELECT photo_uri FROM photo_detail WHERE id = :id")
    fun getPhotoDetailUriById(id: Int): String
}

@Dao
interface PhotoDetailDao {
    @Insert
    fun insertPhotoDetail(photoDetail: PhotoDetail)

    @Query("SELECT * FROM photo_detail")
    fun getAllPhotoDetail(): Flow<List<PhotoDetail>>

    @Query("SELECT * FROM photo_detail WHERE id = :id")
    fun getPhotoDetailById(id: Int): PhotoDetail

    @Query("SELECT photo_uri FROM photo_detail WHERE id = :id")
    fun getPhotoDetailUriById(id: Int): String

    @Query("SELECT photo_uri FROM photo_detail WHERE label_id = :labelId")
    fun getAllPhotoDetailUriByLabelId(labelId: Int): List<String>
}
