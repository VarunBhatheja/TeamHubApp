package com.example.teamhubapp.ui.theme

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// AndroidViewModel used instead of ViewModel — needs Application context for DataStore
class ThemeViewModel(application: Application) : AndroidViewModel(application) {

    private val themePreference = ThemePreference(application)

    // stateIn converts Flow → StateFlow so Compose can read it directly
    val isDarkMode: StateFlow<Boolean> = themePreference.isDarkMode
        .stateIn(
            scope        = viewModelScope,
            started      = SharingStarted.WhileSubscribed(5_000),
            initialValue = false
        )

    // Flips current theme
    fun toggleTheme() {
        viewModelScope.launch { themePreference.setDarkMode(!isDarkMode.value) }
    }

    // Sets theme explicitly — used on app start to avoid flash
    fun setDarkMode(isDark: Boolean) {
        viewModelScope.launch { themePreference.setDarkMode(isDark) }
    }
}