package com.scania.android.feature.launcher.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scania.android.core.car.model.CalendarEvent
import com.scania.android.core.car.model.DriverProfile
import com.scania.android.core.car.model.MediaPlayback
import com.scania.android.core.car.model.VehicleStatus
import com.scania.android.core.car.model.WeatherInfo
import com.scania.android.core.car.repository.CalendarRepository
import com.scania.android.core.car.repository.DriverProfileRepository
import com.scania.android.core.car.repository.MediaRepository
import com.scania.android.core.car.repository.VehicleStatusRepository
import com.scania.android.core.car.repository.WeatherRepository
import com.scania.android.feature.launcher.impl.dashboard.DashboardCardType
import com.scania.android.feature.launcher.impl.dashboard.DashboardSlot
import com.scania.android.feature.launcher.impl.dashboard.NavigationWidthLevel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Launcher 页状态：
 * - [dashboardState]：来自 AIDL / SDK 的各类业务数据汇总；
 * - [slots]：卡片布局（可重排、可合并），由用户交互修改，存放在内存中（后续可下沉到 DataStore）。
 */
@HiltViewModel
class LauncherViewModel @Inject constructor(
    private val mediaRepository: MediaRepository,
    weatherRepository: WeatherRepository,
    calendarRepository: CalendarRepository,
    driverProfileRepository: DriverProfileRepository,
    vehicleStatusRepository: VehicleStatusRepository,
) : ViewModel() {

    val dashboardState: StateFlow<DashboardState> = combine(
        mediaRepository.playback,
        weatherRepository.current,
        calendarRepository.events,
        driverProfileRepository.currentDriver,
        vehicleStatusRepository.status,
    ) { media, weather, events, driver, vehicle ->
        DashboardState(
            media = media,
            weather = weather,
            events = events,
            driver = driver,
            vehicle = vehicle,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = DashboardState(),
    )

    private val _slots = MutableStateFlow(defaultSlots)
    val slots: StateFlow<List<DashboardSlot>> = _slots.asStateFlow()

    private val _navigationWidth = MutableStateFlow(NavigationWidthLevel.OneThird)
    val navigationWidth: StateFlow<NavigationWidthLevel> = _navigationWidth.asStateFlow()

    fun onNavigationWidthDrag(fraction: Float) {
        _navigationWidth.value = NavigationWidthLevel.nearest(fraction.coerceIn(0.2f, 0.75f))
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

    fun onTogglePlay() {
        viewModelScope.launch {
            val state = dashboardState.value.media
            if (state?.state == com.scania.android.core.car.model.MediaPlaybackState.Playing) {
                mediaRepository.pause()
            } else {
                mediaRepository.play()
            }
        }
    }

    fun onNext() = viewModelScope.launch { mediaRepository.next() }
    fun onPrevious() = viewModelScope.launch { mediaRepository.previous() }

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

data class DashboardState(
    val media: MediaPlayback? = null,
    val weather: WeatherInfo? = null,
    val events: List<CalendarEvent> = emptyList(),
    val driver: DriverProfile? = null,
    val vehicle: VehicleStatus? = null,
)
