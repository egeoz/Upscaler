package com.image.upscaler

import android.annotation.SuppressLint
import android.content.Context
import androidx.startup.Initializer
import androidx.work.Configuration
import androidx.work.Logger
import androidx.work.WorkManager
import com.image.upscaler.work.RealESRGANWorkerManager

/**
 * Initializes [androidx.work.WorkManager] using `androidx.startup`.
 */
class UpscalerWorkManagerInitializer : Initializer<WorkManager> {

    @SuppressLint("RestrictedApi")
    override fun create(context: Context): WorkManager {
        // Initialize WorkManager with the default configuration.
        Logger.get().debug(TAG, "Initializing WorkManager with default configuration.")
        WorkManager.initialize(context, Configuration.Builder().build())
        val instance = WorkManager.getInstance(context)

        // Don't try to restart RealESRGANWorker
        instance.cancelUniqueWork(RealESRGANWorkerManager.UNIQUE_WORK_ID)

        return instance
    }

    override fun dependencies(): List<Class<out Initializer<*>?>> {
        return emptyList()
    }

    companion object {
        @SuppressLint("RestrictedApi")
        private val TAG = Logger.tagWithPrefix("WrkMgrInitializer")
    }
}