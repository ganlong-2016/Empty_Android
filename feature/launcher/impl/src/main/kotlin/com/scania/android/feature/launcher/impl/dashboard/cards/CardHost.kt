package com.scania.android.feature.launcher.impl.dashboard.cards

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.scania.android.feature.launcher.impl.dashboard.DashboardCardType

/**
 * 根据 [DashboardCardType] 分发到各张 **Stateful** 卡片。
 *
 * 每张卡片自己负责：
 * - 用 `hiltViewModel()` 拿到自己的 ViewModel；
 * - 从自己的 ViewModel 里读取并渲染 `UiState`；
 * - 把交互回调传回自己的 ViewModel。
 *
 * 因此本函数是整个 feature 里唯一需要「认识所有卡片」的地方，扩展非常轻量：
 * 新增一张卡片就在 [DashboardCardType] 加一个枚举值 + 在这里加一个分支。
 */
@Composable
fun CardHost(
    type: DashboardCardType,
    modifier: Modifier = Modifier,
) {
    when (type) {
        DashboardCardType.Navigation -> NavigationCard(modifier = modifier)
        DashboardCardType.Media -> MediaCard(modifier = modifier)
        DashboardCardType.Weather -> WeatherCard(modifier = modifier)
        DashboardCardType.Calendar -> CalendarCard(modifier = modifier)
        DashboardCardType.Driver -> DriverCard(modifier = modifier)
        DashboardCardType.Vehicle -> VehicleCard(modifier = modifier)
        DashboardCardType.Shortcuts -> ShortcutsCard(modifier = modifier)
    }
}
