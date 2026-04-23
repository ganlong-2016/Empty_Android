package com.scania.android.feature.launcher.impl

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.scania.android.feature.launcher.impl.dashboard.DashboardPage
import com.scania.android.feature.launcher.impl.splash.SplashPage
import kotlinx.coroutines.launch

/**
 * Launcher 顶层入口：
 *
 * 两页手动切换（不使用 HorizontalPager，以便 3D 页保留全屏手势控制）：
 *   - 第 0 页：[SplashPage]（3D 模型全屏），仅在右侧窄条区域可向左滑动进入主页；
 *   - 第 1 页：[DashboardPage]（卡片主页），左侧半透明条可点击/右滑返回 3D 页。
 *
 * 使用 [Animatable] 统一管理页面偏移量，拖拽时 snapTo 当前值，
 * 松手后 animateTo 目标值，保证动画始终从手指释放位置开始，不会回弹闪烁。
 */
@Composable
fun LauncherScreen(
    modifier: Modifier = Modifier,
    viewModel: LauncherViewModel = hiltViewModel(),
) {
    val slots by viewModel.slots.collectAsStateWithLifecycle()
    val navWidth by viewModel.navigationWidthFraction.collectAsStateWithLifecycle()

    var currentPage by remember { mutableIntStateOf(0) }
    val pageOffset = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        val totalWidthPx = constraints.maxWidth.toFloat()
        val offset = pageOffset.value

        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { translationX = offset * totalWidthPx },
        ) {
            SplashPage()
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { translationX = (1f + offset) * totalWidthPx },
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
                                val committed = pageOffset.value < -0.3f
                                scope.launch {
                                    if (committed) {
                                        pageOffset.animateTo(-1f, tween(250))
                                        currentPage = 1
                                    } else {
                                        pageOffset.animateTo(0f, tween(250))
                                    }
                                }
                            },
                            onDragCancel = {
                                scope.launch { pageOffset.animateTo(0f, tween(200)) }
                            },
                            onHorizontalDrag = { change, dragAmount ->
                                change.consume()
                                val newValue = (pageOffset.value + dragAmount / widthPx)
                                    .coerceIn(-1f, 0f)
                                scope.launch { pageOffset.snapTo(newValue) }
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
                    .width(48.dp)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.25f),
                                Color.Transparent,
                            ),
                        ),
                    )
                    .clickable {
                        scope.launch {
                            pageOffset.animateTo(0f, tween(300))
                            currentPage = 0
                        }
                    }
                    .pointerInput(Unit) {
                        val widthPx = totalWidthPx.coerceAtLeast(1f)
                        detectHorizontalDragGestures(
                            onDragEnd = {
                                val committed = pageOffset.value > -0.85f
                                scope.launch {
                                    if (committed) {
                                        pageOffset.animateTo(0f, tween(250))
                                        currentPage = 0
                                    } else {
                                        pageOffset.animateTo(-1f, tween(250))
                                    }
                                }
                            },
                            onDragCancel = {
                                scope.launch { pageOffset.animateTo(-1f, tween(200)) }
                            },
                            onHorizontalDrag = { change, dragAmount ->
                                change.consume()
                                val newValue = (pageOffset.value + dragAmount / widthPx)
                                    .coerceIn(-1f, 0f)
                                scope.launch { pageOffset.snapTo(newValue) }
                            },
                        )
                    },
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "返回3D页面",
                        tint = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.size(28.dp),
                    )
                    Text(
                        text = "3D",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.7f),
                    )
                }
            }
        }
    }
}
