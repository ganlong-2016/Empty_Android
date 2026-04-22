package com.empty.android.feature.settings.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.empty.android.core.data.repository.UserSettingsRepository
import com.empty.android.core.model.ThemeMode
import com.empty.android.core.model.UserSettings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userSettingsRepository: UserSettingsRepository,
) : ViewModel() {

    val uiState: StateFlow<UserSettings> = userSettingsRepository.userSettings.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = UserSettings(),
    )

    fun setThemeMode(themeMode: ThemeMode) {
        viewModelScope.launch { userSettingsRepository.setThemeMode(themeMode) }
    }

    fun setUseDynamicColor(enabled: Boolean) {
        viewModelScope.launch { userSettingsRepository.setUseDynamicColor(enabled) }
    }
}
