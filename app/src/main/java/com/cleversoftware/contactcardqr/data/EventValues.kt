package com.cleversoftware.contactcardqr.data

data class EventValues(
	val title: String = "",
	val location: String = "",
	val description: String = "",
	val startDate: String = "",
	val startTime: String = "",
	val endDate: String = "",
	val endTime: String = "",
	val timeZone: String = ""
) {
	// Helper to format ISO date-time string
	private fun formatDateTime(date: String, time: String): String {
		val cleanDate = date.replace("-", "").replace("/", "").trim()
		val cleanTime = time.replace(":", "").trim()
		return when {
			cleanDate.isNotBlank() && cleanTime.isNotBlank() -> "${cleanDate}T${cleanTime.padStart(6, '0')}"
			cleanDate.isNotBlank() -> cleanDate
			else -> ""
		}
	}

	// Converts event details into a standard vEvent QR code payload string
	fun toEventPayload(): String {
		val lines = mutableListOf<String>()
		lines.add("BEGIN:VEVENT")
		lines.add("SUMMARY:$title")
		if (location.isNotBlank()) lines.add("LOCATION:$location")
		if (description.isNotBlank()) lines.add("DESCRIPTION:$description")

		val startDt = formatDateTime(startDate, startTime)
		if (startDt.isNotBlank()) {
			if (timeZone.isNotBlank()) {
				lines.add("DTSTART;TZID=$timeZone:$startDt")
			} else {
				lines.add("DTSTART:$startDt")
			}
		}

		val endDt = formatDateTime(endDate, endTime)
		if (endDt.isNotBlank()) {
			if (timeZone.isNotBlank()) {
				lines.add("DTEND;TZID=$timeZone:$endDt")
			} else {
				lines.add("DTEND:$endDt")
			}
		}

		lines.add("END:VEVENT")
		return lines.joinToString("\n")
	}
}
