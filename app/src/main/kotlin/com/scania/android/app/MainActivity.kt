package com.scania.android.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scania.android.app.navigation.AppNavHost
import com.scania.android.core.data.repository.UserSettingsRepository
import com.scania.android.core.designsystem.theme.ScaniaTheme
import com.scania.android.core.model.ThemeMode
import com.scania.android.core.model.UserSettings
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { ScaniaApp() }
    }
}

@Composable
private fun ScaniaApp(viewModel: MainViewModel = hiltViewModel()) {
    val settings by viewModel.settings.collectAsState()

    val darkTheme = when (settings.themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> androidx.compose.foundation.isSystemInDarkTheme()
    }

    ScaniaTheme(
        darkTheme = darkTheme,
        dynamicColor = settings.useDynamicColor,
    ) {
        AppNavHost()
    }
}

@HiltViewModel
class MainViewModel @Inject constructor(
    repository: UserSettingsRepository,
) : ViewModel() {
    val settings: StateFlow<UserSettings> = repository.userSettings.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = UserSettings(),
    )
}
