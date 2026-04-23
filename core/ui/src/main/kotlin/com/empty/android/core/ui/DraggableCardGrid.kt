package com.empty.android.core.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import kotlin.math.roundToInt

/**
 * Represents a card item in the draggable grid.
 * A card can be a single item or a merged pair displayed top/bottom.
 */
data class GridCardItem(
    val id: String,
    val title: String,
    val subtitle: String = "",
    val isNavigation: Boolean = false,
    val mergedWith: GridCardItem? = null,
)

/**
 * A draggable card grid that supports:
 * - Fixed navigation cards (cannot be dragged)
 * - Drag-to-reorder for non-navigation cards (long press to start)
 * - Drag one card onto another to merge them (displayed vertically stacked)
 * - Drag to split merged cards back into individual cards
 */
@Composable
fun DraggableCardGrid(
    cards: List<GridCardItem>,
    onCardsChanged: (List<GridCardItem>) -> Unit,
    modifier: Modifier = Modifier,
    columns: Int = 2,
) {
    var draggedIndex by remember { mutableIntStateOf(-1) }
    var dragOffset by remember { mutableStateOf(Offset.Zero) }
    var targetIndex by remember { mutableIntStateOf(-1) }

    val cardPositions = remember { mutableStateListOf<Pair<Offset, Offset>>() }

    fun updatePositions(index: Int, topLeft: Offset, bottomRight: Offset) {
        while (cardPositions.size <= index) {
            cardPositions.add(Pair(Offset.Zero, Offset.Zero))
        }
        cardPositions[index] = Pair(topLeft, bottomRight)
    }

    fun findTargetIndex(currentPos: Offset): Int {
        for (i in cardPositions.indices) {
            if (i == draggedIndex || i >= cards.size) continue
            val (tl, br) = cardPositions[i]
            if (currentPos.x in tl.x..br.x && currentPos.y in tl.y..br.y) {
                return i
            }
        }
        return -1
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        itemsIndexed(
            items = cards,
            key = { _, card -> card.id },
        ) { index, card ->
            val isDragged = draggedIndex == index
            val isTarget = targetIndex == index && draggedIndex != -1

            Box(
                modifier = Modifier
                    .zIndex(if (isDragged) 10f else 0f)
                    .onGloballyPositioned { coordinates ->
                        val pos = coordinates.positionInWindow()
                        val bounds = coordinates.boundsInWindow()
                        updatePositions(index, pos, Offset(bounds.right, bounds.bottom))
                    }
                    .then(
                        if (!card.isNavigation) {
                            Modifier.pointerInput(cards, index) {
                                detectDragGesturesAfterLongPress(
                                    onDragStart = {
                                        draggedIndex = index
                                        dragOffset = Offset.Zero
                                    },
                                    onDragEnd = {
                                        if (targetIndex >= 0 && targetIndex < cards.size) {
                                            val draggedCard = cards[draggedIndex]
                                            val targetCard = cards[targetIndex]

                                            if (!targetCard.isNavigation) {
                                                if (draggedCard.mergedWith == null && targetCard.mergedWith == null) {
                                                    val merged = targetCard.copy(
                                                        mergedWith = draggedCard,
                                                    )
                                                    val newCards = cards.toMutableList()
                                                    newCards[targetIndex] = merged
                                                    newCards.removeAt(draggedIndex)
                                                    onCardsChanged(newCards)
                                                } else {
                                                    val newCards = cards.toMutableList()
                                                    val removed = newCards.removeAt(draggedIndex)
                                                    val insertAt = if (draggedIndex < targetIndex) targetIndex - 1 else targetIndex
                                                    newCards.add(insertAt, removed)
                                                    onCardsChanged(newCards)
                                                }
                                            }
                                        }
                                        draggedIndex = -1
                                        dragOffset = Offset.Zero
                                        targetIndex = -1
                                    },
                                    onDragCancel = {
                                        draggedIndex = -1
                                        dragOffset = Offset.Zero
                                        targetIndex = -1
                                    },
                                ) { change, dragAmount ->
                                    change.consume()
                                    dragOffset += Offset(dragAmount.x, dragAmount.y)

                                    if (draggedIndex >= 0 && draggedIndex < cardPositions.size) {
                                        val (tl, _) = cardPositions[draggedIndex]
                                        val center = tl + dragOffset + Offset(
                                            (cardPositions[draggedIndex].second.x - tl.x) / 2,
                                            (cardPositions[draggedIndex].second.y - tl.y) / 2,
                                        )
                                        targetIndex = findTargetIndex(center)
                                    }
                                }
                            }
                        } else {
                            Modifier
                        },
                    ),
            ) {
                CardContent(
                    card = card,
                    isDragged = isDragged,
                    isDropTarget = isTarget,
                    dragOffset = if (isDragged) dragOffset else Offset.Zero,
                    onSplit = { cardToSplit ->
                        val idx = cards.indexOf(cardToSplit)
                        if (idx >= 0 && cardToSplit.mergedWith != null) {
                            val newCards = cards.toMutableList()
                            newCards[idx] = cardToSplit.copy(mergedWith = null)
                            newCards.add(idx + 1, cardToSplit.mergedWith)
                            onCardsChanged(newCards)
                        }
                    },
                )
            }
        }
    }
}

@Composable
private fun CardContent(
    card: GridCardItem,
    isDragged: Boolean,
    isDropTarget: Boolean,
    dragOffset: Offset,
    onSplit: (GridCardItem) -> Unit,
) {
    val elevation = if (isDragged) 12.dp else if (isDropTarget) 6.dp else 2.dp
    val cardScale = if (isDragged) 1.05f else if (isDropTarget) 1.08f else 1f
    val borderColor = when {
        isDropTarget -> MaterialTheme.colorScheme.primary
        isDragged -> MaterialTheme.colorScheme.tertiary
        else -> Color.Transparent
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (isDragged) {
                    Modifier.offset {
                        IntOffset(dragOffset.x.roundToInt(), dragOffset.y.roundToInt())
                    }
                } else {
                    Modifier
                },
            )
            .scale(cardScale)
            .alpha(if (isDragged) 0.9f else 1f)
            .border(
                width = if (borderColor != Color.Transparent) 2.dp else 0.dp,
                color = borderColor,
                shape = RoundedCornerShape(12.dp),
            ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation),
        colors = CardDefaults.cardColors(
            containerColor = if (card.isNavigation) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceContainerLow
            },
        ),
    ) {
        if (card.mergedWith != null) {
            MergedCardContent(card = card, onSplit = onSplit)
        } else {
            SingleCardContent(card = card)
        }
    }
}

@Composable
private fun SingleCardContent(card: GridCardItem) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start,
    ) {
        Text(
            text = card.title,
            style = MaterialTheme.typography.titleSmall,
            color = if (card.isNavigation) {
                MaterialTheme.colorScheme.onPrimaryContainer
            } else {
                MaterialTheme.colorScheme.onSurface
            },
        )
        if (card.subtitle.isNotBlank()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = card.subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = if (card.isNavigation) {
                    MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
                maxLines = 2,
            )
        }
    }
}

@Composable
private fun MergedCardContent(
    card: GridCardItem,
    onSplit: (GridCardItem) -> Unit,
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 8.dp),
            ) {
                Text(
                    text = card.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                if (card.subtitle.isNotBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = card.subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 12.dp),
                color = MaterialTheme.colorScheme.outlineVariant,
            )

            card.mergedWith?.let { merged ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 16.dp),
                ) {
                    Text(
                        text = merged.title,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    if (merged.subtitle.isNotBlank()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = merged.subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                        )
                    }
                }
            }
        }

        IconButton(
            onClick = { onSplit(card) },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(28.dp)
                .padding(4.dp),
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "拆分卡片",
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.size(16.dp),
            )
        }
    }
}
