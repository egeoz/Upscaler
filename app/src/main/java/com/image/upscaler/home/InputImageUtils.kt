package com.image.upscaler.home

import com.image.realesrgan.UpscalingModel
import com.image.upscaler.shared.model.InputImage

fun InputImage.mayRunOutOfMemory(model: UpscalingModel): Boolean {
    // TODO: Remove hardcoded 4 channels
    val estimatedBytesSize = width * height * 4L
    val nonAllocatedMemory = Runtime.getRuntime().let {
        // totalMemory - freeMemory is the currently used memory in JVM
        it.maxMemory() - (it.totalMemory() - it.freeMemory())
    }
    return estimatedBytesSize * (model.scale * model.scale + 1) > nonAllocatedMemory
}
