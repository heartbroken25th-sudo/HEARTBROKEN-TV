package com.heartbroken.tv.ui.settings

import android.app.Application
import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

// Create the DataStore instance
private val Context.dataStore by preferencesDataStore(name = "settings")

data class SettingsUiState(
    val autoplay: Boolean = true,
    val preferredQuality: String = "Auto",
    val wifiOnly: Boolean = false,
    val appTheme: String = "Dark"
)

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = SettingsRepository(application.applicationContext)

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        // Listen for changes in DataStore and update the UI
        viewModelScope.launch {
            repository.settingsFlow.collect { settings ->
                _uiState.value = settings
            }
        }
    }

    fun setAutoplay(enabled: Boolean) = viewModelScope.launch { repository.setAutoplay(enabled) }
    fun setPreferredQuality(quality: String) = viewModelScope.launch { repository.setPreferredQuality(quality) }
    fun setWifiOnly(enabled: Boolean) = viewModelScope.launch { repository.setWifiOnly(enabled) }
    fun setAppTheme(theme: String) = viewModelScope.launch { repository.setAppTheme(theme) }
}

class SettingsRepository(private val context: Context) {
    private object Keys {
        val AUTOPLAY = booleanPreferencesKey("autoplay")
        val QUALITY = stringPreferencesKey("quality")
        val WIFI_ONLY = booleanPreferencesKey("wifi_only")
        val THEME = stringPreferencesKey("theme")
    }

    val settingsFlow = context.dataStore.data.map { prefs ->
        SettingsUiState(
            autoplay = prefs[Keys.AUTOPLAY] ?: true,
            preferredQuality = prefs[Keys.QUALITY] ?: "Auto",
            wifiOnly = prefs[Keys.WIFI_ONLY] ?: false,
            appTheme = prefs[Keys.THEME] ?: "Dark"
        )
    }

    suspend fun setAutoplay(enabled: Boolean) {
        context.dataStore.edit { it[Keys.AUTOPLAY] = enabled }
    }

    suspend fun setPreferredQuality(quality: String) {
        context.dataStore.edit { it[Keys.QUALITY] = quality }
    }

    suspend fun setWifiOnly(enabled: Boolean) {
        context.dataStore.edit { it[Keys.WIFI_ONLY] = enabled }
    }

    suspend fun setAppTheme(theme: String) {
        context.dataStore.edit { it[Keys.THEME] = theme }
    }
}
