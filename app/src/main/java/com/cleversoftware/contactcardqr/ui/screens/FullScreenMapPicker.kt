package com.cleversoftware.contactcardqr.ui.screens

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.cleversoftware.contactcardqr.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullScreenMapPicker(
	initialLat: Double,
	initialLng: Double,
	onConfirmLocation: (Double, Double) -> Unit,
	onDismiss: () -> Unit
) {
	val context = LocalContext.current
	val coroutineScope = rememberCoroutineScope()

	var pickedLat by remember { mutableStateOf(if (initialLat != 0.0) initialLat else 40.7128) }
	var pickedLng by remember { mutableStateOf(if (initialLng != 0.0) initialLng else -74.0060) }

	var searchQuery by remember { mutableStateOf("") }
	var isSearching by remember { mutableStateOf(false) }
	var searchError by remember { mutableStateOf<String?>(null) }

	// Target point for map center updates
	var targetGeoPoint by remember { mutableStateOf(GeoPoint(pickedLat, pickedLng)) }

	// Nominatim OpenStreetMap Search Function
	fun searchLocation(query: String) {
		if (query.isBlank()) return
		isSearching = true
		searchError = null

		coroutineScope.launch(Dispatchers.IO) {
			try {
				val encodedQuery = URLEncoder.encode(query, "UTF-8")
				val url = URL("https://nominatim.openstreetmap.org/search?q=$encodedQuery&format=json&limit=1")
				val connection = url.openConnection() as HttpURLConnection
				connection.setRequestProperty("User-Agent", context.packageName)
				connection.connectTimeout = 5000
				connection.readTimeout = 5000

				if (connection.responseCode == 200) {
					val jsonText = connection.inputStream.bufferedReader().use { it.readText() }
					val jsonArray = JSONArray(jsonText)

					if (jsonArray.length() > 0) {
						val resultObj = jsonArray.getJSONObject(0)
						val lat = resultObj.getDouble("lat")
						val lon = resultObj.getDouble("lon")

						withContext(Dispatchers.Main) {
							pickedLat = lat
							pickedLng = lon
							targetGeoPoint = GeoPoint(lat, lon)
							isSearching = false
						}
					} else {
						withContext(Dispatchers.Main) {
							searchError = "No locations found for \"$query\""
							isSearching = false
						}
					}
				} else {
					withContext(Dispatchers.Main) {
						searchError = "Search service unavailable"
						isSearching = false
					}
				}
			} catch (e: Exception) {
				Log.e("FullScreenMapPicker", "Search error", e)
				withContext(Dispatchers.Main) {
					searchError = "Search failed: Check network connection"
					isSearching = false
				}
			}
		}
	}

	Scaffold(
		topBar = {
			TopAppBar(
				title = { Text("Pick Location on Map") },
				navigationIcon = {
					IconButton(onClick = onDismiss) {
						Icon(
							painter = painterResource(R.drawable.ic_more_horiz), // replace with back arrow when available
							contentDescription = "Back"
						)
					}
				}
			)
		}
	) { innerPadding ->
		Box(
			modifier = Modifier
				.fillMaxSize()
				.padding(innerPadding)
		) {
			// Full Screen Interactive OSM Map
			FullOsmMapView(
				context = context,
				targetGeoPoint = targetGeoPoint,
				onLocationPicked = { lat, lng ->
					pickedLat = lat
					pickedLng = lng
				},
				modifier = Modifier.fillMaxSize()
			)

			// Top Search Card Overlay
			Card(
				colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
				elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
				modifier = Modifier
					.fillMaxWidth()
					.padding(16.dp)
					.align(Alignment.TopCenter)
			) {
				Column(modifier = Modifier.padding(12.dp)) {
					Row(
						modifier = Modifier.fillMaxWidth(),
						verticalAlignment = Alignment.CenterVertically
					) {
						OutlinedTextField(
							value = searchQuery,
							onValueChange = { searchQuery = it },
							placeholder = { Text("Search location (e.g. Eiffel Tower)") },
							modifier = Modifier.weight(1f),
							singleLine = true
						)

						Button(
							onClick = { searchLocation(searchQuery) },
							enabled = searchQuery.isNotBlank() && !isSearching,
							modifier = Modifier.padding(start = 8.dp)
						) {
							Text(if (isSearching) "..." else "Search")
						}
					}

					searchError?.let { err ->
						Text(
							text = err,
							style = MaterialTheme.typography.bodySmall,
							color = MaterialTheme.colorScheme.error,
							modifier = Modifier.padding(top = 4.dp)
						)
					}
				}
			}

			// Bottom Confirm Card Overlay
			Card(
				colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
				elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
				modifier = Modifier
					.fillMaxWidth()
					.padding(16.dp)
					.align(Alignment.BottomCenter)
			) {
				Column(
					modifier = Modifier.padding(16.dp),
					horizontalAlignment = Alignment.CenterHorizontally
				) {
					Text(
						text = "Selected Coordinates:",
						style = MaterialTheme.typography.labelMedium,
						color = MaterialTheme.colorScheme.onSurfaceVariant
					)
					Text(
						text = "Lat: ${String.format("%.6f", pickedLat)}, Lng: ${String.format("%.6f", pickedLng)}",
						style = MaterialTheme.typography.bodyLarge
					)

					Button(
						onClick = { onConfirmLocation(pickedLat, pickedLng) },
						modifier = Modifier
							.fillMaxWidth()
							.padding(top = 12.dp)
					) {
						Text("Confirm Location")
					}
				}
			}
		}
	}
}

@Composable
private fun FullOsmMapView(
	context: Context,
	targetGeoPoint: GeoPoint,
	onLocationPicked: (Double, Double) -> Unit,
	modifier: Modifier = Modifier
) {
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

				controller.setZoom(17.0)
				controller.setCenter(targetGeoPoint)

				// Current GPS Location Overlay (Blue Dot)
				val locationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(ctx), this)
				locationOverlay.enableMyLocation()
				overlays.add(locationOverlay)

				// Pin Marker
				val marker = Marker(this).apply {
					position = targetGeoPoint
					setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
					title = "Picked Location"
				}
				overlays.add(marker)

				// Map Tap Listener
				val eventsOverlay = MapEventsOverlay(object : MapEventsReceiver {
					override fun singleTapConfirmedHelper(p: GeoPoint): Boolean {
						marker.position = p
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
			mapView.controller.setCenter(targetGeoPoint)
			mapView.overlays.filterIsInstance<Marker>().firstOrNull()?.apply {
				position = targetGeoPoint
			}
			mapView.invalidate()
		}
	)
}
