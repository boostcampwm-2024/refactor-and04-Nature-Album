package com.and04.naturealbum.ui.home

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.and04.naturealbum.R
import com.and04.naturealbum.ui.component.ClippingButtonWithFile
import com.and04.naturealbum.ui.component.PortraitTopAppBar

@Composable
fun HomeScreenPortrait(
    context: Context,
    permissionHandler: PermissionHandler,
    onNavigateToAlbum: () -> Unit,
    onNavigateToMyPage: () -> Unit,
    onNavigateToMap: () -> Unit,
) {
    Scaffold(topBar = { PortraitTopAppBar { onNavigateToMyPage() } }) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
        ) {
            MainBackground(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            )

            ClippingButtonWithFile(
                context = context,
                isFromAssets = true,
                fileNameOrResId = MAP_BUTTON_BACKGROUND_OUTLINE_SVG,
                text = stringResource(R.string.home_navigate_to_map),
                textColor = Color.Black,
                imageResId = R.drawable.btn_home_menu_map_background,
                onClick = onNavigateToMap
            )

            NavigateContent(
                modifier = Modifier
                    .weight(1.17f)
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                permissionHandler = permissionHandler,
                onNavigateToAlbum = onNavigateToAlbum
            )
        }
    }
}
