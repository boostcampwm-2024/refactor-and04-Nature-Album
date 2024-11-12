package com.and04.naturealbum.ui.component

import android.content.Context
import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.and04.naturealbum.R
import com.and04.naturealbum.utils.parseDrawableSvgFile
import com.and04.naturealbum.utils.parseSvgFile


private fun Path.scale(scaleX: Float, scaleY: Float) {
    val matrix = Matrix().apply {
        reset()
        scale(scaleX, scaleY)
    }
    transform(matrix)
}


class CustomShape(
    private val path: Path,
    private val viewportWidth: Float,
    private val viewportHeight: Float
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {

        val scaleX = size.width / viewportWidth
        val scaleY = size.height / viewportHeight
        val scale = minOf(scaleX, scaleY)


        val scaledPath = Path().apply {
            addPath(path)
            scale(scale, scale)
        }
        return Outline.Generic(scaledPath)
    }
}


@Composable
fun HomeMapButton(
    modifier: Modifier = Modifier,
    text: String,
    textColor: Color,
    onClick: () -> Unit
) {
    val pathData =
        "M341.33,88.24C356.06,80.33 366.08,64.79 366.08,46.9C366.08,21 345.08,0 319.18,0C303.29,0 289.24,7.91 280.76,20H10C4.48,20 0,24.48 0,30V197C0,202.52 4.48,207 10,207H210.59L204.45,218.71H216.76L213.81,224.33H356.96L373,91.36L362.82,92.5L363.64,85.74L341.33,88.24Z"
    val path = PathParser().parsePathString(pathData).toPath()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(373f / 225f)
            .clip(CustomShape(path, 373f, 225f))
            .clickable { onClick() }
    ) {
        Image(
            painter = painterResource(id = R.drawable.btn_home_menu_map_background),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Text(
            text = text,
            modifier = Modifier
                .padding(start = 16.dp, top = 36.dp),
            color = textColor
        )
    }
}


@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_NO)
@Composable
fun HomeMapButtonPreview() {
    HomeMapButton(modifier = Modifier.background(Color.Blue), "테스트", Color.Red) {}
}


@Composable
fun ClippingButton(
    modifier: Modifier = Modifier,
    text: String,
    textColor: Color,
    imageResId: Int,
    pathData: String,
    viewportWidth: Float,
    viewportHeight: Float,
    onClick: () -> Unit
) {

    val path = PathParser().parsePathString(pathData).toPath()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(viewportWidth / viewportHeight)
            .clip(CustomShape(path, viewportWidth, viewportHeight))
            .clickable { onClick() }
    ) {
        Image(
            painter = painterResource(id = imageResId),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize()
        )

        Text(
            text = text,
            modifier = Modifier
                .padding(start = 16.dp, top = 36.dp),
            color = textColor
        )
    }
}

@Composable
fun ClippingButtonWithFile(
    modifier: Modifier = Modifier,
    context: Context,
    isFromAssets: Boolean = true,
    fileNameOrResId: Any,
    text: String,
    textColor: Color,
    imageResId: Int,
    onClick: () -> Unit
) {
    var parsedPathData by remember { mutableStateOf<String?>(null) }
    var parsedViewportWidth by remember { mutableFloatStateOf(1f) }
    var parsedViewportHeight by remember { mutableFloatStateOf(1f) }

    LaunchedEffect(fileNameOrResId) {
        val svgData = if (isFromAssets && fileNameOrResId is String) {
            parseSvgFile(context, fileNameOrResId)
        } else if (!isFromAssets && fileNameOrResId is Int) {
            parseDrawableSvgFile(context, fileNameOrResId)
        } else null

        svgData?.let { parsedSvgData ->
            parsedPathData = parsedSvgData.pathData
            parsedViewportWidth = parsedSvgData.viewportWidth
            parsedViewportHeight = parsedSvgData.viewportHeight
        }
    }

    parsedPathData?.let { validPathData ->
        ClippingButton(
            modifier = modifier,
            text = text,
            textColor = textColor,
            imageResId = imageResId,
            pathData = validPathData,
            viewportWidth = parsedViewportWidth,
            viewportHeight = parsedViewportHeight,
            onClick = onClick
        )
    } ?: run {
        Text(text = stringResource(R.string.text_loading))
    }
}


@Preview(
    showBackground = true,
    uiMode = UI_MODE_NIGHT_YES,
    name = "ClippingButtonWithFile Preview (Dark Mode)"
)
@Preview(
    showBackground = true,
    uiMode = UI_MODE_NIGHT_NO,
    name = "ClippingButtonWithFile Preview (Light Mode)"
)
@Composable
fun ClippingButtonWithFilePreview() {
    val context = LocalContext.current
    ClippingButtonWithFile(
        context = context,
        isFromAssets = true,
        fileNameOrResId = stringResource(R.string.btn_home_menu_map_background_outline_svg),
        text = stringResource(R.string.home_navigate_to_map),
        textColor = Color.Black,
        imageResId = R.drawable.btn_home_menu_map_background,
        onClick = { /* TODO */ }
    )
}

