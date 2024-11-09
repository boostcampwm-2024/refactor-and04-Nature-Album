package com.and04.naturealbum.ui.component

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@Composable
fun RoundedShapeButton(@StringRes text: Int, modifier: Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(10)
    ) {
        Text(
            text = stringResource(text),
            modifier = Modifier
                .padding(top = 24.dp)
                .align(Alignment.Top)
        )
    }
}
