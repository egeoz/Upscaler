package com.image.upscaler.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp

/**
 * Convert an [Int] pixel value to [Dp].
 */
@Composable
fun Int.pxToDp(): Dp = with(LocalDensity.current) { this@pxToDp.toDp() }

@Composable
fun Dp.dpToPx(): Float = with(LocalDensity.current) { this@dpToPx.toPx() }
