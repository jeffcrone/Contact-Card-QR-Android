package com.cleversoftware.contactcardqr

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Creates a single DataStore instance associated with the Context private
val Context.dataStore by preferencesDataStore(name = "user_profile_prefs")
class ProfileRepository(private val context: Context) {
	companion object {
		private val FIRST_NAME = stringPreferencesKey("first_name")
		private val LAST_NAME = stringPreferencesKey("last_name")
		private val JOB_TITLE = stringPreferencesKey("job_title")
		private val COMPANY = stringPreferencesKey("company")
		private val EMAIL = stringPreferencesKey("email")
		private val PHONE = stringPreferencesKey("phone")
		private val WEBSITE = stringPreferencesKey("website")
	}

	// Flow that emits updated ContactProfile whenever DataStore changes
	val profileFlow: Flow<ContactProfile> = context.dataStore.data.map { prefs ->
		ContactProfile(
			firstName = prefs[FIRST_NAME] ?: "",
			lastName = prefs[LAST_NAME] ?: "",
			jobTitle = prefs[JOB_TITLE] ?: "",
			company = prefs[COMPANY] ?: "",
			email = prefs[EMAIL] ?: "",
			phone = prefs[PHONE] ?: "",
			website = prefs[WEBSITE] ?: ""
		)
	}

	// Save profile asynchronously
	suspend fun saveProfile(profile: ContactProfile) {
		context.dataStore.edit { prefs ->
			prefs[FIRST_NAME] = profile.firstName
			prefs[LAST_NAME] = profile.lastName
			prefs[JOB_TITLE] = profile.jobTitle
			prefs[COMPANY] = profile.company
			prefs[EMAIL] = profile.email
			prefs[PHONE] = profile.phone
			prefs[WEBSITE] = profile.website
		}
	}
}
