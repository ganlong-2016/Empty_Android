package com.empty.android.feature.landing.impl

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

@Composable
fun LandingScreen(
    onNavigateToHome: () -> Unit,
) {
    val density = LocalDensity.current
    val screenWidthDp = LocalConfiguration.current.screenWidthDp
    val screenWidthPx = with(density) { screenWidthDp.dp.toPx() }

    var rotateX by remember { mutableFloatStateOf(0f) }
    var rotateY by remember { mutableFloatStateOf(0f) }

    val swipeZoneWidthDp = 48.dp
    val swipeThreshold = screenWidthPx * 0.3f

    val scope = rememberCoroutineScope()
    val animRotateX = remember { Animatable(0f) }
    val animRotateY = remember { Animatable(0f) }

    val primaryColor = MaterialTheme.colorScheme.primary
    val outlineVariantColor = MaterialTheme.colorScheme.outlineVariant

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface),
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragEnd = {
                            scope.launch {
                                animRotateX.snapTo(rotateX)
                                animRotateX.animateTo(
                                    0f,
                                    spring(
                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                        stiffness = Spring.StiffnessLow,
                                    ),
                                ) { rotateX = value }
                            }
                            scope.launch {
                                animRotateY.snapTo(rotateY)
                                animRotateY.animateTo(
                                    0f,
                                    spring(
                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                        stiffness = Spring.StiffnessLow,
                                    ),
                                ) { rotateY = value }
                            }
                        },
                    ) { change, dragAmount ->
                        change.consume()
                        rotateY += dragAmount.x * 0.3f
                        rotateX -= dragAmount.y * 0.3f
                    }
                },
        ) {
            val cx = size.width / 2
            val cy = size.height / 2
            val cubeSize = size.width * 0.25f

            val rx = Math.toRadians(rotateX.toDouble())
            val ry = Math.toRadians(rotateY.toDouble())

            fun project(x: Float, y: Float, z: Float): Offset {
                val y1 = y * cos(rx).toFloat() - z * sin(rx).toFloat()
                val z1 = y * sin(rx).toFloat() + z * cos(rx).toFloat()
                val x2 = x * cos(ry).toFloat() + z1 * sin(ry).toFloat()
                val perspective = 1000f / (1000f + (z1 * cos(ry).toFloat() - x * sin(ry).toFloat()))
                return Offset(cx + x2 * perspective, cy + y1 * perspective)
            }

            val s = cubeSize / 2
            val vertices = listOf(
                Triple(-s, -s, -s), Triple(s, -s, -s),
                Triple(s, s, -s), Triple(-s, s, -s),
                Triple(-s, -s, s), Triple(s, -s, s),
                Triple(s, s, s), Triple(-s, s, s),
            )

            val projected = vertices.map { (x, y, z) -> project(x, y, z) }

            val edges = listOf(
                0 to 1, 1 to 2, 2 to 3, 3 to 0,
                4 to 5, 5 to 6, 6 to 7, 7 to 4,
                0 to 4, 1 to 5, 2 to 6, 3 to 7,
            )

            val edgeColor = primaryColor

            edges.forEach { (a, b) ->
                drawLine(
                    color = edgeColor,
                    start = projected[a],
                    end = projected[b],
                    strokeWidth = 3f,
                )
            }

            val faces = listOf(
                listOf(0, 1, 2, 3), listOf(4, 5, 6, 7),
                listOf(0, 1, 5, 4), listOf(2, 3, 7, 6),
                listOf(0, 3, 7, 4), listOf(1, 2, 6, 5),
            )

            val faceColors = listOf(
                Color(0x33FF6B6B), Color(0x334ECDC4),
                Color(0x33FFE66D), Color(0x3395E1D3),
                Color(0x33F38181), Color(0x33AA96DA),
            )

            faces.forEachIndexed { idx, face ->
                val path = androidx.compose.ui.graphics.Path().apply {
                    moveTo(projected[face[0]].x, projected[face[0]].y)
                    for (i in 1 until face.size) {
                        lineTo(projected[face[i]].x, projected[face[i]].y)
                    }
                    close()
                }
                drawPath(path, faceColors[idx % faceColors.size])
            }

            val gridCount = 8
            val gridSize = size.width * 0.8f
            val gridStartX = cx - gridSize / 2
            val gridStartY = cy + cubeSize * 0.8f
            val gridColor = outlineVariantColor.copy(alpha = 0.3f)

            for (i in 0..gridCount) {
                val frac = i.toFloat() / gridCount
                drawLine(
                    color = gridColor,
                    start = Offset(gridStartX + frac * gridSize, gridStartY),
                    end = Offset(gridStartX + frac * gridSize, gridStartY + gridSize * 0.3f),
                    strokeWidth = 1f,
                )
                drawLine(
                    color = gridColor,
                    start = Offset(gridStartX, gridStartY + frac * gridSize * 0.3f),
                    end = Offset(gridStartX + gridSize, gridStartY + frac * gridSize * 0.3f),
                    strokeWidth = 1f,
                )
            }

            val particleCount = 20
            for (i in 0 until particleCount) {
                val angle = (i * 18f + rotateY * 2f)
                val radius = cubeSize * 1.2f + i * 8f
                val px = cx + radius * cos(Math.toRadians(angle.toDouble())).toFloat()
                val py = cy + radius * sin(Math.toRadians(angle.toDouble())).toFloat() * 0.4f
                val dotAlpha = (0.2f + 0.3f * abs(sin(Math.toRadians(angle.toDouble()))).toFloat())
                drawCircle(
                    color = edgeColor.copy(alpha = dotAlpha),
                    radius = 3f + i * 0.3f,
                    center = Offset(px, py),
                )
            }
        }

        Text(
            text = "EmptyAndroid",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 80.dp),
        )

        Text(
            text = "拖拽旋转 3D 模型",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 124.dp),
        )

        var edgeSwipeOffset by remember { mutableFloatStateOf(0f) }
        val edgeSwipeAnim = remember { Animatable(0f) }

        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .width(swipeZoneWidthDp)
                .fillMaxHeight()
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragEnd = {
                            if (abs(edgeSwipeOffset) > swipeThreshold) {
                                onNavigateToHome()
                            } else {
                                scope.launch {
                                    edgeSwipeAnim.snapTo(edgeSwipeOffset)
                                    edgeSwipeAnim.animateTo(0f, spring()) {
                                        edgeSwipeOffset = value
                                    }
                                }
                            }
                        },
                        onDragCancel = {
                            scope.launch {
                                edgeSwipeAnim.snapTo(edgeSwipeOffset)
                                edgeSwipeAnim.animateTo(0f, spring()) {
                                    edgeSwipeOffset = value
                                }
                            }
                        },
                    ) { change, dragAmount ->
                        change.consume()
                        if (dragAmount.x < 0) {
                            edgeSwipeOffset += dragAmount.x
                        }
                    }
                },
            contentAlignment = Alignment.Center,
        ) {
            val indicatorAlpha = (abs(edgeSwipeOffset) / swipeThreshold).coerceIn(0f, 1f)

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = "向左滑动进入主页",
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                    alpha = 0.3f + indicatorAlpha * 0.7f,
                ),
                modifier = Modifier
                    .offset {
                        IntOffset(edgeSwipeOffset.roundToInt() / 3, 0)
                    },
            )
        }

        Text(
            text = "← 左滑进入主页",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 48.dp),
        )
    }
}
