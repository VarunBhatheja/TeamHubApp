package com.example.teamhubapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import com.example.teamhubapp.navigation.AppNavGraph
import com.example.teamhubapp.ui.theme.TeamHubTheme
import com.example.teamhubapp.ui.theme.ThemeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    // Activity-level ViewModel — needed before first frame
    // to prevent theme flash on startup
    private val themeViewModel: ThemeViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Draw content behind system bars (status + nav bar)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            // Collect dark mode preference from DataStore
            val isDarkMode by themeViewModel.isDarkMode.collectAsState()

            // Pass isDarkMode into TeamHubTheme — overrides system default
            TeamHubTheme(darkTheme = isDarkMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background  // ← ADD Surface
                ){
                AppNavGraph(
                    // Pass toggle function down to NavGraph
                    // so any screen can trigger a theme change
                    onToggleTheme = { themeViewModel.toggleTheme() },
                    isDarkMode    = isDarkMode
                )
                }
            }
        }
    }
}
