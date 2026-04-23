package com.scania.android.feature.launcher.impl.dashboard

/**
 * Launcher Dashboard 可用的卡片类型。
 *
 * 新增一类卡片只需要：
 * 1. 在这里加一个 enum 值；
 * 2. 新建 `cards/XxxCard.kt`（内部带 `@HiltViewModel`）；
 * 3. 在 `CardHost` 的 `when` 分支里加上分发。
 *
 * 整条链路不需要动 [com.scania.android.feature.launcher.impl.LauncherViewModel]。
 */
enum class DashboardCardType {
    Navigation,
    Media,
    Weather,
    Calendar,
    Driver,
    Vehicle,
    Shortcuts,
}

/**
 * 卡片槽位：一个槽位最多承载两张卡（上下排列）。
 * 这样就实现了「把两个卡片合并到一个卡片里上下展示」的需求。
 */
data class DashboardSlot(
    val id: String,
    val top: DashboardCardType,
    val bottom: DashboardCardType? = null,
) {
    val isMerged: Boolean get() = bottom != null
}

/**
 * 导航卡在宽度轴上的离散档位。对应 1/3、1/2、2/3 屏宽。
 *
 * 拖拽中我们使用连续 fraction 实时渲染；松手时再 snap 到这里面最近的一档。
 */
enum class NavigationWidthLevel(val fraction: Float) {
    OneThird(1f / 3f),
    Half(0.5f),
    TwoThird(2f / 3f),
    ;

    companion object {
        val MinFraction: Float = OneThird.fraction
        val MaxFraction: Float = TwoThird.fraction

        fun nearest(fraction: Float): NavigationWidthLevel =
            entries.minBy { kotlin.math.abs(it.fraction - fraction) }
    }
}
