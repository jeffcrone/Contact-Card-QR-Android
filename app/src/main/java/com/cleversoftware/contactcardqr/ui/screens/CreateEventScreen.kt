package com.cleversoftware.contactcardqr.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.unit.dp
import com.cleversoftware.contactcardqr.data.EventValues
import com.cleversoftware.contactcardqr.data.QRCodeCard
import com.cleversoftware.contactcardqr.data.QRType

@Composable
fun CreateEventScreen(
	padding: PaddingValues,
	initialValues: EventValues = EventValues(),
	onSaveCard: (QRCodeCard) -> Unit = {}
) {
	var title by remember { mutableStateOf(initialValues.title) }
	var location by remember { mutableStateOf(initialValues.location) }
	var description by remember { mutableStateOf(initialValues.description) }
	var startDate by remember { mutableStateOf(initialValues.startDate) }
	var startTime by remember { mutableStateOf(initialValues.startTime) }
	var endDate by remember { mutableStateOf(initialValues.endDate) }
	var endTime by remember { mutableStateOf(initialValues.endTime) }
	var timeZone by remember { mutableStateOf(initialValues.timeZone) }

	Column(
		modifier = Modifier
			.fillMaxSize()
			.padding(padding)
			.padding(16.dp)
			.verticalScroll(rememberScrollState()),
		verticalArrangement = Arrangement.spacedBy(12.dp)
	) {
		Text(
			text = "Calendar Event QR Code",
			style = MaterialTheme.typography.headlineMedium
		)

		OutlinedTextField(
			value = title,
			onValueChange = { title = it },
			label = { Text("Event Title") },
			placeholder = { Text("e.g. Birthday Party") },
			modifier = Modifier.fillMaxWidth(),
			singleLine = true
		)

		OutlinedTextField(
			value = location,
			onValueChange = { location = it },
			label = { Text("Location") },
			placeholder = { Text("e.g. 123 Main St, New York") },
			modifier = Modifier.fillMaxWidth(),
			singleLine = true
		)

		Row(
			modifier = Modifier.fillMaxWidth(),
			horizontalArrangement = Arrangement.spacedBy(8.dp)
		) {
			OutlinedTextField(
				value = startDate,
				onValueChange = { startDate = it },
				label = { Text("Start Date") },
				placeholder = { Text("YYYY-MM-DD") },
				modifier = Modifier.weight(1f),
				singleLine = true
			)

			OutlinedTextField(
				value = startTime,
				onValueChange = { startTime = it },
				label = { Text("Start Time") },
				placeholder = { Text("18:00") },
				modifier = Modifier.weight(1f),
				singleLine = true
			)
		}

		Row(
			modifier = Modifier.fillMaxWidth(),
			horizontalArrangement = Arrangement.spacedBy(8.dp)
		) {
			OutlinedTextField(
				value = endDate,
				onValueChange = { endDate = it },
				label = { Text("End Date") },
				placeholder = { Text("YYYY-MM-DD") },
				modifier = Modifier.weight(1f),
				singleLine = true
			)

			OutlinedTextField(
				value = endTime,
				onValueChange = { endTime = it },
				label = { Text("End Time") },
				placeholder = { Text("21:00") },
				modifier = Modifier.weight(1f),
				singleLine = true
			)
		}

		OutlinedTextField(
			value = timeZone,
			onValueChange = { timeZone = it },
			label = { Text("Time Zone (Optional)") },
			placeholder = { Text("e.g. America/New_York or UTC") },
			modifier = Modifier.fillMaxWidth(),
			singleLine = true
		)

		OutlinedTextField(
			value = description,
			onValueChange = { description = it },
			label = { Text("Description") },
			placeholder = { Text("Event details or notes...") },
			modifier = Modifier.fillMaxWidth(),
			minLines = 3
		)

		Button(
			onClick = {
				val eventValues = EventValues(
					title = title,
					location = location,
					description = description,
					startDate = startDate,
					startTime = startTime,
					endDate = endDate,
					endTime = endTime,
					timeZone = timeZone
				)
				val newCard = QRCodeCard(
					title = title.ifBlank { "Calendar Event" },
					type = QRType.EVENT,
					payload = eventValues.toEventPayload()
				)
				onSaveCard(newCard)
			},
			enabled = title.isNotBlank(),
			modifier = Modifier
				.fillMaxWidth()
				.padding(top = 16.dp)
		) {
			Text("Generate Event QR Code")
		}
	}
}
