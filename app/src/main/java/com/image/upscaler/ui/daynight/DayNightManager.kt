package com.image.upscaler.ui.daynight

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DayNightManager(private val dataStore: DataStore<Preferences>) {

    private val themeModeKey = intPreferencesKey("theme_mode")

    val themeModeFlow: Flow<DayNightMode>
        get() = dataStore.data.map { prefs ->
            DayNightMode.fromId(prefs[themeModeKey]) ?: DayNightMode.AUTO
        }

    companion object {

        const val THEME_MODE_KEY = "theme_mode"
    }
}
