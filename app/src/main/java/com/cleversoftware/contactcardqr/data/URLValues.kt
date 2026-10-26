package com.cleversoftware.contactcardqr.data

data class URLValues(
	val title: String = "",
	val url: String = ""
) {
	// Converts URL input into a valid web address payload string
	fun toUrlPayload(): String {
		val trimmedUrl = url.trim()
		return if (!trimmedUrl.startsWith("http://") && !trimmedUrl.startsWith("https://")) {
			"https://$trimmedUrl"
		} else {
			trimmedUrl
		}
	}
}
