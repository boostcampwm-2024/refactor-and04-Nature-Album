package com.and04.naturealbum.utils

import android.content.Context
import android.content.res.Configuration
import androidx.compose.runtime.Composable
import com.and04.naturealbum.ui.component.HomeTopAppBar
import com.and04.naturealbum.ui.component.LandscapeHomeTopAppBar
import com.and04.naturealbum.ui.component.LandscapeMyPageTopAppBar
import com.and04.naturealbum.ui.component.LandscapeTopAppBar
import com.and04.naturealbum.ui.component.MyPageTopAppBar
import com.and04.naturealbum.ui.component.PortraitTopAppBar

const val COLUMN_COUNT_PORTRAIT = 2
const val COLUMN_COUNT_LANDSCAPE = 4

fun Context.isPortrait(): Boolean {
    return resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
}

fun Context.isLandscape(): Boolean {
    return resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
}

@Composable
fun Context.gridColumnCount(): Int {
    return if (isPortrait()) COLUMN_COUNT_PORTRAIT else COLUMN_COUNT_LANDSCAPE
}

@Composable
fun Context.GetTopBar(
    navigateToMyPage: () -> Unit,
    navigateToBackScreen: () -> Unit,
) {
    if (isPortrait()) {
        PortraitTopAppBar(
            navigateToMyPage = navigateToMyPage,
            navigateToBackScreen = navigateToBackScreen,
        )
    } else {
        LandscapeTopAppBar(
            navigateToMyPage = navigateToMyPage,
            navigateToBackScreen = navigateToBackScreen,
        )
    }
}

@Composable
fun Context.GetHomeTopBar(
    navigateToMyPage: () -> Unit,
){
    if (isPortrait()) {
        HomeTopAppBar(
            navigateToMyPage = navigateToMyPage,
        )
    } else {
        LandscapeHomeTopAppBar(
            onClick = navigateToMyPage
        )
    }
}

@Composable
fun Context.GetMyPageTopAppBar(
    navigateToBackScreen: () -> Unit,
){
    if (isPortrait()) {
        MyPageTopAppBar(
            navigateToBackScreen = navigateToBackScreen,
        )
    } else {
        LandscapeMyPageTopAppBar(
            navigateToBackScreen = navigateToBackScreen
        )
    }
}
