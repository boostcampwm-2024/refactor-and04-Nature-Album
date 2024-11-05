package com.and04.naturealbum

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import com.and04.naturealbum.ui.theme.NatureAlbumTheme

@Composable
fun MyDialog(
    dialogData: DialogData,
) {
    if (dialogData.isShow){
        Dialog(
            onDismissRequest = dialogData.onDismiss
        ) {
            Column {
                Text(text = dialogData.text)
                Row {
                    Button(onClick = dialogData.onNegative) { Text(text = dialogData.negativeText) }
                    Button(onClick = dialogData.onPositive) { Text(text = dialogData.positiveText) }
                }
            }
        }
    }
}

data class DialogData(
    val text: String = "",
    val positiveText: String = "yes",
    val negativeText: String = "no",
    val onDismiss: () -> Unit = {},
    val onNegative: () -> Unit = {},
    val onPositive: () -> Unit = {},
    val isShow: Boolean = true
)

@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
fun MyDialogPreview() {
    NatureAlbumTheme {
        MyDialog(
            DialogData()
        )
    }
}