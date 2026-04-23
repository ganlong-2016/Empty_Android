package com.scania.android.feature.launcher.impl.dashboard

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CallSplit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.scania.android.feature.launcher.impl.dashboard.cards.CardHost
import com.scania.android.feature.launcher.impl.dashboard.cards.NavigationResizeHandle
import kotlin.math.roundToInt

private data class SlotLayoutInfo(
    val slotId: String,
    val position: Offset,
    val size: IntSize,
)

/**
 * Launcher Dashboard 主页：
 *
 * - 左侧固定「导航卡」，通过右边缘拖拽手柄实时改变宽度；
 * - 右侧水平滚动多个槽位，支持长按拖拽以：
 *   1. 交换卡片位置（拖到另一个卡片上释放）；
 *   2. 合并卡片（拖到另一个卡片的下半区域释放，两张卡上下叠放在一个槽位里）；
 *   3. 拆分卡片（合并态的卡片上方显示拆分按钮，点击即可恢复成两个独立卡片）。
 */
@Composable
fun DashboardPage(
    slots: List<DashboardSlot>,
    navigationWidthFraction: Float,
    onNavigationWidthDrag: (deltaFraction: Float) -> Unit,
    onNavigationWidthDragEnd: () -> Unit,
    onReorderSlot: (fromIndex: Int, toIndex: Int) -> Unit,
    onMergeSlot: (fromSlotId: String, toSlotId: String, targetIsBottom: Boolean) -> Unit,
    onSplitSlot: (slotId: String) -> Unit,
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

        var draggedSlotId by remember { mutableStateOf<String?>(null) }
        var dragOffset by remember { mutableStateOf(IntOffset.Zero) }
        var draggedSlotIndex by remember { mutableIntStateOf(-1) }

        val slotLayouts = remember { mutableMapOf<String, SlotLayoutInfo>() }

        var dropTargetSlotId by remember { mutableStateOf<String?>(null) }
        var dropTargetIsBottom by remember { mutableStateOf(false) }

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
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
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                itemsIndexed(otherSlots, key = { _, slot -> slot.id }) { index, slot ->
                    val isDragging = draggedSlotId == slot.id
                    val isDropTarget = dropTargetSlotId == slot.id && !isDragging

                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(360.dp)
                            .zIndex(if (isDragging) 10f else 0f)
                            .onGloballyPositioned { coordinates ->
                                slotLayouts[slot.id] = SlotLayoutInfo(
                                    slotId = slot.id,
                                    position = coordinates.positionInRoot(),
                                    size = coordinates.size,
                                )
                            }
                            .then(
                                if (isDragging) {
                                    Modifier
                                        .offset { dragOffset }
                                        .graphicsLayer {
                                            scaleX = 1.05f
                                            scaleY = 1.05f
                                            alpha = 0.9f
                                            shadowElevation = 16f
                                        }
                                } else {
                                    Modifier
                                }
                            )
                            .pointerInput(slot.id, index) {
                                detectDragGesturesAfterLongPress(
                                    onDragStart = {
                                        draggedSlotId = slot.id
                                        draggedSlotIndex = index
                                        dragOffset = IntOffset.Zero
                                    },
                                    onDrag = { change, dragAmount ->
                                        change.consume()
                                        dragOffset = IntOffset(
                                            x = dragOffset.x + dragAmount.x.roundToInt(),
                                            y = dragOffset.y + dragAmount.y.roundToInt(),
                                        )

                                        val myLayout = slotLayouts[slot.id] ?: return@detectDragGesturesAfterLongPress
                                        val dragCenterX = myLayout.position.x + myLayout.size.width / 2f + dragOffset.x
                                        val dragCenterY = myLayout.position.y + myLayout.size.height / 2f + dragOffset.y

                                        var foundTarget: String? = null
                                        var isBottom = false

                                        for ((targetId, targetLayout) in slotLayouts) {
                                            if (targetId == slot.id) continue
                                            val targetSlot = otherSlots.firstOrNull { it.id == targetId } ?: continue

                                            val inX = dragCenterX in targetLayout.position.x..(targetLayout.position.x + targetLayout.size.width)
                                            val inY = dragCenterY in targetLayout.position.y..(targetLayout.position.y + targetLayout.size.height)

                                            if (inX && inY) {
                                                foundTarget = targetId
                                                val midY = targetLayout.position.y + targetLayout.size.height / 2f
                                                isBottom = dragCenterY > midY && !targetSlot.isMerged
                                                break
                                            }
                                        }

                                        dropTargetSlotId = foundTarget
                                        dropTargetIsBottom = isBottom
                                    },
                                    onDragEnd = {
                                        val targetId = dropTargetSlotId
                                        val fromId = draggedSlotId

                                        if (fromId != null && targetId != null) {
                                            if (dropTargetIsBottom) {
                                                onMergeSlot(fromId, targetId, true)
                                            } else {
                                                val fromIdx = otherSlots.indexOfFirst { it.id == fromId }
                                                val toIdx = otherSlots.indexOfFirst { it.id == targetId }
                                                if (fromIdx >= 0 && toIdx >= 0) {
                                                    val realFromIdx = slots.indexOfFirst { it.id == fromId }
                                                    val realToIdx = slots.indexOfFirst { it.id == targetId }
                                                    if (realFromIdx >= 0 && realToIdx >= 0) {
                                                        onReorderSlot(realFromIdx, realToIdx)
                                                    }
                                                }
                                            }
                                        }

                                        draggedSlotId = null
                                        dragOffset = IntOffset.Zero
                                        draggedSlotIndex = -1
                                        dropTargetSlotId = null
                                        dropTargetIsBottom = false
                                    },
                                    onDragCancel = {
                                        draggedSlotId = null
                                        dragOffset = IntOffset.Zero
                                        draggedSlotIndex = -1
                                        dropTargetSlotId = null
                                        dropTargetIsBottom = false
                                    },
                                )
                            },
                    ) {
                        DashboardSlotView(
                            slot = slot,
                            isDropTarget = isDropTarget,
                            dropTargetIsBottom = dropTargetIsBottom,
                            onSplit = { onSplitSlot(slot.id) },
                        )
                    }
                }
            }
        }
    }
}

/**
 * 单个卡片槽位的展示。
 *
 * - 非合并态时：单张卡占满整个槽；
 * - 合并态时：上下各占一半，并在顶部显示拆分按钮。
 * - 当作为拖拽目标时，显示高亮边框提示。
 */
@Composable
private fun DashboardSlotView(
    slot: DashboardSlot,
    isDropTarget: Boolean = false,
    dropTargetIsBottom: Boolean = false,
    onSplit: () -> Unit = {},
) {
    val borderColor = MaterialTheme.colorScheme.primary

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .width(360.dp)
                .then(
                    if (isDropTarget) {
                        Modifier.border(
                            width = 3.dp,
                            color = borderColor,
                            shape = RoundedCornerShape(16.dp),
                        )
                    } else {
                        Modifier
                    }
                ),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            ) {
                CardHost(
                    type = slot.top,
                    modifier = Modifier.fillMaxSize(),
                )

                if (isDropTarget && !dropTargetIsBottom) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                color = borderColor.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(16.dp),
                            ),
                    )
                }
            }

            if (slot.isMerged) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                ) {
                    CardHost(
                        type = slot.bottom!!,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            } else if (isDropTarget && dropTargetIsBottom) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(
                            color = borderColor.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(16.dp),
                        )
                        .border(
                            width = 2.dp,
                            color = borderColor.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(16.dp),
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "放置到此处合并",
                        style = MaterialTheme.typography.bodyMedium,
                        color = borderColor,
                    )
                }
            }
        }

        if (slot.isMerged) {
            IconButton(
                onClick = onSplit,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.CallSplit,
                    contentDescription = "拆分卡片",
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}
