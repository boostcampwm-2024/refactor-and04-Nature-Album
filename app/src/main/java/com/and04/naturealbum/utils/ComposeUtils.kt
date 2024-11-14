package com.and04.naturealbum.utils

import android.content.Context
import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.and04.naturealbum.ui.component.LandscapeTopAppBar
import com.and04.naturealbum.ui.component.PortraitTopAppBar

const val COLUMN_COUNT_PORTRAIT = 2
const val COLUMN_COUNT_LANDSCAPE = 2

fun Context.isPortrait(): Boolean {
    return resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
}

fun Context.isLandscape(): Boolean {
    return resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
}

@Composable
fun Context.gridColumnCount(): Int {
    return if (LocalContext.current.isPortrait()) COLUMN_COUNT_PORTRAIT else COLUMN_COUNT_LANDSCAPE
}

@Composable
fun Context.GetTopbar(onNavigateToMyPage: () -> Unit) {
    if (LocalContext.current.isPortrait()) {
        PortraitTopAppBar { onNavigateToMyPage() }
    } else {
        LandscapeTopAppBar { onNavigateToMyPage() }
    }
}
