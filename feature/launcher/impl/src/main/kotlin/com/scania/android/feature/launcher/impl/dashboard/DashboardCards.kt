package com.scania.android.feature.launcher.impl.dashboard

/**
 * Launcher Dashboard 可用的卡片类型。
 *
 * 导航卡（[Navigation]）固定钉在左侧、宽度可拖拽；其他卡片水平排列，可拖拽重排、
 * 也可以上下合并到同一个「槽位」里形成上下两段的复合卡。
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
 */
enum class NavigationWidthLevel(val fraction: Float) {
    OneThird(0.33f),
    Half(0.5f),
    TwoThird(0.66f),
    ;

    companion object {
        fun nearest(fraction: Float): NavigationWidthLevel =
            values().minBy { kotlin.math.abs(it.fraction - fraction) }
    }
}
