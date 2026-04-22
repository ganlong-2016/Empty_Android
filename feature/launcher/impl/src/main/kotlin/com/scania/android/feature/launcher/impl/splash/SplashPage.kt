package com.scania.android.feature.launcher.impl.splash

import android.graphics.Color
import android.view.View
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

/**
 * 开机/首屏 3D 模型全屏展示页。
 *
 * 目前只放一个空的 [AndroidView]，后续接入真实 3D 引擎
 * （Filament / SceneView / Open3DSceneView / Unity Embed 等）时，
 * 只要把 `factory` 里返回的 View 换成对应的 `GLSurfaceView` / `SurfaceView` 即可，
 * 外层交互（左滑进入 Dashboard）不需要改动。
 */
@Composable
fun SplashPage(
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {

        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                View(context).apply {
                    setBackgroundColor(Color.BLACK)
                    tag = "scania-3d-placeholder"
                }
            },
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(Transparent, Black.copy(alpha = 0.35f)),
                    ),
                ),
        )

        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(32.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = "SCANIA",
                style = MaterialTheme.typography.displayMedium,
                color = White,
            )
            Text(
                text = "Driver-centric launcher",
                style = MaterialTheme.typography.titleMedium,
                color = White.copy(alpha = 0.8f),
            )
        }

        Row(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(horizontal = 32.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "左滑进入主页",
                style = MaterialTheme.typography.titleSmall,
                color = White.copy(alpha = 0.85f),
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = null,
                tint = White,
                modifier = Modifier.size(32.dp),
            )
        }
    }
}
