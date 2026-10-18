package com.cleversoftware.contactcardqr

data class ContactProfile(
	val firstName: String = "",
	val lastName: String = "",
	val jobTitle: String = "",
	val company: String = "",
	val email: String = "",
	val phone: String = "",
	val website: String = ""
) {
	// Converts contact info to vCard standard string for QR codes
	fun toVCardString(): String {
		return """
            BEGIN:VCARD
            VERSION:3.0
            N:$lastName;$firstName;;;
            FN:$firstName $lastName
            ORG:$company
            TITLE:$jobTitle
            TEL;TYPE=CELL:$phone
            EMAIL:$email
            URL:$website
            END:VCARD
        """.trimIndent()
	}
}