package com.cleversoftware.contactcardqr.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsRepository(private val context: Context) {
	companion object {
		private val SHOW_SAVED_QR_CODES = booleanPreferencesKey("show_saved_qr_codes")
		private val DEFAULT_DISPLAY_MODE = stringPreferencesKey("default_display_mode")
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

	// Flow that emits default CardDisplayMode (default = QR_AND_TEXT)
	val defaultDisplayModeFlow: Flow<CardDisplayMode> = context.dataStore.data.map { prefs ->
		val modeName = prefs[DEFAULT_DISPLAY_MODE] ?: CardDisplayMode.QR_AND_TEXT.name
		try {
			CardDisplayMode.valueOf(modeName)
		} catch (e: Exception) {
			CardDisplayMode.QR_AND_TEXT
		}
	}

	suspend fun setDefaultDisplayMode(mode: CardDisplayMode) {
		context.dataStore.edit { prefs ->
			prefs[DEFAULT_DISPLAY_MODE] = mode.name
		}
	}
}
