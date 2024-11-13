package com.and04.naturealbum.ui.home

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.and04.naturealbum.R
import com.and04.naturealbum.ui.LocationHandler
import com.and04.naturealbum.ui.component.NavigationImageButton

const val MAP_BUTTON_BACKGROUND_OUTLINE_SVG = "btn_home_menu_map_background_outline.svg"

@Composable
fun HomeScreen(
    locationHandler: LocationHandler,
    takePicture: () -> Unit,
    onNavigateToAlbum: () -> Unit,
) {
    val configuration = LocalConfiguration.current
    val isPortarit = configuration.orientation == Configuration.ORIENTATION_PORTRAIT

    if (isPortarit) {
        HomeScreenPortrait(
            locationHandler = locationHandler,
            takePicture = takePicture,
            onNavigateToAlbum = onNavigateToAlbum,
        )
    } else {
        HomeScreenLandscape(
            locationHandler = locationHandler,
            takePicture = takePicture,
            onNavigateToAlbum = onNavigateToAlbum,
        )
    }
}

@Composable
fun MainBackground(modifier: Modifier) {
    Image(
        modifier = modifier,
        contentScale = ContentScale.FillBounds,
        imageVector = ImageVector.vectorResource(id = R.drawable.drawable_home_main_background),
        contentDescription = null
    )
}

@Composable
fun NavigateContent(
    modifier: Modifier = Modifier,
    permissionHandler: PermissionHandler,
    onNavigateToAlbum: () -> Unit,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        val contentModifier = Modifier
            .weight(1f)
            .fillMaxWidth()
        NavigationImageButton(
            text = stringResource(R.string.home_navigate_to_album),
            modifier = contentModifier,
            textColor = Color.White,
            imageVector = ImageVector.vectorResource(id = R.drawable.btn_album_background)
        ) { onNavigateToAlbum() }

        NavigationImageButton(
            text = stringResource(R.string.home_navigate_to_camera),
            modifier = contentModifier,
            textColor = Color.Black,
            imageVector = ImageVector.vectorResource(id = R.drawable.btn_camera_background)
        ) { permissionHandler.onClickCamera() }
    }
}


