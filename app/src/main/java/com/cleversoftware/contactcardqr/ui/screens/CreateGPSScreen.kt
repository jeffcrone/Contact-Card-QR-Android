package com.cleversoftware.contactcardqr.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.cleversoftware.contactcardqr.BuildConfig
import com.cleversoftware.contactcardqr.data.GPSValues
import com.cleversoftware.contactcardqr.data.QRCodeCard
import com.cleversoftware.contactcardqr.data.QRType
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun CreateGPSScreen(
	padding: PaddingValues,
	initialValues: GPSValues = GPSValues(),
	onSaveCard: (QRCodeCard) -> Unit = {}
) {
	var title by remember { mutableStateOf(initialValues.title) }
	var latText by remember { mutableStateOf(if (initialValues.latitude != 0.0) initialValues.latitude.toString() else "") }
	var lngText by remember { mutableStateOf(if (initialValues.longitude != 0.0) initialValues.longitude.toString() else "") }

	var inputMode by remember { mutableStateOf(0) } // 0 = Manual, 1 = Pick on Map

	var selectedLatLng by remember {
		mutableStateOf(
			if (initialValues.latitude != 0.0 || initialValues.longitude != 0.0) {
				LatLng(initialValues.latitude, initialValues.longitude)
			} else {
				LatLng(40.7128, -74.0060) // Default New York
			}
		)
	}

	val cameraPositionState = rememberCameraPositionState {
		position = CameraPosition.fromLatLngZoom(selectedLatLng, 12f)
	}

	// Check if API key is not configured yet
	val isApiKeyMissing = try {
		val key = BuildConfig.MAPS_API_KEY
		key.isBlank() || key.contains("YOUR_KEY_HERE") || key == "DEFAULT_KEY"
	} catch (e: Exception) {
		false
	}

	Column(
		modifier = Modifier
			.fillMaxSize()
			.padding(padding)
			.padding(16.dp)
			.verticalScroll(rememberScrollState()),
		verticalArrangement = Arrangement.spacedBy(12.dp)
	) {
		Text(
			text = "GPS Location QR Code",
			style = MaterialTheme.typography.headlineMedium
		)

		OutlinedTextField(
			value = title,
			onValueChange = { title = it },
			label = { Text("Location Name / Title") },
			placeholder = { Text("e.g. My Office or Central Park") },
			modifier = Modifier.fillMaxWidth(),
			singleLine = true
		)

		// Mode Selection: Manual vs Pick on Map
		SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
			SegmentedButton(
				selected = inputMode == 0,
				onClick = { inputMode = 0 },
				shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
			) {
				Text("Manual Entry")
			}
			SegmentedButton(
				selected = inputMode == 1,
				onClick = { inputMode = 1 },
				shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
			) {
				Text("Pick on Map")
			}
		}

		if (inputMode == 0) {
			// Manual Entry Mode
			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.spacedBy(8.dp)
			) {
				OutlinedTextField(
					value = latText,
					onValueChange = { latText = it },
					label = { Text("Latitude") },
					placeholder = { Text("40.7128") },
					keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
					modifier = Modifier.weight(1f),
					singleLine = true
				)

				OutlinedTextField(
					value = lngText,
					onValueChange = { lngText = it },
					label = { Text("Longitude") },
					placeholder = { Text("-74.0060") },
					keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
					modifier = Modifier.weight(1f),
					singleLine = true
				)
			}
		} else {
			// Google Maps Mode
			if (isApiKeyMissing) {
				Card(
					colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
					modifier = Modifier.fillMaxWidth()
				) {
					Column(modifier = Modifier.padding(12.dp)) {
						Text(
							text = "Google Maps API Key Not Set",
							style = MaterialTheme.typography.titleSmall,
							color = MaterialTheme.colorScheme.onSurfaceVariant
						)
						Text(
							text = "To enable interactive Google Maps, add your MAPS_API_KEY in local.properties. You can still enter latitude and longitude manually above.",
							style = MaterialTheme.typography.bodySmall,
							color = MaterialTheme.colorScheme.onSurfaceVariant,
							modifier = Modifier.padding(top = 4.dp)
						)
					}
				}
			}

			Box(
				modifier = Modifier
					.fillMaxWidth()
					.height(280.dp)
			) {
				GoogleMap(
					modifier = Modifier.fillMaxSize(),
					cameraPositionState = cameraPositionState,
					onMapClick = { latLng ->
						selectedLatLng = latLng
						latText = String.format("%.6f", latLng.latitude)
						lngText = String.format("%.6f", latLng.longitude)
					}
				) {
					Marker(
						state = MarkerState(position = selectedLatLng),
						title = title.ifBlank { "Selected Pin" },
						snippet = "Lat: ${selectedLatLng.latitude}, Lng: ${selectedLatLng.longitude}"
					)
				}
			}

			Text(
				text = "Tap anywhere on the map to set coordinates.",
				style = MaterialTheme.typography.bodySmall,
				color = MaterialTheme.colorScheme.onSurfaceVariant
			)

			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.spacedBy(8.dp)
			) {
				OutlinedTextField(
					value = latText,
					onValueChange = { latText = it },
					label = { Text("Latitude") },
					modifier = Modifier.weight(1f),
					singleLine = true
				)
				OutlinedTextField(
					value = lngText,
					onValueChange = { lngText = it },
					label = { Text("Longitude") },
					modifier = Modifier.weight(1f),
					singleLine = true
				)
			}
		}

		val parsedLat = latText.toDoubleOrNull()
		val parsedLng = lngText.toDoubleOrNull()
		val isValid = parsedLat != null && parsedLng != null

		Button(
			onClick = {
				if (isValid) {
					val gpsValues = GPSValues(
						title = title,
						latitude = parsedLat!!,
						longitude = parsedLng!!
					)
					val newCard = QRCodeCard(
						title = title.ifBlank { "GPS Location" },
						type = QRType.GPS,
						payload = gpsValues.toGpsPayload()
					)
					onSaveCard(newCard)
				}
			},
			enabled = isValid,
			modifier = Modifier
				.fillMaxWidth()
				.padding(top = 16.dp)
		) {
			Text("Generate GPS QR Code")
		}
	}
}
