package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.MainAppScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.GemsViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // Instantiate the ViewModel using our custom offline room-and-preferences factory
            val viewModel: GemsViewModel = viewModel(
                factory = GemsViewModel.Factory(application)
            )

            // Gather values of theme from our stored DataStore settings
            val darkThemeOverride by viewModel.darkTheme.collectAsState()
            val useDarkTheme = when (darkThemeOverride) {
                true -> true
                false -> false
                null -> isSystemInDarkTheme()
            }
            
            val appTheme by viewModel.appTheme.collectAsState()

            MyApplicationTheme(darkTheme = useDarkTheme, appTheme = appTheme) {
                MainAppScreen(viewModel = viewModel)
            }
        }
    }
}
