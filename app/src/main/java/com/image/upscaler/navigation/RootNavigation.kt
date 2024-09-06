package com.image.upscaler.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.arkivanov.decompose.extensions.compose.LocalLifecycleOwner
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.Children
import com.image.upscaler.home.HomePage
import com.image.upscaler.settings.SettingsPage

@Composable
fun RootNavigation(rootComponent: RootComponent) = Children(rootComponent.childStack) {
    val child = it.instance
    CompositionLocalProvider(LocalLifecycleOwner provides child.component) {
        when (child) {
            is RootComponent.Child.Home -> HomePage(child.component)
            is RootComponent.Child.Settings -> SettingsPage(child.component)
        }
    }
}
