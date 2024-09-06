package com.image.upscaler

import android.app.Application
import com.image.upscaler.datastore.DataStoreModule
import com.image.upscaler.intent.InputImageIntentManagerModule
import com.image.upscaler.ui.daynight.DayNightModule
import com.image.upscaler.work.RealESRGANWorkerModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.component.KoinComponent
import org.koin.core.context.startKoin
import timber.log.Timber


class UpscalerApplication : Application(), KoinComponent {

    override fun onCreate() {
        Timber.plant(Timber.DebugTree())
        super.onCreate()
        startKoin {
            // Log Koin into Android logger
            androidLogger()
            // Reference Android context
            androidContext(this@UpscalerApplication)

            modules(
                RealESRGANWorkerModule,
                DataStoreModule,
                DayNightModule,
                InputImageIntentManagerModule,
            )
        }
    }
}