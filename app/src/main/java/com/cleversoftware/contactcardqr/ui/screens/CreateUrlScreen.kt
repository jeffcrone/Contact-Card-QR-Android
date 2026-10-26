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
import com.cleversoftware.contactcardqr.data.QRCodeCard
import com.cleversoftware.contactcardqr.data.QRType
import com.cleversoftware.contactcardqr.data.URLValues

@Composable
fun CreateUrlScreen(
	padding: PaddingValues,
	initialValues: URLValues = URLValues(),
	onSaveCard: (QRCodeCard) -> Unit = {}
) {
	var title by remember { mutableStateOf(initialValues.title) }
	var url by remember { mutableStateOf(initialValues.url) }

	Column(
		modifier = Modifier
			.fillMaxSize()
			.padding(padding)
			.padding(16.dp)
			.verticalScroll(rememberScrollState()),
		verticalArrangement = Arrangement.spacedBy(12.dp)
	) {
		Text(
			text = "Website Link QR Code",
			style = MaterialTheme.typography.headlineMedium
		)

		OutlinedTextField(
			value = title,
			onValueChange = { title = it },
			label = { Text("Title / Label") },
			placeholder = { Text("e.g. My Portfolio") },
			modifier = Modifier.fillMaxWidth(),
			singleLine = true
		)

		OutlinedTextField(
			value = url,
			onValueChange = { url = it },
			label = { Text("URL / Web Address") },
			placeholder = { Text("https://example.com") },
			keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
			modifier = Modifier.fillMaxWidth(),
			singleLine = true
		)

		Button(
			onClick = {
				val urlValues = URLValues(title = title, url = url)
				val newCard = QRCodeCard(
					title = title.ifBlank { "Website Link" },
					type = QRType.URL,
					payload = urlValues.toUrlPayload()
				)
				onSaveCard(newCard)
			},
			enabled = url.isNotBlank(),
			modifier = Modifier
				.fillMaxWidth()
				.padding(top = 16.dp)
		) {
			Text("Generate URL QR Code")
		}
	}
}
