package com.and04.naturealbum.ui.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.and04.naturealbum.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTopAppBar(onClick: () -> Unit = { }) {
    TopAppBar(
        title = {
            Text(stringResource(R.string.app_name))
        },
        actions = {
            IconButton(onClick = { onClick() }) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = null /* TODO */
                )
            }
        }
    )
}

@Composable
fun LandscapeTopAppBar(onClick: () -> Unit = { }) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 30.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(R.string.app_name),
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Start,
        )
        Box(
            modifier = Modifier.weight(4f)
        ) {
            IconButton(
                onClick = { onClick() },
            ) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = null, /* TODO */
                )
            }
        }
    }
}
