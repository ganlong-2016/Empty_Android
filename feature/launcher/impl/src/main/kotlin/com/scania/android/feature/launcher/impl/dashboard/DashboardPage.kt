package com.scania.android.feature.launcher.impl.dashboard

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.scania.android.feature.launcher.impl.DashboardState
import com.scania.android.feature.launcher.impl.dashboard.cards.CalendarCard
import com.scania.android.feature.launcher.impl.dashboard.cards.DriverCard
import com.scania.android.feature.launcher.impl.dashboard.cards.MediaCard
import com.scania.android.feature.launcher.impl.dashboard.cards.NavigationCard
import com.scania.android.feature.launcher.impl.dashboard.cards.NavigationResizeHandle
import com.scania.android.feature.launcher.impl.dashboard.cards.ShortcutsCard
import com.scania.android.feature.launcher.impl.dashboard.cards.VehicleCard
import com.scania.android.feature.launcher.impl.dashboard.cards.WeatherCard

/**
 * Launcher Dashboard 主页：
 *
 * - 左侧固定「导航卡」，宽度可通过边缘拖拽调整（1/3、1/2、2/3）；
 * - 右侧水平滚动多个卡片，通过 [DashboardSlot] 支持上下合并。
 */
@Composable
fun DashboardPage(
    state: DashboardState,
    slots: List<DashboardSlot>,
    navigationWidth: NavigationWidthLevel,
    onNavigationWidthDrag: (Float) -> Unit,
    onMergeInto: (fromId: String, toId: String, bottom: Boolean) -> Unit,
    onSplit: (slotId: String) -> Unit,
    onTogglePlay: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        val totalWidth = maxWidth
        val navSlot = slots.firstOrNull { it.top == DashboardCardType.Navigation }
        val otherSlots = slots.filter { it.top != DashboardCardType.Navigation }

        Row(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(modifier = Modifier.width(totalWidth * navigationWidth.fraction).fillMaxHeight()) {
                if (navSlot != null) {
                    NavigationCard(modifier = Modifier.fillMaxSize())
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .fillMaxHeight()
                        .width(20.dp)
                        .pointerInput(totalWidth) {
                            detectDragGestures(
                                onDrag = { _, dragAmount ->
                                    val dxPx = dragAmount.x
                                    val totalPx = totalWidth.toPx().coerceAtLeast(1f)
                                    val delta = dxPx / totalPx
                                    val target = (navigationWidth.fraction + delta)
                                    onNavigationWidthDrag(target)
                                },
                            )
                        },
                ) {
                    NavigationResizeHandle()
                }
            }

            LazyRow(
                state = rememberLazyListState(),
                modifier = Modifier.fillMaxHeight().weight(1f),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(otherSlots, key = { it.id }) { slot ->
                    DashboardSlotView(
                        slot = slot,
                        state = state,
                        allSlots = otherSlots,
                        onMergeInto = onMergeInto,
                        onSplit = onSplit,
                        onTogglePlay = onTogglePlay,
                        onNext = onNext,
                        onPrevious = onPrevious,
                    )
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
 * 卡片重排序 / 合并 / 拆分的手势入口通过 [onMergeInto] / [onSplit] 暴露给调用方，
 * 具体车机交互（长按、拖拽到边缘等）可以由接入方按 HMI 规范实现。
 */
@Composable
private fun DashboardSlotView(
    slot: DashboardSlot,
    state: DashboardState,
    allSlots: List<DashboardSlot>,
    onMergeInto: (fromId: String, toId: String, bottom: Boolean) -> Unit,
    onSplit: (slotId: String) -> Unit,
    onTogglePlay: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
) {
    @Suppress("UNUSED_PARAMETER") val neighbors = allSlots
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(360.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        CardByType(
            type = slot.top,
            state = state,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            onTogglePlay = onTogglePlay,
            onNext = onNext,
            onPrevious = onPrevious,
        )
        if (slot.isMerged) {
            CardByType(
                type = slot.bottom!!,
                state = state,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                onTogglePlay = onTogglePlay,
                onNext = onNext,
                onPrevious = onPrevious,
            )
        }
    }
}

@Composable
private fun CardByType(
    type: DashboardCardType,
    state: DashboardState,
    modifier: Modifier = Modifier,
    onTogglePlay: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
) {
    when (type) {
        DashboardCardType.Navigation -> NavigationCard(modifier = modifier)
        DashboardCardType.Media -> MediaCard(
            playback = state.media,
            onTogglePlay = onTogglePlay,
            onNext = onNext,
            onPrevious = onPrevious,
            modifier = modifier,
        )
        DashboardCardType.Weather -> WeatherCard(state.weather, modifier = modifier)
        DashboardCardType.Calendar -> CalendarCard(state.events, modifier = modifier)
        DashboardCardType.Driver -> DriverCard(state.driver, modifier = modifier)
        DashboardCardType.Vehicle -> VehicleCard(state.vehicle, modifier = modifier)
        DashboardCardType.Shortcuts -> ShortcutsCard(modifier = modifier)
    }
}
