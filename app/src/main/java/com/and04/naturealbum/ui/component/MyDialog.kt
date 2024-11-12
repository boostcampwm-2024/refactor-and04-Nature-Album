package com.and04.naturealbum.ui.component

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.and04.naturealbum.R


@Composable
fun MyDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    @StringRes dialogTitle: Int? = null,
    @StringRes dialogText: Int? = null,
    @DrawableRes icon: Int? = null,
    @StringRes iconDescription: Int? = null,
) {
    AlertDialog(
        icon = {
            if (icon != null && iconDescription != null) {
                Icon(
                    painter = painterResource(icon),
                    contentDescription = stringResource(iconDescription)
                )
            }
        },
        title = {
            dialogTitle?.let { title -> Text(text = stringResource(title)) }
        },
        text = {
            dialogText?.let { text -> Text(text = stringResource(text)) }
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                }
            ) {
                Text(stringResource(R.string.dialog_confirm_button))
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text(stringResource(R.string.dialog_dismiss_button))
            }
        }
    )
}
