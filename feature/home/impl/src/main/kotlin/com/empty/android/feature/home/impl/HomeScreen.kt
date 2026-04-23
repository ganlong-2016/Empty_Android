package com.empty.android.feature.home.impl

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.empty.android.core.model.DemoItem
import com.empty.android.core.ui.DraggableCardGrid
import com.empty.android.core.ui.GridCardItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToSettings: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "EmptyAndroid") },
                actions = {
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = "刷新")
                    }
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(imageVector = Icons.Default.Settings, contentDescription = "设置")
                    }
                },
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { viewModel.addSample() },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("添加示例") },
            )
        },
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            when (val state = uiState) {
                HomeUiState.Loading -> LoadingIndicator()
                HomeUiState.Empty -> EmptyPlaceholder()
                is HomeUiState.Error -> ErrorPlaceholder(message = state.message)
                is HomeUiState.Success -> {
                    HomeCardGrid(items = state.items)
                }
            }
        }
    }
}

@Composable
private fun HomeCardGrid(
    items: List<DemoItem>,
) {
    val navigationCard = GridCardItem(
        id = "nav_settings",
        title = "设置",
        subtitle = "主题模式 · 动态取色",
        isNavigation = true,
    )

    var cards by remember(items) {
        val itemCards = items.map { item ->
            GridCardItem(
                id = item.id,
                title = item.title,
                subtitle = item.description,
            )
        }
        mutableStateOf(listOf(navigationCard) + itemCards)
    }

    DraggableCardGrid(
        cards = cards,
        onCardsChanged = { newCards -> cards = newCards },
        columns = 2,
    )
}

@Composable
private fun LoadingIndicator() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun EmptyPlaceholder() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "暂无数据",
            style = MaterialTheme.typography.headlineSmall,
        )
        Text(
            text = "点击右下角按钮添加一条示例数据，或点击刷新按钮从网络拉取",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun ErrorPlaceholder(message: String?) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "加载失败：${message ?: "未知错误"}",
            color = MaterialTheme.colorScheme.error,
        )
    }
}
