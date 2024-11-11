package com.and04.naturealbum.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.and04.naturealbum.data.dto.AlbumDto

@Dao
interface LabelDao {
    @Query("SELECT * FROM label")
    fun getAllLabel(): List<Label>

    @Query("SELECT name FROM label")
    fun getAllNameOfLabel(): List<String>

    @Query("SELECT * FROM label WHERE id = :id")
    suspend fun getLabelById(id: Int): Label

    @Query("SELECT id FROM label WHERE name = :name")
    fun getIdByName(name: String): Int?

    @Query("SELECT background_color FROM label WHERE id = :id")
    fun getLabelBackgroundColorById(id: Int): String

    @Query("SELECT name FROM label WHERE id = :id")
    fun getLabelNameById(id: Int): String

    @Insert
    fun insertLabel(label: Label): Long
}

@Dao
interface AlbumDao {
    @Insert
    fun insertAlbum(album: Album): Long

    @Query(
        """
        SELECT
            label.id AS labelId,
            label.name AS labelName,
            label.background_color AS labelBackgroundColor,
            photo_detail.photo_uri AS photoDetailUri           
        FROM
            album
        JOIN label ON album.label_id = label.id
        JOIN photo_detail ON album.label_id = photo_detail.label_id
            
    """
    )
    fun getAllAlbum(): List<AlbumDto>

    @Query("SELECT * FROM album")
    fun getALLAlbum(): List<Album>

    @Query("SELECT * FROM album WHERE label_id = :labelId")
    fun getAlbumByLabelId(labelId: Int): List<Album>
}

@Dao
interface PhotoDetailDao {
    @Insert
    fun insertPhotoDetail(photoDetail: PhotoDetail): Long

    @Query("SELECT * FROM photo_detail")
    fun getAllPhotoDetail(): List<PhotoDetail>

    @Query("SELECT * FROM photo_detail WHERE id = :id")
    fun getPhotoDetailById(id: Int): PhotoDetail

    @Query("SELECT photo_uri FROM photo_detail WHERE id = :id")
    fun getPhotoDetailUriById(id: Int): String

    @Query("SELECT * FROM photo_detail WHERE label_id = :labelId")
    suspend fun getAllPhotoDetailsUriByLabelId(labelId: Int): List<PhotoDetail>

}
