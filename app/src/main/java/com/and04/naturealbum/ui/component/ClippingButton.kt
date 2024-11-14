package com.and04.naturealbum.ui.component

import android.content.Context
import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
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

private class SvgOutlineShape(
    private val path: Path,
    private val viewportWidth: Float,
    private val viewportHeight: Float,
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density,
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
fun ClippingButtonWithFile(
    context: Context,
    isFromAssets: Boolean = true,
    fileNameOrResId: Any,
    text: String,
    textColor: Color,
    imageResId: Int,
    onClick: () -> Unit,
) {
    var parsedPathData by remember { mutableStateOf("") }
    var parsedViewportWidth by remember { mutableFloatStateOf(1f) }
    var parsedViewportHeight by remember { mutableFloatStateOf(1f) }

    LaunchedEffect(fileNameOrResId) {
        val svgData = when {
            isFromAssets && fileNameOrResId is String -> parseSvgFile(context, fileNameOrResId)
            !isFromAssets && fileNameOrResId is Int -> parseDrawableSvgFile(
                context,
                fileNameOrResId
            )

            else -> null
        }
        if (svgData == null) return@LaunchedEffect

        parsedPathData = svgData.pathData
        parsedViewportWidth = svgData.viewportWidth
        parsedViewportHeight = svgData.viewportHeight

    }

    Box(
        modifier = Modifier
    ) {
        val path = PathParser().parsePathString(parsedPathData).toPath()

        Surface(
            modifier = Modifier
                .aspectRatio(parsedViewportWidth / parsedViewportHeight)
                .clip(SvgOutlineShape(path, parsedViewportWidth, parsedViewportHeight))
                .clickable { onClick() }
        ) {

            Image(
                painter = painterResource(id = imageResId),
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .fillMaxSize()
            )

            Text(
                text = text,
                modifier = Modifier
                    .padding(start = 16.dp, top = 36.dp),
                color = textColor
            )
        }
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
private fun ClippingButtonWithFilePreview() {
    val context = LocalContext.current
    ClippingButtonWithFile(
        context = context,
        isFromAssets = true,
        fileNameOrResId = "btn_home_menu_map_background_outline.svg",
        text = stringResource(R.string.home_navigate_to_map),
        textColor = Color.Black,
        imageResId = R.drawable.btn_home_menu_map_background,
        onClick = { /* TODO */ }
    )
}

