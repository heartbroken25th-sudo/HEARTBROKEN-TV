package com.heartbroken.tv.ui.livetv

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.heartbroken.tv.data.model.Channel
import com.heartbroken.tv.data.repository.AssetsChannelRepository
import com.heartbroken.tv.data.repository.ChannelRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * UI state for the Live TV grid.
 *
 * Contains both the full channel list and the currently filtered list. Filters
 * are applied in-memory because the catalog is small enough to keep fully
 * loaded. If the catalog grows to thousands of channels, we'll move filtering
 * into the repository (Room DB) — the UI contract will not change.
 */
data class LiveTvUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val allChannels: List<Channel> = emptyList(),
    val filteredChannels: List<Channel> = emptyList(),
    val availableCountries: List<String> = emptyList(),
    val availableCategories: List<String> = emptyList(),
    val selectedCountry: String? = null,
    val selectedCategory: String? = null
) {
    val isEmpty: Boolean get() = !isLoading && errorMessage == null && filteredChannels.isEmpty()
}

/**
 * Live TV ViewModel.
 *
 * Responsibilities:
 *  - Load all channels from the repository.
 *  - Extract available countries and categories from the actual data.
 *  - Hold current filter selections (country, category).
 *  - Recompute the filtered channel list whenever filters change.
 */
class LiveTvViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ChannelRepository = AssetsChannelRepository(application)

    private val _uiState = MutableStateFlow(LiveTvUiState())
    val uiState: StateFlow<LiveTvUiState> = _uiState.asStateFlow()

    init {
        loadChannels()
    }

    fun loadChannels() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = repository.getChannels()

            result.fold(
                onSuccess = { channels ->
                    val countries = channels.map { it.countryName }.distinct().sorted()
                    val categories = channels.map { it.category }.distinct().sorted()

                    _uiState.update { currentState ->
                        currentState.copy(
                            isLoading = false,
                            allChannels = channels,
                            availableCountries = countries,
                            availableCategories = categories,
                            // Re-apply existing filters to the new data
                            filteredChannels = applyFilters(
                                channels,
                                currentState.selectedCountry,
                                currentState.selectedCategory
                            )
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Could not load channels."
                        )
                    }
                }
            )
        }
    }

    fun onCountrySelected(country: String?) {
        _uiState.update { currentState ->
            currentState.copy(
                selectedCountry = country,
                filteredChannels = applyFilters(
                    currentState.allChannels,
                    country,
                    currentState.selectedCategory
                )
            )
        }
    }

    fun onCategorySelected(category: String?) {
        _uiState.update { currentState ->
            currentState.copy(
                selectedCategory = category,
                filteredChannels = applyFilters(
                    currentState.allChannels,
                    currentState.selectedCountry,
                    category
                )
            )
        }
    }

    fun clearFilters() {
        _uiState.update { currentState ->
            currentState.copy(
                selectedCountry = null,
                selectedCategory = null,
                filteredChannels = currentState.allChannels
            )
        }
    }

    private fun applyFilters(
        channels: List<Channel>,
        country: String?,
        category: String?
    ): List<Channel> {
        return channels.filter { channel ->
            val matchesCountry = country == null || channel.countryName == country
            val matchesCategory = category == null || channel.category == category
            matchesCountry && matchesCategory
        }
    }
}
