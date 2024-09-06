package com.image.upscaler.intent

import android.content.Intent
import android.net.Uri
import com.image.upscaler.utils.getParcelableExtraCompat
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.consumeAsFlow

class InputImageIntentManager {

    private val _intentUriChannel = Channel<Uri>(1, BufferOverflow.DROP_OLDEST)
    val imageUriFlow: Flow<Uri>
        get() = _intentUriChannel.consumeAsFlow()

    /**
     * @return whether the [Intent] has been consumed
     */
    fun notifyNewIntent(intent: Intent): Boolean {
        if (intent.action == Intent.ACTION_SEND && intent.type?.startsWith("image/") == true) {
            intent.getParcelableExtraCompat<Uri>(Intent.EXTRA_STREAM)?.let {
                _intentUriChannel.trySend(it)
                return true
            }
        }
        return false
    }
}
