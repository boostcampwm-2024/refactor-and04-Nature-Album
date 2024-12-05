package com.and04.naturealbum.ui.component

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable

data class PermissionDialogState(
    val onDismiss: () -> Unit,
    val onConfirmation: () -> Unit,
    @StringRes val dialogText: Int,
)

@Composable
fun PermissionDialogs(permissionDialogState: PermissionDialogState?) {
    permissionDialogState?.let { dialogState ->
        MyDialog(
            onConfirmation = {
                dialogState.onDismiss()
                dialogState.onConfirmation()
            },
            onDismissRequest = dialogState.onDismiss,
            dialogText = dialogState.dialogText,
        )
    }
}
