package com.vexono.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.vexono.app.presentation.navigation.VexonoNavGraph
import com.vexono.app.presentation.theme.VexonoTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val appContainer = (application as VexonoApplication).container

        setContent {
            val userSettings by appContainer.settingsRepository.userSettings.collectAsState(
                initial = com.vexono.app.domain.model.UserSettings()
            )

            VexonoTheme(
                themeMode = userSettings.themeMode,
                primaryColorHex = userSettings.primaryColorHex
            ) {
                VexonoNavGraph(container = appContainer)
            }
        }
    }
}
