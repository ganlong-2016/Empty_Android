package com.scania.android.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.scania.android.core.model.ThemeMode
import com.scania.android.core.model.UserSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 基于 DataStore 的用户偏好数据源。
 */
@Singleton
class UserPreferencesDataSource @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) {

    val userSettings: Flow<UserSettings> = dataStore.data.map { prefs ->
        UserSettings(
            themeMode = prefs[KEY_THEME_MODE]
                ?.let { runCatching { ThemeMode.valueOf(it) }.getOrDefault(ThemeMode.SYSTEM) }
                ?: ThemeMode.SYSTEM,
            useDynamicColor = prefs[KEY_DYNAMIC_COLOR] ?: true,
        )
    }

    suspend fun setThemeMode(themeMode: ThemeMode) {
        dataStore.edit { prefs -> prefs[KEY_THEME_MODE] = themeMode.name }
    }

    suspend fun setUseDynamicColor(enabled: Boolean) {
        dataStore.edit { prefs -> prefs[KEY_DYNAMIC_COLOR] = enabled }
    }

    private companion object {
        val KEY_THEME_MODE = stringPreferencesKey("theme_mode")
        val KEY_DYNAMIC_COLOR = booleanPreferencesKey("dynamic_color")
    }
}
