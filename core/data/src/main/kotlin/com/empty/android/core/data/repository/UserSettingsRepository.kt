package com.empty.android.core.data.repository

import com.empty.android.core.datastore.UserPreferencesDataSource
import com.empty.android.core.model.ThemeMode
import com.empty.android.core.model.UserSettings
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
