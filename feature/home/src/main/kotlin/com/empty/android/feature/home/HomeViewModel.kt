package com.empty.android.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.empty.android.core.common.result.Result
import com.empty.android.core.common.result.asResult
import com.empty.android.core.data.repository.DemoItemRepository
import com.empty.android.core.domain.GetDemoItemsUseCase
import com.empty.android.core.model.DemoItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Home 页 ViewModel：展示一个典型的「读取仓库数据 + 触发刷新」流程。
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    getDemoItems: GetDemoItemsUseCase,
    private val repository: DemoItemRepository,
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = getDemoItems()
        .asResult()
        .map(::toUiState)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HomeUiState.Loading,
        )

    fun refresh() {
        viewModelScope.launch { repository.refreshItems() }
    }

    fun addSample() {
        viewModelScope.launch {
            val id = System.currentTimeMillis().toString()
            repository.addItem(
                DemoItem(
                    id = id,
                    title = "Sample #$id",
                    description = "这条数据是在本地新增的示例项",
                ),
            )
        }
    }

    fun remove(id: String) {
        viewModelScope.launch { repository.removeItem(id) }
    }

    private fun toUiState(result: Result<List<DemoItem>>): HomeUiState = when (result) {
        is Result.Loading -> HomeUiState.Loading
        is Result.Success -> if (result.data.isEmpty()) {
            HomeUiState.Empty
        } else {
            HomeUiState.Success(result.data)
        }
        is Result.Error -> HomeUiState.Error(result.exception?.message)
    }
}

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data object Empty : HomeUiState
    data class Success(val items: List<DemoItem>) : HomeUiState
    data class Error(val message: String?) : HomeUiState
}
