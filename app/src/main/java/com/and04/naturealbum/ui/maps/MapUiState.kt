package com.and04.naturealbum.ui.maps

import android.graphics.PointF
import com.and04.naturealbum.data.dto.FirebaseFriend
import com.and04.naturealbum.ui.component.BottomSheetState

data class MapUiState(
    val photosByUid: Map<String, List<PhotoItem>> = emptyMap(),
    val pick: PhotoItem? = null,
    val showPhotoContent: Boolean = false,
    val cameraPivot: PointF = PointF(0.5f, 0.5f),
    val isSignIn: Boolean = false,
    val bottomSheetUiState: BottomSheetUiState = BottomSheetUiState(),
    val dialogUiState: DialogUiState = DialogUiState()
)

data class BottomSheetUiState(
    val bottomSheetState: BottomSheetState = BottomSheetState.Hide,
    val photos: List<PhotoItem> = emptyList()
)

data class DialogUiState(
    val isVisible: Boolean = false,
    val friends: List<FirebaseFriend> = emptyList(),
    val selectedFriends: List<FirebaseFriend> = emptyList(),
    val userSelectMax: Int = USER_SELECT_MAX
) {
    companion object {
        const val USER_SELECT_MAX = 4
    }
}
