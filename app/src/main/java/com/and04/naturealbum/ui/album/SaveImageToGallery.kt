package com.and04.naturealbum.ui.album

import android.content.ContentValues
import android.content.Context
import android.os.Environment
import android.provider.MediaStore
import androidx.core.net.toUri
import com.and04.naturealbum.data.room.PhotoDetail
import java.io.InputStream
import java.io.OutputStream

fun saveImageToGallery(context: Context, photoDetail: PhotoDetail) {
    val resolver = context.contentResolver

    val contentValues = ContentValues().apply {
        put(
            MediaStore.MediaColumns.DISPLAY_NAME,
            "NatureAlbum_${photoDetail.description.replace("\n", "")}"
        )
        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg") //파일형식
        put(
            MediaStore.MediaColumns.RELATIVE_PATH,
            "${Environment.DIRECTORY_DCIM}/NatureAlbum"
        )
    }

    val externalUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

    if (externalUri != null) {
        try {
            resolver.openInputStream(photoDetail.photoUri.toUri()).use { inputStream ->
                resolver.openOutputStream(externalUri).use { outputStream ->
                    if (inputStream != null && outputStream != null) {
                        copyStream(inputStream, outputStream)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            resolver.delete(externalUri, null, null) // 오류 발생 시 생성된 항목 삭제
        }
    }
}

private fun copyStream(input: InputStream, output: OutputStream) {
    val buffer = ByteArray(1024)
    var bytesRead: Int
    while (input.read(buffer).also { bytesRead = it } != -1) {
        output.write(buffer, 0, bytesRead)
    }
}
