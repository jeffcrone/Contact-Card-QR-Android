package com.cleversoftware.contactcardqr.data

data class WifiValues(
	val ssid: String = "",
	val password: String = "",
	val securityType: String = "WPA",
	val isHidden: Boolean = false
) {
	// Converts Wi-Fi credentials to standard QR code payload string
	fun toWifiPayload(): String {
		val hiddenFlag = if (isHidden) ";H:true" else ""
		return "WIFI:S:$ssid;T:$securityType;P:$password$hiddenFlag;;"
	}
}
