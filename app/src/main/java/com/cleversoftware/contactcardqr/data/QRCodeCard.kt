package com.cleversoftware.contactcardqr.data

import java.util.UUID

enum class QRType {
	CONTACT,
	WIFI,
	URL,
	EVENT,
	EMAIL,
	PHONE,
	TEXT
}

data class QRCodeCard(
	val id: String = UUID.randomUUID().toString(),
	val title: String,
	val type: QRType,
	val payload: String // Generated payload string passed to ZXing
)

fun formatWifiPayload(ssid: String, password: String, security: String = "WPA"): String {
	return "WIFI:S:$ssid;T:$security;P:$password;;"
}

fun formatEventPayload(title: String, location: String, startIso: String, endIso: String): String {
	return """
        BEGIN:VEVENT
        SUMMARY:$title
        LOCATION:$location
        DTSTART:$startIso
        DTEND:$endIso
        END:VEVENT
    """.trimIndent()
}