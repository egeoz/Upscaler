package com.image.upscaler.ui.utils

import androidx.compose.ui.unit.Constraints

val Constraints.isLandscape: Boolean
    get() = maxWidth > maxHeight
