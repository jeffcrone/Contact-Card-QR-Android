package com.cleversoftware.contactcardqr.data

import androidx.room.Entity
import androidx.room.PrimaryKey
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

@Entity(tableName = "qr_cards")
data class QRCodeCard(
	@PrimaryKey
	val id: String = UUID.randomUUID().toString(),
	val title: String,
	val type: QRType,
	val payload: String,
	val createdAt: Long = System.currentTimeMillis()
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
