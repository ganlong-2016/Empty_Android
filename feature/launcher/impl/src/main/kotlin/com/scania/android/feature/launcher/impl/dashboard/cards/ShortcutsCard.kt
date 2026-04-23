package com.scania.android.feature.launcher.impl.dashboard.cards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Traffic
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class ShortcutItem(
    val id: String,
    val label: String,
    val icon: ImageVector,
)

data class ShortcutsCardUiState(
    val items: List<ShortcutItem> = defaultShortcuts,
)

@HiltViewModel
class ShortcutsCardViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(ShortcutsCardUiState())
    val uiState: StateFlow<ShortcutsCardUiState> = _uiState.asStateFlow()
}

@Composable
fun ShortcutsCard(
    modifier: Modifier = Modifier,
    viewModel: ShortcutsCardViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    ShortcutsCardContent(state, modifier = modifier)
}

@Composable
fun ShortcutsCardContent(
    state: ShortcutsCardUiState,
    modifier: Modifier = Modifier,
) {
    CardFrame(
        title = "快捷方式",
        subtitle = "${state.items.size} 项",
        modifier = modifier,
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(state.items, key = { it.id }) { shortcut ->
                ShortcutTile(shortcut)
            }
        }
    }
}

@Composable
private fun ShortcutTile(shortcut: ShortcutItem) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.primaryContainer,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(modifier = Modifier.padding(12.dp), contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = shortcut.icon,
                    contentDescription = shortcut.label,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
            Text(
                text = shortcut.label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }
    }
}

private val defaultShortcuts = listOf(
    ShortcutItem("phone", "电话", Icons.Default.Phone),
    ShortcutItem("bt", "蓝牙", Icons.Default.Bluetooth),
    ShortcutItem("wifi", "Wi-Fi", Icons.Default.Wifi),
    ShortcutItem("music", "音乐", Icons.Default.MusicNote),
    ShortcutItem("traffic", "路况", Icons.Default.Traffic),
    ShortcutItem("settings", "设置", Icons.Default.Settings),
)
