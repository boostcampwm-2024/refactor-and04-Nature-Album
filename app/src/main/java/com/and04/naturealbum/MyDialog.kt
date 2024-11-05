package com.and04.naturealbum

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource


@Composable
fun MyDialog(
    dialogData: DialogData,
) {
    AlertDialog(
        icon = {
            if (dialogData.icon != null && dialogData.iconDescription != null) {
                Icon(
                    painter = painterResource(dialogData.icon),
                    contentDescription = stringResource(dialogData.iconDescription)
                )
            }
        },
        title = {
            dialogData.dialogTitle?.let { title -> Text(text = stringResource(title)) }
        },
        text = {
            dialogData.dialogText?.let { text -> Text(text = stringResource(text)) }
        },
        onDismissRequest = {
            dialogData.onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    dialogData.onConfirmation()
                }
            ) {
                Text(stringResource(R.string.dialog_confirm_button))
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    dialogData.onDismissRequest()
                }
            ) {
                Text(stringResource(R.string.dialog_dismiss_button))
            }
        }
    )
}

data class DialogData(
    val onDismissRequest: () -> Unit = {},
    val onConfirmation: () -> Unit = {},
    @StringRes val dialogTitle: Int? = null,
    @StringRes val dialogText: Int? = null,
    @DrawableRes val icon: Int? = null,
    @StringRes val iconDescription: Int? = null,
)
