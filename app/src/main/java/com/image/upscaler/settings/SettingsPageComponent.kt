package com.image.upscaler.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.image.upscaler.datastore.SETTINGS_DATA_STORE_QUALIFIER
import com.image.upscaler.navigation.ChildComponent
import com.image.upscaler.navigation.RootComponent
import com.image.upscaler.navigation.getViewModel
import com.image.upscaler.ui.daynight.DayNightManager
import com.image.upscaler.ui.daynight.DayNightMode
import com.image.upscaler.utils.IntentUtils
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SettingsPageComponent(
    componentContext: ComponentContext,
    navigation: StackNavigation<RootComponent.Config>
) : ChildComponent<SettingsPageComponent.ViewModel>(componentContext, navigation) {

    override val viewModel = getViewModel(::ViewModel)

    class ViewModel : ChildComponent.ViewModel(), KoinComponent {

        private val dataStore by inject<DataStore<Preferences>>(SETTINGS_DATA_STORE_QUALIFIER)

        val themeMode =
            IntPreferenceState(dataStore, DayNightManager.THEME_MODE_KEY, DayNightMode.AUTO.id)
    }

    companion object {
        private const val GITHUB_PAGE_URL = "github.com/egeoz/Upscaler"

        fun openGithubPage(context: Context) {
            context.startActivity(IntentUtils.openStringUriIntent("https://$GITHUB_PAGE_URL"))
        }
    }
}