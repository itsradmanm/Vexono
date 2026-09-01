package com.vexono.app.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.vexono.app.domain.model.ThemeMode
import com.vexono.app.domain.model.UserSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "vexono_settings")

class PreferencesDataStore(private val context: Context) {

    private object Keys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val PRIMARY_COLOR_HEX = stringPreferencesKey("primary_color_hex")
        val SHOW_GREGORIAN_DATE = booleanPreferencesKey("show_gregorian_date")
        val SHOW_ISLAMIC_DATE = booleanPreferencesKey("show_islamic_date")
        val ENABLE_NOTIFICATIONS = booleanPreferencesKey("enable_notifications")
    }

    val userSettingsFlow: Flow<UserSettings> = context.dataStore.data.map { preferences ->
        val themeModeStr = preferences[Keys.THEME_MODE] ?: ThemeMode.DARK.name
        val themeMode = runCatching { ThemeMode.valueOf(themeModeStr) }.getOrDefault(ThemeMode.DARK)
        val primaryColorHex = preferences[Keys.PRIMARY_COLOR_HEX] ?: "#7C4DFF"
        val showGregorian = preferences[Keys.SHOW_GREGORIAN_DATE] ?: true
        val showIslamic = preferences[Keys.SHOW_ISLAMIC_DATE] ?: true
        val enableNotifications = preferences[Keys.ENABLE_NOTIFICATIONS] ?: true

        UserSettings(
            themeMode = themeMode,
            primaryColorHex = primaryColorHex,
            showGregorianDate = showGregorian,
            showIslamicDate = showIslamic,
            enableNotifications = enableNotifications
        )
    }

    suspend fun updateThemeMode(themeMode: ThemeMode) {
        context.dataStore.edit { preferences ->
            preferences[Keys.THEME_MODE] = themeMode.name
        }
    }

    suspend fun updatePrimaryColorHex(colorHex: String) {
        context.dataStore.edit { preferences ->
            preferences[Keys.PRIMARY_COLOR_HEX] = colorHex
        }
    }

    suspend fun updateShowGregorianDate(show: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[Keys.SHOW_GREGORIAN_DATE] = show
        }
    }

    suspend fun updateShowIslamicDate(show: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[Keys.SHOW_ISLAMIC_DATE] = show
        }
    }

    suspend fun updateEnableNotifications(enable: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[Keys.ENABLE_NOTIFICATIONS] = enable
        }
    }
}
