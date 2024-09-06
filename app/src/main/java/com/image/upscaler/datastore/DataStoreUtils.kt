package com.image.upscaler.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.image.upscaler.common.Identifiable

suspend fun DataStore<Preferences>.writeIntIdentifiable(
    key: Preferences.Key<Int>,
    value: Identifiable<Int>
) {
    edit { it[key] = value.id }
}
