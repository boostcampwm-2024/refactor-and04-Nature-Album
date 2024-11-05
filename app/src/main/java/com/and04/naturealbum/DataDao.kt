package com.and04.naturealbum

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface LabelDao {
    @Insert
    fun insertLabel(label: Label)

    @Query("SELECT * FROM label")
    fun getAllLabel(): List<Label>

    @Query("SELECT name FROM label")
    fun getAllNameOfLabel(): List<String>

    @Query("SELECT name FROM label WHERE id = :id")
    fun getLabelById(id: Int): Label
}

@Dao
interface AlbumDao {
    @Insert
    fun insertAlbum(album: Album)

    @Query("SELECT * FROM album")
    fun getALLAlbum(): List<Album>
}

@Dao
interface PhotoDetailDao {
    @Insert
    fun insertPhotoDetail(photoDetail: PhotoDetail)

    @Query("SELECT * FROM photo_detail")
    fun getAllPhotoDetail(): List<PhotoDetail>

    @Query("SELECT * FROM photo_detail WHERE id = :id")
    fun getPhotoDetailById(id: Int): PhotoDetail

    @Query("SELECT photo_uri FROM photo_detail WHERE id = :id")
    fun getPhotoDetailUriById(id: Int): PhotoDetail
}
