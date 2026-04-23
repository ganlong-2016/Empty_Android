package com.scania.android.feature.launcher.impl

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.scania.android.feature.launcher.impl.dashboard.DashboardPage
import com.scania.android.feature.launcher.impl.splash.SplashPage

/**
 * Launcher 顶层入口：
 *
 * 两页手动切换（不使用 HorizontalPager，以便 3D 页保留全屏手势控制）：
 *   - 第 0 页：[SplashPage]（3D 模型全屏），仅在右侧窄条区域可向左滑动进入主页；
 *   - 第 1 页：[DashboardPage]（卡片主页）。
 */
@Composable
fun LauncherScreen(
    modifier: Modifier = Modifier,
    viewModel: LauncherViewModel = hiltViewModel(),
) {
    val slots by viewModel.slots.collectAsStateWithLifecycle()
    val navWidth by viewModel.navigationWidthFraction.collectAsStateWithLifecycle()

    var currentPage by remember { mutableIntStateOf(0) }
    var dragProgress by remember { mutableFloatStateOf(0f) }

    val animatedOffset by animateFloatAsState(
        targetValue = if (currentPage == 1) -1f else 0f,
        animationSpec = tween(durationMillis = 350),
        label = "pageOffset",
    )

    val effectiveOffset = animatedOffset + dragProgress

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        val totalWidthPx = constraints.maxWidth.toFloat()

        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    translationX = effectiveOffset * totalWidthPx
                },
        ) {
            SplashPage()
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    translationX = (1f + effectiveOffset) * totalWidthPx
                },
        ) {
            DashboardPage(
                slots = slots,
                navigationWidthFraction = navWidth,
                onNavigationWidthDrag = viewModel::dragNavigationWidth,
                onNavigationWidthDragEnd = viewModel::snapNavigationWidth,
                onReorderSlot = viewModel::reorderSlot,
                onMergeSlot = viewModel::mergeInto,
                onSplitSlot = viewModel::splitSlot,
            )
        }

        if (currentPage == 0) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .fillMaxHeight()
                    .width(80.dp)
                    .pointerInput(Unit) {
                        val widthPx = totalWidthPx.coerceAtLeast(1f)
                        detectHorizontalDragGestures(
                            onDragEnd = {
                                if (dragProgress < -0.3f) {
                                    currentPage = 1
                                }
                                dragProgress = 0f
                            },
                            onDragCancel = {
                                dragProgress = 0f
                            },
                            onHorizontalDrag = { change, dragAmount ->
                                change.consume()
                                dragProgress =
                                    (dragProgress + dragAmount / widthPx).coerceIn(-1f, 0f)
                            },
                        )
                    },
            )
        }

        if (currentPage == 1) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .fillMaxHeight()
                    .width(40.dp)
                    .pointerInput(Unit) {
                        val widthPx = totalWidthPx.coerceAtLeast(1f)
                        detectHorizontalDragGestures(
                            onDragEnd = {
                                if (dragProgress > 0.3f) {
                                    currentPage = 0
                                }
                                dragProgress = 0f
                            },
                            onDragCancel = {
                                dragProgress = 0f
                            },
                            onHorizontalDrag = { change, dragAmount ->
                                change.consume()
                                dragProgress =
                                    (dragProgress + dragAmount / widthPx).coerceIn(0f, 1f)
                            },
                        )
                    },
            )
        }
    }
}
