package com.cleversoftware.contactcardqr.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.cleversoftware.contactcardqr.data.ContactProfile
import com.cleversoftware.contactcardqr.data.QRCodeCard
import com.cleversoftware.contactcardqr.data.QRType

@Composable
fun CreateContactScreen(
	padding: PaddingValues,
	initialProfile: ContactProfile = ContactProfile(),
	onSaveCard: (QRCodeCard) -> Unit = {}
) {
	var firstName by remember { mutableStateOf(initialProfile.firstName) }
	var lastName by remember { mutableStateOf(initialProfile.lastName) }
	var jobTitle by remember { mutableStateOf(initialProfile.jobTitle) }
	var company by remember { mutableStateOf(initialProfile.company) }
	var email by remember { mutableStateOf(initialProfile.email) }
	var phone by remember { mutableStateOf(initialProfile.phone) }
	var website by remember { mutableStateOf(initialProfile.website) }

	Column(
		modifier = Modifier
			.fillMaxSize()
			.padding(padding)
			.padding(16.dp)
			.verticalScroll(rememberScrollState()),
		verticalArrangement = Arrangement.spacedBy(12.dp)
	) {
		Text(
			text = "Create Contact QR Code",
			style = MaterialTheme.typography.headlineMedium
		)

		OutlinedTextField(
			value = firstName,
			onValueChange = { firstName = it },
			label = { Text("First Name") },
			modifier = Modifier.fillMaxWidth(),
			singleLine = true
		)

		OutlinedTextField(
			value = lastName,
			onValueChange = { lastName = it },
			label = { Text("Last Name") },
			modifier = Modifier.fillMaxWidth(),
			singleLine = true
		)

		OutlinedTextField(
			value = jobTitle,
			onValueChange = { jobTitle = it },
			label = { Text("Job Title") },
			modifier = Modifier.fillMaxWidth(),
			singleLine = true
		)

		OutlinedTextField(
			value = company,
			onValueChange = { company = it },
			label = { Text("Company") },
			modifier = Modifier.fillMaxWidth(),
			singleLine = true
		)

		OutlinedTextField(
			value = email,
			onValueChange = { email = it },
			label = { Text("Email Address") },
			keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
			modifier = Modifier.fillMaxWidth(),
			singleLine = true
		)

		OutlinedTextField(
			value = phone,
			onValueChange = { phone = it },
			label = { Text("Phone Number") },
			keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
			modifier = Modifier.fillMaxWidth(),
			singleLine = true
		)

		OutlinedTextField(
			value = website,
			onValueChange = { website = it },
			label = { Text("Website / Social Link") },
			keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
			modifier = Modifier.fillMaxWidth(),
			singleLine = true
		)

		Button(
			onClick = {
				val profile = ContactProfile(
					firstName = firstName,
					lastName = lastName,
					jobTitle = jobTitle,
					company = company,
					email = email,
					phone = phone,
					website = website
				)

				val cardTitle = listOf(firstName, lastName)
					.filter { it.isNotBlank() }
					.joinToString(" ")
					.ifBlank { "Contact Card" }

				val newCard = QRCodeCard(
					title = cardTitle,
					type = QRType.CONTACT,
					payload = profile.toVCardString()
				)
				onSaveCard(newCard)
			},
			enabled = firstName.isNotBlank() || lastName.isNotBlank() || phone.isNotBlank(),
			modifier = Modifier
				.fillMaxWidth()
				.padding(top = 16.dp)
		) {
			Text("Generate Contact QR Code")
		}
	}
}