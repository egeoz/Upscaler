package com.image.upscaler.ui.daynight

import com.image.upscaler.datastore.SETTINGS_DATA_STORE_QUALIFIER
import org.koin.dsl.module

val DayNightModule = module {
    single { DayNightManager(get(SETTINGS_DATA_STORE_QUALIFIER)) }
}
