package com.heartbroken.tv.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.heartbroken.tv.data.model.Channel
import com.heartbroken.tv.data.repository.AssetsChannelRepository
import com.heartbroken.tv.data.repository.ChannelRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * All possible states of the Home discovery screen.
 *
 * [Empty] and [Error] are deliberately distinct:
 *  - Empty = catalog is genuinely empty (valid, expected on first launch)
 *  - Error = loading failed (network, malformed file, missing asset)
 *
 * Conflating the two is a common mistake that results in "no channels" being
 * displayed when the data source is actually broken. Keeping them separate
 * means the UI can tell the user the truth.
 */
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

/**
 * A country derived from the actual channel catalog.
 * Never created manually — always computed from real channel data.
 */
data class CountrySummary(
    val code: String,
    val name: String,
    val channelCount: Int
)

/**
 * A category derived from the actual channel catalog.
 */
data class CategorySummary(
    val name: String,
    val channelCount: Int
)

/**
 * Home discovery ViewModel.
 *
 * Loads channels from the repository once on creation and exposes the result as
 * a [StateFlow]. The Compose UI collects this state and renders the matching
 * screen. Retry is exposed as a public method — nothing about the state machine
 * is hidden from the UI.
 */
class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ChannelRepository = AssetsChannelRepository(application)

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadChannels()
    }

    /**
     * Reloads the channel catalog. Safe to call from a retry button.
     */
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
                    HomeUiState.Error(
                        error.message ?: "Could not load channels."
                    )
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
