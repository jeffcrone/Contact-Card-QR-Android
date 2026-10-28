package com.cleversoftware.contactcardqr.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsRepository(private val context: Context) {
	companion object {
		private val SHOW_SAVED_QR_CODES = booleanPreferencesKey("show_saved_qr_codes")
	}

	// Flow that emits whether QR codes should be displayed in Saved Items (default = true)
	val showSavedQrCodesFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
		prefs[SHOW_SAVED_QR_CODES] ?: true
	}

	suspend fun setShowSavedQrCodes(show: Boolean) {
		context.dataStore.edit { prefs ->
			prefs[SHOW_SAVED_QR_CODES] = show
		}
	}
}
