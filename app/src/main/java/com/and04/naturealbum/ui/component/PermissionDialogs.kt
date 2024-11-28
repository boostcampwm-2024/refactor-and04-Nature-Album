package com.and04.naturealbum.ui.component

import androidx.compose.runtime.Composable
import com.and04.naturealbum.R

enum class PermissionDialogState {
    None,
    Explain,
    GoToSettings,
    AlarmExplain,
}

@Composable
fun PermissionDialogs(
    permissionDialogState: PermissionDialogState,
    onDismiss: () -> Unit,
    onRequestPermission: () -> Unit = {},
    onGoToSettings: () -> Unit = {},
) {
    when (permissionDialogState) {
        PermissionDialogState.Explain -> {
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

        PermissionDialogState.AlarmExplain -> {
            MyDialog(
                onConfirmation = {
                    onDismiss()
                    onRequestPermission()
                },
                onDismissRequest = { onDismiss() },
                dialogText = R.string.my_page_permission_alarm_explain,
            )
        }

        PermissionDialogState.None -> {}
    }
}
