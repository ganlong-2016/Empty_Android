package com.scania.android.feature.launcher.impl

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.scania.android.feature.launcher.impl.dashboard.DashboardPage
import com.scania.android.feature.launcher.impl.splash.SplashPage

/**
 * Launcher 顶层入口：
 *
 * - [HorizontalPager] 两页：
 *   - 第 0 页：[SplashPage]（3D 模型全屏）；
 *   - 第 1 页：[DashboardPage]（卡片主页）。
 *   从 3D 页面向左滑动即进入主页。
 *
 * 这里只从 [LauncherViewModel] 取「布局状态」（卡片顺序、导航卡宽度）；
 * 每张业务卡片的具体数据在卡片内部通过自己的 `@HiltViewModel` 订阅。
 */
@Composable
fun LauncherScreen(
    modifier: Modifier = Modifier,
    viewModel: LauncherViewModel = hiltViewModel(),
) {
    val slots by viewModel.slots.collectAsStateWithLifecycle()
    val navWidth by viewModel.navigationWidthFraction.collectAsStateWithLifecycle()

    val pagerState = rememberPagerState(pageCount = { 2 })

    HorizontalPager(
        state = pagerState,
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        pageSize = PageSize.Fill,
    ) { page ->
        when (page) {
            0 -> SplashPage()
            1 -> DashboardPage(
                slots = slots,
                navigationWidthFraction = navWidth,
                onNavigationWidthDrag = viewModel::dragNavigationWidth,
                onNavigationWidthDragEnd = viewModel::snapNavigationWidth,
            )
        }
    }
}
