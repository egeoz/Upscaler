package com.image.upscaler

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import com.arkivanov.decompose.defaultComponentContext
import com.image.upscaler.navigation.RootComponent
import com.image.upscaler.navigation.RootNavigation
import com.image.upscaler.ui.daynight.DayNightManager
import com.image.upscaler.ui.daynight.DayNightMode
import com.image.upscaler.ui.theme.MonoTheme
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {

    private val dayNightManager by inject<DayNightManager>()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        // Draw edge-to-edge
        WindowCompat.setDecorFitsSystemWindows(window, false)
        lifecycleScope.launch {
            dayNightManager.themeModeFlow.collect {
                delegate.localNightMode = it.delegateNightMode
            }
        }
        // Handle the splash screen transition.
        super.onCreate(savedInstanceState)
        val rootComponent = RootComponent(defaultComponentContext())
        setContent {
            /**
             * Due to this bug https://issuetracker.google.com/issues/227926002 when using
             * splash screen API on some devices with Compose Jetpack Navigation leads to a blank
             * screen. However wrapping the [NavHost] in a [Scaffold] solves this issue, so after
             * digging a bit, I figured out the following line is the workaround for the
             * blank screen bug
             */
            ScaffoldDefaults.contentWindowInsets
            MonoTheme(DayNightMode.fromDelegateNightMode(delegate.localNightMode).lightMode) {
                RootNavigation(rootComponent)
            }
        }
    }
}
