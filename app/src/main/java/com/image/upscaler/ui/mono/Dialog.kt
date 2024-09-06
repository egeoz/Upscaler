@file:Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")

package com.image.upscaler.ui.mono

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.RowScopeInstance.weight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.image.upscaler.R
import com.image.upscaler.ui.theme.MonoTheme
import com.image.upscaler.ui.theme.border
import com.image.upscaler.ui.theme.elevation
import com.image.upscaler.ui.theme.spacing
import com.image.upscaler.ui.utils.isLandscape

@Composable
fun MonoAlertDialog(
    onDismissRequest: () -> Unit,
    properties: DialogProperties = DialogProperties(
        dismissOnClickOutside = false,
        usePlatformDefaultWidth = false
    ),
    title: @Composable (() -> Unit)? = null,
    content: @Composable ColumnScope.(padding: PaddingValues, isLandscape: Boolean) -> Unit,
    buttons: @Composable RowScope.() -> Unit = { },
) = Dialog(
    onDismissRequest = onDismissRequest,
    properties = properties
) {
    val dialogPaneDescription = getString(Strings.Dialog)
    BoxWithConstraints(
        modifier = Modifier
            .padding(MaterialTheme.spacing.level5)
            .sizeIn(
                minWidth = MonoAlertDialogDefaults.DialogMinWidth,
                maxWidth = MonoAlertDialogDefaults.DialogMaxWidth
            )
            .then(Modifier.semantics { paneTitle = dialogPaneDescription }),
        propagateMinConstraints = true
    ) {
        Surface(
            modifier = Modifier
                .wrapContentWidth()
                .wrapContentHeight(),
            shape = MaterialTheme.shapes.large,
            tonalElevation = MaterialTheme.elevation.container,
            shadowElevation = MaterialTheme.elevation.container,
            border = MaterialTheme.border.thin
        ) {
            Column {
                title?.let {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .drawBottomBorder(MaterialTheme.border.regular)
                            .padding(MaterialTheme.spacing.level5)
                    ) {
                        ProvideTextStyle(
                            value = MaterialTheme.typography.headlineSmall,
                            content = it
                        )
                    }
                }

                ProvideTextStyle(value = MaterialTheme.typography.labelLarge) {
                    content(
                        PaddingValues(
                            start = MaterialTheme.spacing.level5,
                            end = MaterialTheme.spacing.level5,
                            top = MaterialTheme.spacing.level5
                        ),
                        this@BoxWithConstraints.constraints.isLandscape
                    )
                }
                Row(
                    modifier = Modifier
                        .padding(MaterialTheme.spacing.level5)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(
                        MaterialTheme.spacing.level4,
                        Alignment.End
                    ),
                    content = buttons
                )
            }
        }
    }
}

object MonoAlertDialogDefaults {
    val DialogMinWidth = 280.dp
    val DialogMaxWidth = 560.dp
}

@Composable
fun MonoCloseDialogButton(onClick: () -> Unit) = MonoButton(onClick = onClick) {
    MonoButtonIcon(
        Icons.Outlined.Close,
        contentDescription = stringResource(id = R.string.close)
    )
    EllipsisText(stringResource(id = R.string.close))
}

@Composable
fun MonoCancelDialogButton(onClick: () -> Unit) = MonoButton(onClick = onClick) {
    MonoButtonIcon(
        Icons.Outlined.Close,
        contentDescription = stringResource(id = R.string.cancel)
    )
    EllipsisText(stringResource(id = R.string.cancel))
}

fun Modifier.monoDialogScrollableContent() = composed {
    verticalScroll(rememberScrollState()).weight(1f, false)
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview(showBackground = true, showSystemUi = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, showSystemUi = true)
@Composable
private fun MonoAlertDialogPreview() = MonoTheme {
    MonoAlertDialog(
        onDismissRequest = { },
        title = { Text("Dialog title") },
        content = { padding, _ ->
            Box(
                modifier = Modifier.padding(padding)
            ) {
                Text("Dialog content")
            }
        },
        buttons = {
            MonoButton(onClick = {}) {
                EllipsisText("Confirm")
            }
        }
    )
}
