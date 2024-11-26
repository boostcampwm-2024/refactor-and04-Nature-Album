package com.and04.naturealbum.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.and04.naturealbum.data.dto.AlbumDto

@Dao
interface LabelDao {
    @Query("SELECT * FROM label")
    suspend fun getAllLabel(): List<Label>

    @Query("SELECT name FROM label")
    suspend fun getAllNameOfLabel(): List<String>

    @Query("SELECT * FROM label WHERE id = :id")
    suspend fun getLabelById(id: Int): Label

    @Query("SELECT id FROM label WHERE name = :name")
    suspend fun getIdByName(name: String): Int?

    @Query("SELECT background_color FROM label WHERE id = :id")
    suspend fun getLabelBackgroundColorById(id: Int): String

    @Query("SELECT name FROM label WHERE id = :id")
    suspend fun getLabelNameById(id: Int): String

    @Insert
    suspend fun insertLabel(label: Label): Long
}

@Dao
interface AlbumDao {
    @Insert
    suspend fun insertAlbum(album: Album): Long

    @Update
    suspend fun updateAlbum(album: Album)

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
        JOIN photo_detail ON album.photo_detail_id = photo_detail.id
            
    """
    )
    suspend fun getAllAlbum(): List<AlbumDto>

    @Query("SELECT * FROM album")
    suspend fun getALLAlbum(): List<Album>

    @Query("SELECT * FROM album WHERE label_id = :labelId")
    suspend fun getAlbumByLabelId(labelId: Int): List<Album>
}

@Dao
interface PhotoDetailDao {
    @Insert
    suspend fun insertPhotoDetail(photoDetail: PhotoDetail): Long

    @Query("SELECT * FROM photo_detail")
    suspend fun getAllPhotoDetail(): List<PhotoDetail>

    @Query("SELECT * FROM photo_detail WHERE id = :id")
    suspend fun getPhotoDetailById(id: Int): PhotoDetail

    @Query("SELECT photo_uri FROM photo_detail WHERE id = :id")
    suspend fun getPhotoDetailUriById(id: Int): String

    @Query("SELECT * FROM photo_detail WHERE label_id = :labelId")
    suspend fun getAllPhotoDetailsUriByLabelId(labelId: Int): List<PhotoDetail>

    @Query("SELECT hazard_check_result FROM photo_detail WHERE id = :id")
    suspend fun getHazardCheckResult(id: Int): HazardCheckStatus
}
