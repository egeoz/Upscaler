package com.image.upscaler.home

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.image.realesrgan.InterpreterError
import com.image.realesrgan.JNIProgressTracker
import com.image.realesrgan.UpscalingModel
import com.image.upscaler.R
import com.image.upscaler.shared.model.OutputFormat
import com.image.upscaler.ui.mono.EllipsisText
import com.image.upscaler.ui.mono.MonoAlertDialog
import com.image.upscaler.ui.mono.MonoButton
import com.image.upscaler.ui.mono.MonoButtonIcon
import com.image.upscaler.ui.mono.MonoCancelDialogButton
import com.image.upscaler.ui.mono.MonoCloseDialogButton
import com.image.upscaler.ui.mono.drawBottomBorder
import com.image.upscaler.ui.mono.drawTopBorder
import com.image.upscaler.ui.theme.MonoTheme
import com.image.upscaler.ui.theme.border
import com.image.upscaler.ui.theme.spacing
import com.image.upscaler.ui.utils.RowSpacer
import com.image.upscaler.ui.utils.ignoreHorizontalPadding
import com.image.upscaler.utils.IntentUtils
import com.image.upscaler.utils.TimeUtils
import com.image.upscaler.work.RealESRGANWorker
import kotlin.math.roundToInt

@Composable
internal fun UpscalingWork(
    inputData: RealESRGANWorker.InputData,
    progress: RealESRGANWorker.Progress,
    onDismissRequest: () -> Unit,
    onCancelClicked: () -> Unit,
    onRetryClicked: () -> Unit,
    onOpenOutputImageClicked: (Intent) -> Unit,
) = MonoAlertDialog(
    onDismissRequest = onDismissRequest,
    title = if (progress is RealESRGANWorker.Progress.Failed && progress.error == InterpreterError.CREATE_SESSION) {
        { Text(stringResource(id = R.string.upscaling_worker_error_no_backend_title)) }
    } else {
        null
    },
    content = { padding, _ ->
        when (progress) {
            is RealESRGANWorker.Progress.Failed -> Text(
                modifier = Modifier.padding(padding),
                text = if (progress.error == InterpreterError.CREATE_SESSION) {
                    stringResource(id = R.string.upscaling_worker_error_no_backend_desc)
                } else {
                    stringResource(
                        id = R.string.upscaling_worker_error_notification_title,
                        inputData.originalFileName
                    )
                }
            )

            is RealESRGANWorker.Progress.Running -> {
                Column(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(
                            id = R.string.upscaling_worker_notification_title,
                            inputData.originalFileName
                        ),
                        textAlign = TextAlign.Center,
                    )
                    Text(
                        modifier = Modifier.padding(vertical = MaterialTheme.spacing.level3),
                        text = when {
                            progress.progress == JNIProgressTracker.INDETERMINATE_PROGRESS -> stringResource(
                                id = R.string.progress_indeterminate
                            )

                            progress.estimatedMillisLeft == JNIProgressTracker.INDETERMINATE_TIME -> stringResource(
                                id = R.string.progress_template,
                                progress.progress.coerceAtMost(100f).roundToInt(),
                            )

                            else -> stringResource(
                                id = R.string.progress_and_estimated_time_template,
                                progress.progress.coerceAtMost(100f).roundToInt(),
                                TimeUtils.periodToString(
                                    LocalContext.current,
                                    progress.estimatedMillisLeft
                                )
                            )
                        },
                        textAlign = TextAlign.Center,
                    )
                    Text(
                        text = stringResource(id = R.string.upscaling_worker_notification_desc),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            is RealESRGANWorker.Progress.Success -> Column(modifier = Modifier.padding(padding)) {
                Text(
                    stringResource(
                        id = R.string.upscaling_worker_success_notification_title,
                        inputData.originalFileName
                    )
                )
                Text(
                    stringResource(
                        id = R.string.execution_time_template,
                        TimeUtils.periodToString(LocalContext.current, progress.executionTime)
                    )
                )
            }
        }
    },
    buttons = {
        when (progress) {
            is RealESRGANWorker.Progress.Failed -> {
                if (progress.error != InterpreterError.CREATE_SESSION) {
                    MonoCancelDialogButton(onDismissRequest)
                    RowSpacer()
                    MonoButton(onClick = onRetryClicked) {
                        MonoButtonIcon(
                            painterResource(id = R.drawable.ic_arrow_clockwise_24),
                            contentDescription = null
                        )
                        EllipsisText(
                            stringResource(id = R.string.retry)
                        )
                    }
                } else {
                    MonoCloseDialogButton(onDismissRequest)
                }
            }

            is RealESRGANWorker.Progress.Running -> {
                MonoCancelDialogButton(onCancelClicked)
                RowSpacer()
            }

            is RealESRGANWorker.Progress.Success -> {
                MonoCloseDialogButton(onDismissRequest)
                RowSpacer()
                MonoButton(
                    onClick = {
                        onOpenOutputImageClicked(IntentUtils.viewImageIntent(progress.outputFileUri))
                        onDismissRequest()
                    }
                ) {
                    MonoButtonIcon(
                        painterResource(id = R.drawable.outline_launch_24),
                        contentDescription = null
                    )
                    EllipsisText(stringResource(id = R.string.open))
                }
            }
        }
    }
)

@Preview
@Composable
private fun UpscalingWorkRunningPreview() = MonoTheme {
    UpscalingWork(
        inputData = RealESRGANWorker.InputData(
            "Bliss.jpg",
            "",
            OutputFormat.PNG,
            UpscalingModel.X4_PLUS
        ),
        progress = RealESRGANWorker.Progress.Running(69f, 57000),
        onDismissRequest = {},
        onCancelClicked = {},
        onRetryClicked = {},
        onOpenOutputImageClicked = {}
    )
}

@Preview
@Composable
private fun UpscalingWorkFailedPreview() = MonoTheme {
    UpscalingWork(
        inputData = RealESRGANWorker.InputData(
            "Bliss.jpg",
            "",
            OutputFormat.PNG,
            UpscalingModel.X4_PLUS
        ),
        progress = RealESRGANWorker.Progress.Failed(InterpreterError.UNKNOWN),
        onDismissRequest = {},
        onCancelClicked = {},
        onRetryClicked = {},
        onOpenOutputImageClicked = {}
    )
}


@Preview
@Composable
private fun UpscalingWorkFailedNoBackendPreview() = MonoTheme {
    UpscalingWork(
        inputData = RealESRGANWorker.InputData(
            "Bliss.jpg",
            "",
            OutputFormat.PNG,
            UpscalingModel.X4_PLUS
        ),
        progress = RealESRGANWorker.Progress.Failed(InterpreterError.CREATE_SESSION),
        onDismissRequest = {},
        onCancelClicked = {},
        onRetryClicked = {},
        onOpenOutputImageClicked = {}
    )
}

@Preview
@Composable
private fun UpscalingWorkSuccessPreview() = MonoTheme {
    UpscalingWork(
        inputData = RealESRGANWorker.InputData(
            "Bliss.jpg",
            "",
            OutputFormat.PNG,
            UpscalingModel.X4_PLUS
        ),
        progress = RealESRGANWorker.Progress.Success(Uri.EMPTY, 125000),
        onDismissRequest = {},
        onCancelClicked = {},
        onRetryClicked = {},
        onOpenOutputImageClicked = {}
    )
}
