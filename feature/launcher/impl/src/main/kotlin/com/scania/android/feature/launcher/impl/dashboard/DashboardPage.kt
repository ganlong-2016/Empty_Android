package com.scania.android.feature.launcher.impl.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.scania.android.feature.launcher.impl.dashboard.cards.CardHost
import com.scania.android.feature.launcher.impl.dashboard.cards.NavigationResizeHandle
import kotlin.math.abs
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
 *   3. 拆分卡片（合并态卡片中间的拖拽手柄可上下拉开来拆分）。
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
                                                val realFromIdx = slots.indexOfFirst { it.id == fromId }
                                                val realToIdx = slots.indexOfFirst { it.id == targetId }
                                                if (realFromIdx >= 0 && realToIdx >= 0) {
                                                    onReorderSlot(realFromIdx, realToIdx)
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

private const val SplitThresholdDp = 60f

/**
 * 单个卡片槽位的展示。
 *
 * - 非合并态时：单张卡占满整个槽；
 * - 合并态时：上下各占一半，中间显示可拖拽的分隔手柄；
 *   上下拖拽手柄超过阈值即触发拆分，两张卡恢复为独立槽位。
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
    val density = LocalDensity.current

    var splitDragPx by remember { mutableFloatStateOf(0f) }
    var isSplitDragging by remember { mutableStateOf(false) }

    val splitThresholdPx = with(density) { SplitThresholdDp.dp.toPx() }
    val splitProgress = if (splitThresholdPx > 0f) {
        (abs(splitDragPx) / splitThresholdPx).coerceIn(0f, 1f)
    } else 0f

    val animatedSplitGap by animateDpAsState(
        targetValue = if (isSplitDragging) {
            with(density) { abs(splitDragPx).toDp() }
        } else {
            0.dp
        },
        animationSpec = if (isSplitDragging) {
            spring(stiffness = 800f)
        } else {
            spring(stiffness = 400f, dampingRatio = 0.7f)
        },
        label = "splitGap",
    )

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
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .then(
                        if (slot.isMerged) {
                            Modifier.graphicsLayer {
                                translationY = -(animatedSplitGap / 2).toPx()
                            }
                        } else Modifier
                    ),
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
                SplitDragHandle(
                    splitProgress = splitProgress,
                    onDragStart = { isSplitDragging = true },
                    onDrag = { deltaY -> splitDragPx += deltaY },
                    onDragEnd = {
                        if (abs(splitDragPx) >= splitThresholdPx) {
                            onSplit()
                        }
                        splitDragPx = 0f
                        isSplitDragging = false
                    },
                    onDragCancel = {
                        splitDragPx = 0f
                        isSplitDragging = false
                    },
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .graphicsLayer {
                            translationY = (animatedSplitGap / 2).toPx()
                        },
                ) {
                    CardHost(
                        type = slot.bottom!!,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            } else if (isDropTarget && dropTargetIsBottom) {
                Spacer(modifier = Modifier.height(12.dp))
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
    }
}

/**
 * 合并卡片之间的分隔拖拽手柄。
 *
 * 视觉上是一条横向的圆角条，上下各有小箭头指示可拖拽方向。
 * 随着拖拽进度 [splitProgress] 增加，手柄颜色渐变为主色调，
 * 达到阈值时显示「松手拆分」提示。
 */
@Composable
private fun SplitDragHandle(
    splitProgress: Float,
    onDragStart: () -> Unit,
    onDrag: (deltaY: Float) -> Unit,
    onDragEnd: () -> Unit,
    onDragCancel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val handleColor = MaterialTheme.colorScheme.outlineVariant
    val activeColor = MaterialTheme.colorScheme.primary
    val currentColor = lerp(handleColor, activeColor, splitProgress)

    val animatedHeight by animateDpAsState(
        targetValue = if (splitProgress > 0.1f) 36.dp else 28.dp,
        label = "handleHeight",
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(animatedHeight)
            .pointerInput(Unit) {
                detectVerticalDragGestures(
                    onDragStart = { onDragStart() },
                    onVerticalDrag = { change, dragAmount ->
                        change.consume()
                        onDrag(dragAmount)
                    },
                    onDragEnd = { onDragEnd() },
                    onDragCancel = { onDragCancel() },
                )
            },
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize(),
        ) {
            Box(
                modifier = Modifier
                    .size(width = 6.dp, height = 3.dp)
                    .clip(RoundedCornerShape(1.5.dp))
                    .background(currentColor.copy(alpha = 0.6f + splitProgress * 0.4f)),
            )

            Spacer(modifier = Modifier.height(3.dp))

            Box(
                modifier = Modifier
                    .size(
                        width = (40 + 20 * splitProgress).dp,
                        height = 4.dp,
                    )
                    .clip(RoundedCornerShape(2.dp))
                    .background(currentColor),
            )

            Spacer(modifier = Modifier.height(3.dp))

            Box(
                modifier = Modifier
                    .size(width = 6.dp, height = 3.dp)
                    .clip(RoundedCornerShape(1.5.dp))
                    .background(currentColor.copy(alpha = 0.6f + splitProgress * 0.4f)),
            )
        }

        AnimatedVisibility(
            visible = splitProgress >= 1f,
            enter = fadeIn(tween(150)),
            exit = fadeOut(tween(150)),
        ) {
            Text(
                text = "松手拆分",
                style = MaterialTheme.typography.labelSmall,
                color = activeColor,
                modifier = Modifier.padding(start = 80.dp),
            )
        }
    }
}
