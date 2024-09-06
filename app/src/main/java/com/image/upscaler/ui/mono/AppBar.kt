package com.image.upscaler.ui.mono

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.image.upscaler.ui.theme.MonoTheme
import com.image.upscaler.ui.theme.border
import com.image.upscaler.ui.theme.spacing
import com.image.upscaler.ui.utils.RowSpacer

@Composable
fun MonoAppBar(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    windowInsets: WindowInsets = MonoAppBarDefaults.windowInsets,
    leadingIcon: @Composable () -> Unit = { },
    trailingIcons: @Composable RowScope.() -> Unit = { }
) = Row(
    modifier = modifier
        .background(MaterialTheme.colorScheme.primaryContainer)
        .windowInsetsPadding(windowInsets)
        .fillMaxWidth()
        .drawBottomBorder(MaterialTheme.border.regular)
        .padding(MaterialTheme.spacing.level3),
    verticalAlignment = Alignment.CenterVertically
) {
    leadingIcon()
    Box(modifier = Modifier.padding(MaterialTheme.spacing.level3)) {
        ProvideTextStyle(
            value = MaterialTheme.typography.displayMedium,
            content = title
        )
    }
    RowSpacer()
    trailingIcons()
}

object MonoAppBarDefaults {

    val windowInsets: WindowInsets
        @Composable
        get() = WindowInsets.safeDrawing
            .only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top)
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, showSystemUi = true)
@Preview(showBackground = true, showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun MonoAppBarPreview() = MonoTheme {
    Scaffold(
        topBar = {
            MonoAppBar(
                title = { Text("AppBar") },
                leadingIcon = {
                    IconButton(
                        onClick = {}
                    ) {
                        Icon(Icons.Rounded.ArrowBack, contentDescription = null)
                    }
                }
            ) {
                IconButton(
                    onClick = {}
                ) {
                    Icon(Icons.Default.Build, contentDescription = null)
                }
            }
        }
    ) {}
}
