package com.scania.android.feature.launcher.impl.dashboard

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.scania.android.feature.launcher.impl.dashboard.cards.CardHost
import com.scania.android.feature.launcher.impl.dashboard.cards.NavigationResizeHandle

/**
 * Launcher Dashboard 主页：
 *
 * - 左侧固定「导航卡」，通过右边缘拖拽手柄实时改变宽度（`navigationWidthFraction`），
 *   拖拽结束时触发 [onNavigationWidthDragEnd]，由 ViewModel snap 到最近一档（1/3、1/2、2/3）；
 * - 右侧水平滚动多个槽位，每个槽位通过 [CardHost] 自行渲染（每张卡片拥有自己的 ViewModel）。
 *
 * 本函数只关心 **布局**；卡片的数据/UiState 完全由卡片自己管理。
 */
@Composable
fun DashboardPage(
    slots: List<DashboardSlot>,
    navigationWidthFraction: Float,
    onNavigationWidthDrag: (deltaFraction: Float) -> Unit,
    onNavigationWidthDragEnd: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        val totalWidth = maxWidth
        val otherSlots = slots.filter { it.top != DashboardCardType.Navigation }
        val hasNavigationCard = slots.any { it.top == DashboardCardType.Navigation }

        val animatedFraction by animateFloatAsState(
            targetValue = navigationWidthFraction,
            label = "navWidthFraction",
        )

        Row(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            if (hasNavigationCard) {
                Box(
                    modifier = Modifier
                        .width(totalWidth * animatedFraction)
                        .fillMaxHeight(),
                ) {
                    CardHost(
                        type = DashboardCardType.Navigation,
                        modifier = Modifier.fillMaxSize(),
                    )

                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .fillMaxHeight()
                            .width(24.dp)
                            .pointerInput(totalWidth) {
                                val totalPx = totalWidth.toPx().coerceAtLeast(1f)
                                detectDragGestures(
                                    onDrag = { change, dragAmount ->
                                        change.consume()
                                        onNavigationWidthDrag(dragAmount.x / totalPx)
                                    },
                                    onDragEnd = { onNavigationWidthDragEnd() },
                                    onDragCancel = { onNavigationWidthDragEnd() },
                                )
                            },
                    ) {
                        NavigationResizeHandle()
                    }
                }
            }

            LazyRow(
                state = rememberLazyListState(),
                modifier = Modifier.fillMaxHeight().weight(1f),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(otherSlots, key = { it.id }) { slot ->
                    DashboardSlotView(slot = slot)
                }
            }
        }
    }
}

/**
 * 单个卡片槽位的展示。
 *
 * - 非合并态时：单张卡占满整个槽；
 * - 合并态（[DashboardSlot.isMerged]）时：上下各占一半，模拟「两张卡合并到同一列」。
 *
 * 每张卡片都通过 [CardHost] 自己拿 ViewModel、自己订阅数据，所以这里不需要传任何 state。
 */
@Composable
private fun DashboardSlotView(
    slot: DashboardSlot,
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(360.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        CardHost(
            type = slot.top,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
        )
        if (slot.isMerged) {
            CardHost(
                type = slot.bottom!!,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            )
        }
    }
}
