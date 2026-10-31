package com.cleversoftware.contactcardqr.ui.screens

import android.content.Context
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.cleversoftware.contactcardqr.data.GPSValues
import com.cleversoftware.contactcardqr.data.QRCodeCard
import com.cleversoftware.contactcardqr.data.QRType
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker

@Composable
fun CreateGPSScreen(
	padding: PaddingValues,
	initialValues: GPSValues = GPSValues(),
	onSaveCard: (QRCodeCard) -> Unit = {}
) {
	val context = LocalContext.current

	var title by remember { mutableStateOf(initialValues.title) }
	var latText by remember { mutableStateOf(if (initialValues.latitude != 0.0) initialValues.latitude.toString() else "40.712800") }
	var lngText by remember { mutableStateOf(if (initialValues.longitude != 0.0) initialValues.longitude.toString() else "-74.006000") }

	var inputMode by remember { mutableStateOf(0) } // 0 = Manual, 1 = Pick on Map

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
			// OpenStreetMap Mode
			Text(
				text = "Tap anywhere on the map to drop a pin:",
				style = MaterialTheme.typography.bodySmall,
				color = MaterialTheme.colorScheme.onSurfaceVariant,
				modifier = Modifier.padding(bottom = 4.dp)
			)

			Card(
				elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
				modifier = Modifier
					.fillMaxWidth()
					.height(260.dp)
			) {
				val initialLat = latText.toDoubleOrNull() ?: 40.7128
				val initialLng = lngText.toDoubleOrNull() ?: -74.0060

				OsmMapView(
					context = context,
					latitude = initialLat,
					longitude = initialLng,
					markerTitle = title.ifBlank { "Selected Location" },
					onLocationPicked = { lat, lng ->
						latText = String.format("%.6f", lat)
						lngText = String.format("%.6f", lng)
					},
					modifier = Modifier.fillMaxSize()
				)
			}

			// Coordinates displayed cleanly BELOW the map
			Row(
				modifier = Modifier
					.fillMaxWidth()
					.padding(top = 8.dp),
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

@Composable
private fun OsmMapView(
	context: Context,
	latitude: Double,
	longitude: Double,
	markerTitle: String,
	onLocationPicked: (Double, Double) -> Unit,
	modifier: Modifier = Modifier
) {
	// Initialize osmdroid user agent configuration once
	remember {
		Configuration.getInstance().userAgentValue = context.packageName
		true
	}

	AndroidView(
		modifier = modifier,
		factory = { ctx ->
			MapView(ctx).apply {
				setTileSource(TileSourceFactory.MAPNIK)
				setMultiTouchControls(true)

				val startPoint = GeoPoint(latitude, longitude)
				controller.setZoom(18.5)
				controller.setCenter(startPoint)

				val marker = Marker(this).apply {
					position = startPoint
					setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
					title = markerTitle
				}
				overlays.add(marker)

				val eventsOverlay = MapEventsOverlay(object : MapEventsReceiver {
					override fun singleTapConfirmedHelper(p: GeoPoint): Boolean {
						marker.position = p
						marker.title = markerTitle
						invalidate()
						onLocationPicked(p.latitude, p.longitude)
						return true
					}

					override fun longPressHelper(p: GeoPoint): Boolean = false
				})
				overlays.add(0, eventsOverlay)
			}
		},
		update = { mapView ->
			val targetPoint = GeoPoint(latitude, longitude)
			val existingMarker = mapView.overlays.filterIsInstance<Marker>().firstOrNull()
			if (existingMarker != null && (existingMarker.position.latitude != latitude || existingMarker.position.longitude != longitude)) {
				existingMarker.position = targetPoint
				existingMarker.title = markerTitle
				mapView.invalidate()
			}
		}
	)
}
