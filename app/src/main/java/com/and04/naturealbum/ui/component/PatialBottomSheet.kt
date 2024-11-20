package com.and04.naturealbum.ui.component

import androidx.annotation.FloatRange
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.DecayAnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt


enum class BottomSheetState {
    Collapsed,
    HalfExpanded,
    Expanded
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CustomBottomSheetComponent(
    initialState: BottomSheetState = BottomSheetState.Collapsed,
    modifier: Modifier = Modifier,
    handleIcon: ImageVector = Icons.Default.DragHandle,
    handleHeight: Dp = 36.dp,
    showHandleCollapsed: Boolean = true,
    @FloatRange(0.0, 1.0) halfExpansionSize: Float = 0.5f,
    @FloatRange(0.0, 1.0) fullExpansionSize: Float = 0.95f,
    @FloatRange(0.0, 1.0) positionThreshold: Float = 0.5f,
    velocityThreshold: Dp = 100.dp,
    snapAnimationSpec: AnimationSpec<Float> = tween(),
    decayAnimationSpec: DecayAnimationSpec<Float> = rememberSplineBasedDecay(),
    content: @Composable () -> Unit
) {
    val density = LocalDensity.current
    val screenHeightPx = with(density) { LocalConfiguration.current.screenHeightDp.dp.toPx() }
    val handleHeightPx = if (showHandleCollapsed) with(density) { handleHeight.toPx() } else 0f
    var currentState by remember { mutableStateOf(initialState) }
    val state = remember {
        AnchoredDraggableState(
            initialValue = currentState,
            anchors = DraggableAnchors {
                BottomSheetState.Collapsed at (screenHeightPx - handleHeightPx)
                BottomSheetState.HalfExpanded at screenHeightPx * (1 - halfExpansionSize)
                BottomSheetState.Expanded at screenHeightPx * (1 - fullExpansionSize)
            },
            positionalThreshold = { distance: Float -> distance * positionThreshold },
            velocityThreshold = { with(density) { velocityThreshold.toPx() } },
            snapAnimationSpec = snapAnimationSpec,
            decayAnimationSpec = decayAnimationSpec
        )
    }

    LaunchedEffect(currentState) {
        state.animateTo(currentState)
    }
    LaunchedEffect(state.currentValue) {
        currentState = state.currentValue
    }

    Box(
        modifier = modifier
            .offset { IntOffset(0, state.requireOffset().roundToInt()) }
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        ElevatedCard(
            shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp),
        ) {
            Icon(
                imageVector = handleIcon,
                contentDescription = null,
                modifier = modifier
                    .fillMaxWidth()
                    .height(handleHeight)
                    .anchoredDraggable(
                        state = state,
                        orientation = Orientation.Vertical,
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        currentState = when (currentState) {
                            BottomSheetState.Collapsed -> BottomSheetState.HalfExpanded
                            BottomSheetState.HalfExpanded -> BottomSheetState.Collapsed
                            BottomSheetState.Expanded -> BottomSheetState.HalfExpanded
                        }
                    }
            )
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                content()
            }
        }
    }
}


@Preview
@Composable
fun BottomSheetScreenCollapsedPreView(
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.BottomCenter
    ) {
        CustomBottomSheetComponent(initialState = BottomSheetState.Collapsed) {
            Column {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .background(Color.Blue)
                )
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .background(Color.Black)
                )
            }
        }
    }
}

@Preview
@Composable
fun BottomSheetScreenHalfExpandedPreView(
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.BottomCenter
    ) {
        // BottomSheet
        CustomBottomSheetComponent(initialState = BottomSheetState.HalfExpanded) {
            Column {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .background(Color.Blue)
                )
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .background(Color.Black)
                )
            }
        }
    }
}

@Preview
@Composable
fun BottomSheetScreenExpandedPreView(
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.BottomCenter
    ) {
        // BottomSheet
        CustomBottomSheetComponent(initialState = BottomSheetState.Expanded) {
            Column {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .background(Color.Blue)
                )
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .background(Color.Black)
                )
            }
        }
    }
}