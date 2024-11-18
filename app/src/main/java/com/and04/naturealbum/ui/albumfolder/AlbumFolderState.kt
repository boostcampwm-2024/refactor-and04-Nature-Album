package com.and04.naturealbum.ui.albumfolder

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.and04.naturealbum.data.room.PhotoDetail
import com.and04.naturealbum.ui.home.PermissionDialogState

@Stable
class AlbumFolderState(
    isDataLoaded: Boolean,
    imgDownLoading: Boolean,
    editMode: Boolean,
    checkList: Set<PhotoDetail>,
    selectAll: Boolean,
    permissionDialogState: PermissionDialogState
) {
    var isDataLoaded = mutableStateOf(isDataLoaded)
    var imgDownLoading = mutableStateOf(imgDownLoading)
    var editMode = mutableStateOf(editMode)
    var checkList = mutableStateOf(checkList)
    var selectAll = mutableStateOf(selectAll)
    var permissionDialogState = mutableStateOf(permissionDialogState)
}

@Composable
fun rememberAlbumFolderState(
    isDataLoaded: Boolean = false,
    imgDownLoading: Boolean = false,
    editMode: Boolean = false,
    checkList: Set<PhotoDetail> = setOf(),
    selectAll: Boolean = false,
    permissionDialogState: PermissionDialogState = PermissionDialogState.None,
): AlbumFolderState {
    return remember {
        AlbumFolderState(
            isDataLoaded = isDataLoaded,
            imgDownLoading = imgDownLoading,
            editMode = editMode,
            checkList = checkList,
            selectAll = selectAll,
            permissionDialogState = permissionDialogState,
        )
    }
}
