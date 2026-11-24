package com.example.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.storeDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "shri_karthik_prefs"
)

class StorePreferencesRepository(private val context: Context) {
    private object Keys {
        val IS_LOCATION_SAVED = booleanPreferencesKey("is_location_saved")
        val IS_OWNER_MODE = booleanPreferencesKey("is_owner_mode")
        val IS_SEEDED = booleanPreferencesKey("is_initial_catalog_seeded")
    }

    val isLocationSaved: Flow<Boolean> = context.storeDataStore.data.map { prefs ->
        prefs[Keys.IS_LOCATION_SAVED] ?: false
    }

    val isOwnerMode: Flow<Boolean> = context.storeDataStore.data.map { prefs ->
        prefs[Keys.IS_OWNER_MODE] ?: false
    }

    val isSeeded: Flow<Boolean> = context.storeDataStore.data.map { prefs ->
        prefs[Keys.IS_SEEDED] ?: false
    }

    suspend fun toggleSaveLocation(): Boolean {
        var newValue = false
        context.storeDataStore.edit { prefs ->
            val current = prefs[Keys.IS_LOCATION_SAVED] ?: false
            newValue = !current
            prefs[Keys.IS_LOCATION_SAVED] = newValue
        }
        return newValue
    }

    suspend fun setOwnerMode(enabled: Boolean) {
        context.storeDataStore.edit { prefs ->
            prefs[Keys.IS_OWNER_MODE] = enabled
        }
    }

    suspend fun markSeeded() {
        context.storeDataStore.edit { prefs ->
            prefs[Keys.IS_SEEDED] = true
        }
    }
}
