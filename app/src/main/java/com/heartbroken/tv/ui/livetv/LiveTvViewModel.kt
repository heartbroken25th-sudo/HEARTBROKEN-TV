package com.heartbroken.tv.ui.livetv

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.heartbroken.tv.data.model.Channel
import com.heartbroken.tv.data.repository.M3uChannelRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LiveTvUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val allChannels: List<Channel> = emptyList(),
    val filteredChannels: List<Channel> = emptyList(),
    val availableCountries: List<String> = emptyList(),
    val availableCategories: List<String> = emptyList(),
    val selectedCountry: String? = null,
    val selectedCategory: String? = null,
    val searchQuery: String = "",
    val selectedRegionCode: String = "ug" // Default to Uganda
) {
    val isEmpty: Boolean get() = !isLoading && errorMessage == null && filteredChannels.isEmpty()
}

class LiveTvViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(LiveTvUiState())
    val uiState: StateFlow<LiveTvUiState> = _uiState.asStateFlow()

    init {
        loadChannels("ug")
    }

    fun changeRegion(code: String) {
        _uiState.update { it.copy(selectedRegionCode = code, selectedCountry = null, selectedCategory = null, searchQuery = "") }
        loadChannels(code)
    }

    fun loadChannels(regionCode: String = _uiState.value.selectedRegionCode, initialCountry: String? = null) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            // Create a fresh repository for the selected region
            val repository = M3uChannelRepository(regionCode)
            val result = repository.getChannels()

            result.fold(
                onSuccess = { channels ->
                    val countries = channels.map { it.countryName }.distinct().sorted()
                    val categories = channels.map { it.category }.distinct().sorted()

                    _uiState.update { currentState ->
                        val countryToApply = initialCountry ?: currentState.selectedCountry
                        currentState.copy(
                            isLoading = false,
                            allChannels = channels,
                            availableCountries = countries,
                            availableCategories = categories,
                            selectedCountry = countryToApply,
                            filteredChannels = applyFilters(
                                channels,
                                countryToApply,
                                currentState.selectedCategory,
                                currentState.searchQuery
                            )
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = error.message ?: "Could not load channels for this region.")
                    }
                }
            )
        }
    }

    fun onCountrySelected(country: String?) {
        _uiState.update { currentState ->
            currentState.copy(
                selectedCountry = country,
                filteredChannels = applyFilters(currentState.allChannels, country, currentState.selectedCategory, currentState.searchQuery)
            )
        }
    }

    fun onCategorySelected(category: String?) {
        _uiState.update { currentState ->
            currentState.copy(
                selectedCategory = category,
                filteredChannels = applyFilters(currentState.allChannels, currentState.selectedCountry, category, currentState.searchQuery)
            )
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { currentState ->
            currentState.copy(
                searchQuery = query,
                filteredChannels = applyFilters(currentState.allChannels, currentState.selectedCountry, currentState.selectedCategory, query)
            )
        }
    }

    fun clearFilters() {
        _uiState.update { currentState ->
            currentState.copy(
                selectedCountry = null,
                selectedCategory = null,
                searchQuery = "",
                filteredChannels = currentState.allChannels
            )
        }
    }

    private fun applyFilters(channels: List<Channel>, country: String?, category: String?, searchQuery: String): List<Channel> {
        return channels.filter { channel ->
            val matchesCountry = country == null || channel.countryName == country
            val matchesCategory = category == null || channel.category == category
            val matchesSearch = searchQuery.isBlank() || channel.name.contains(searchQuery, ignoreCase = true)
            matchesCountry && matchesCategory && matchesSearch
        }
    }
}
