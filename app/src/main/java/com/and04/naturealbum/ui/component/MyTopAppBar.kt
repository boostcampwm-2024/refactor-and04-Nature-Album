package com.and04.naturealbum.ui.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.and04.naturealbum.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTopAppBar(navigationIcon: @Composable () -> Unit = { }, onClick: () -> Unit = { }) {
    TopAppBar(
        title = {
            Text(stringResource(R.string.app_name))
        },
        navigationIcon = {
            navigationIcon()
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
