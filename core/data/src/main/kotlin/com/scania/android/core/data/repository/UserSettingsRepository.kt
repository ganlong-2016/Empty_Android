package com.scania.android.core.data.repository

import com.scania.android.core.datastore.UserPreferencesDataSource
import com.scania.android.core.model.ThemeMode
import com.scania.android.core.model.UserSettings
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

interface UserSettingsRepository {
    val userSettings: Flow<UserSettings>
    suspend fun setThemeMode(themeMode: ThemeMode)
    suspend fun setUseDynamicColor(enabled: Boolean)
}

@Singleton
class DefaultUserSettingsRepository @Inject constructor(
    private val dataSource: UserPreferencesDataSource,
) : UserSettingsRepository {

    override val userSettings: Flow<UserSettings> = dataSource.userSettings

    override suspend fun setThemeMode(themeMode: ThemeMode) =
        dataSource.setThemeMode(themeMode)

    override suspend fun setUseDynamicColor(enabled: Boolean) =
        dataSource.setUseDynamicColor(enabled)
}
