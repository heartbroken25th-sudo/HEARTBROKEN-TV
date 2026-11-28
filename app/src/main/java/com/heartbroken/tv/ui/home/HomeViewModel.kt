package com.heartbroken.tv.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.heartbroken.tv.data.model.Channel
import com.heartbroken.tv.data.repository.ChannelRepository
import com.heartbroken.tv.data.repository.M3uChannelRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data object Empty : HomeUiState
    data class Error(val message: String) : HomeUiState
    data class Success(
        val countries: List<CountrySummary>,
        val categories: List<CategorySummary>,
        val totalChannels: Int
    ) : HomeUiState
}

data class CountrySummary(
    val code: String,
    val name: String,
    val channelCount: Int
)

data class CategorySummary(
    val name: String,
    val channelCount: Int
)

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ChannelRepository = M3uChannelRepository("ug")

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadChannels()
    }

    fun loadChannels() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            val result = repository.getChannels()
            _uiState.value = result.fold(
                onSuccess = { channels ->
                    if (channels.isEmpty()) {
                        HomeUiState.Empty
                    } else {
                        HomeUiState.Success(
                            countries = deriveCountries(channels),
                            categories = deriveCategories(channels),
                            totalChannels = channels.size
                        )
                    }
                },
                onFailure = { error ->
                    HomeUiState.Error(error.message ?: "Could not load channels.")
                }
            )
        }
    }

    private fun deriveCountries(channels: List<Channel>): List<CountrySummary> {
        return channels
            .groupBy { it.countryCode }
            .map { (code, list) ->
                CountrySummary(
                    code = code,
                    name = list.first().countryName,
                    channelCount = list.size
                )
            }
            .sortedBy { it.name }
    }

    private fun deriveCategories(channels: List<Channel>): List<CategorySummary> {
        return channels
            .groupBy { it.category }
            .map { (name, list) ->
                CategorySummary(name = name, channelCount = list.size)
            }
            .sortedByDescending { it.channelCount }
    }
}
