package com.scania.android.feature.launcher.impl

import androidx.lifecycle.ViewModel
import com.scania.android.feature.launcher.impl.dashboard.DashboardCardType
import com.scania.android.feature.launcher.impl.dashboard.DashboardSlot
import com.scania.android.feature.launcher.impl.dashboard.NavigationWidthLevel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

/**
 * Launcher 顶层 ViewModel —— **只**负责桌面布局状态：
 *
 * - [slots]：卡片的排列/合并/拆分；
 * - [navigationWidthFraction]：导航卡当前占屏宽的比例（拖拽时连续变化）；
 * - [navigationWidthLevel]：松手后 snap 到的离散档位（1/3、1/2、2/3）。
 *
 * 每张业务卡片的数据（媒体、天气、日程、车辆……）都由**自己的 [HiltViewModel]** 负责，
 * 这里不聚合，避免一个大 `UiState` 越变越臃肿。新增/删除卡片只需要改
 * [DashboardCardType] 和 `CardHost`，不用改本 ViewModel。
 */
@HiltViewModel
class LauncherViewModel @Inject constructor() : ViewModel() {

    private val _slots = MutableStateFlow(defaultSlots)
    val slots: StateFlow<List<DashboardSlot>> = _slots.asStateFlow()

    private val _navigationWidthFraction = MutableStateFlow(NavigationWidthLevel.OneThird.fraction)
    val navigationWidthFraction: StateFlow<Float> = _navigationWidthFraction.asStateFlow()

    val navigationWidthLevel: NavigationWidthLevel
        get() = NavigationWidthLevel.nearest(_navigationWidthFraction.value)

    /** 拖拽过程中调用：按像素增量累加到当前 fraction，保持实时响应。 */
    fun dragNavigationWidth(deltaFraction: Float) {
        _navigationWidthFraction.update { current ->
            (current + deltaFraction).coerceIn(
                NavigationWidthLevel.MinFraction,
                NavigationWidthLevel.MaxFraction,
            )
        }
    }

    /** 松手时调用：snap 到最近的一档。 */
    fun snapNavigationWidth() {
        _navigationWidthFraction.update { NavigationWidthLevel.nearest(it).fraction }
    }

    /**
     * 把一张卡片拖到目标槽位：
     * - 当 [targetIsBottom] 为 true 且目标槽只有上半段时，把源卡拼到目标槽的下半段，形成合并卡；
     * - 否则，源卡作为目标槽的上半段，原目标卡沉到下半段。
     *
     * 导航卡（Navigation）不能参与合并。
     */
    fun mergeInto(fromSlotId: String, toSlotId: String, targetIsBottom: Boolean) {
        if (fromSlotId == toSlotId) return
        _slots.update { current ->
            val from = current.firstOrNull { it.id == fromSlotId } ?: return@update current
            val to = current.firstOrNull { it.id == toSlotId } ?: return@update current
            if (from.top == DashboardCardType.Navigation || to.top == DashboardCardType.Navigation) {
                return@update current
            }
            val movingCardType = from.top
            val merged = if (targetIsBottom && to.bottom == null) {
                to.copy(bottom = movingCardType)
            } else {
                to.copy(top = movingCardType, bottom = to.top)
            }
            current.mapNotNull { slot ->
                when (slot.id) {
                    from.id -> {
                        val promoted = from.bottom
                        if (promoted != null) from.copy(top = promoted, bottom = null) else null
                    }
                    to.id -> merged
                    else -> slot
                }
            }
        }
    }

    /** 交换两个非导航卡槽位的顺序。 */
    fun reorderSlot(fromIndex: Int, toIndex: Int) {
        _slots.update { current ->
            if (fromIndex !in current.indices || toIndex !in current.indices) return@update current
            if (current[fromIndex].top == DashboardCardType.Navigation) return@update current
            if (current[toIndex].top == DashboardCardType.Navigation) return@update current
            current.toMutableList().apply {
                val item = removeAt(fromIndex)
                add(toIndex, item)
            }
        }
    }

    /** 把当前槽位里的上下两张卡拆开。 */
    fun splitSlot(slotId: String) {
        _slots.update { current ->
            val slot = current.firstOrNull { it.id == slotId } ?: return@update current
            val bottom = slot.bottom ?: return@update current
            val newList = current.toMutableList()
            val idx = newList.indexOf(slot)
            newList[idx] = slot.copy(bottom = null)
            newList.add(idx + 1, DashboardSlot(id = slotId + "_split", top = bottom))
            newList
        }
    }

    companion object {
        private val defaultSlots: List<DashboardSlot> = listOf(
            DashboardSlot("slot_nav", DashboardCardType.Navigation),
            DashboardSlot("slot_media", DashboardCardType.Media),
            DashboardSlot("slot_weather", DashboardCardType.Weather),
            DashboardSlot("slot_calendar", DashboardCardType.Calendar),
            DashboardSlot("slot_driver", DashboardCardType.Driver),
            DashboardSlot("slot_vehicle", DashboardCardType.Vehicle),
            DashboardSlot("slot_shortcuts", DashboardCardType.Shortcuts),
        )
    }
}
