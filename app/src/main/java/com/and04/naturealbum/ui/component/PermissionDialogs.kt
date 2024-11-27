package com.and04.naturealbum.ui.component

import androidx.compose.runtime.Composable
import com.and04.naturealbum.R

enum class PermissionDialogState {
    None,
    ExplainCamera,
    GoToSettings,
}

@Composable
fun PermissionDialogs(
    permissionDialogState: PermissionDialogState,
    onDismiss: () -> Unit,
    onRequestPermission: () -> Unit = {},
    onGoToSettings: () -> Unit = {},
) {
    when (permissionDialogState) {
        PermissionDialogState.ExplainCamera -> {
            MyDialog(
                onConfirmation = {
                    onDismiss()
                    onRequestPermission()
                },
                onDismissRequest = { onDismiss() },
                dialogText = R.string.Home_Screen_permission_explain,
            )
        }

        PermissionDialogState.GoToSettings -> {
            MyDialog(
                onConfirmation = {
                    onDismiss()
                    onGoToSettings()
                },
                onDismissRequest = { onDismiss() },
                dialogText = R.string.Home_Screen_permission_go_to_settings,
            )
        }

        PermissionDialogState.None -> {}
    }
}
