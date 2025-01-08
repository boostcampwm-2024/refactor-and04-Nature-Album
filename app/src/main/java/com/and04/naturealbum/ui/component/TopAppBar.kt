package com.and04.naturealbum.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.and04.naturealbum.R
import com.and04.naturealbum.ui.utils.UserManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PortraitTopAppBar(
    navigateToBackScreen: () -> Unit,
    navigateToMyPage: () -> Unit,
) {
    TopAppBar(
        title = {
            Text(stringResource(R.string.app_name))
        },
        navigationIcon = {
            IconButton(onClick = { navigateToBackScreen() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.my_page_arrow_back_icon_content_description)
                )
            }
        },
        actions = {
            MyPageNavigationIconButton(navigateToMyPage = navigateToMyPage)
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
        )
    )
}

@Composable
fun LandscapeTopAppBar(
    navigateToBackScreen: () -> Unit,
    navigateToMyPage: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 30.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(
            onClick = { navigateToBackScreen() },
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.top_bar_navigate_to_my_page)
            )
        }

        Text(
            text = stringResource(R.string.app_name),
            textAlign = TextAlign.Start,
        )
        Box {
            MyPageNavigationIconButton(navigateToMyPage = navigateToMyPage)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopAppBar(
    navigateToMyPage: () -> Unit,
) {
    TopAppBar(
        title = {
            Text(stringResource(R.string.app_name))
        },
        actions = {
            MyPageNavigationIconButton(navigateToMyPage = navigateToMyPage)
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
        )
    )
}

@Composable
fun LandscapeHomeTopAppBar(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 30.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(R.string.app_name),
            textAlign = TextAlign.Start,
        )
        Box {
            MyPageNavigationIconButton(navigateToMyPage = onClick)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyPageTopAppBar(
    navigateToBackScreen: () -> Unit,
) {
    TopAppBar(
        title = {
            Text(stringResource(R.string.app_name))
        },
        navigationIcon = {
            IconButton(onClick = { navigateToBackScreen() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.my_page_arrow_back_icon_content_description)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
        )
    )
}

@Composable
fun LandscapeMyPageTopAppBar(
    navigateToBackScreen: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 30.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box {
            IconButton(
                onClick = { navigateToBackScreen() },
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.top_bar_navigate_to_my_page)
                )
            }
        }

        Text(
            text = stringResource(R.string.app_name),
            textAlign = TextAlign.Start,
        )
    }
}

@Composable
fun MyPageNavigationIconButton(navigateToMyPage: () -> Unit) {
    IconButton(onClick = { navigateToMyPage() }) {
        UserManager.getUserProfile()?.let { uri ->
            AsyncImage(
                model = uri,
                contentDescription = stringResource(R.string.top_bar_navigate_to_my_page),
                modifier = Modifier.fillMaxSize()
            )
        } ?: Icon(
            imageVector = Icons.Default.AccountCircle,
            contentDescription = stringResource(R.string.top_bar_navigate_to_my_page)
        )
    }
}
