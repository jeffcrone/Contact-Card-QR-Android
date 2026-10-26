package com.cleversoftware.contactcardqr.data

data class GPSValues(
	val title: String = "",
	val latitude: Double = 0.0,
	val longitude: Double = 0.0
) {
	// Converts latitude & longitude into standard Geo URI payload string for maps
	fun toGpsPayload(): String {
		return "geo:$latitude,$longitude?q=$latitude,$longitude($title)"
	}
}
