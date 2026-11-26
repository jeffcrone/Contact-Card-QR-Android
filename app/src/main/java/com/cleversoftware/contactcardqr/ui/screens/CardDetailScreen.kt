package com.cleversoftware.contactcardqr.ui.screens

import android.content.res.Configuration
import android.graphics.Bitmap
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cleversoftware.contactcardqr.R
import com.cleversoftware.contactcardqr.data.CardDisplayMode
import com.cleversoftware.contactcardqr.data.QRCodeCard
import com.cleversoftware.contactcardqr.data.QRType
import com.cleversoftware.contactcardqr.ui.theme.ContactCardQRTheme
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun CardDetailScreen(
	padding: PaddingValues,
	card: QRCodeCard?,
	onUpdateCard: (QRCodeCard) -> Unit = {}
) {
	if (card == null) {
		Box(
			modifier = Modifier
				.fillMaxSize()
				.padding(padding),
			contentAlignment = Alignment.Center
		) {
			Text(
				text = "Card not found.",
				style = MaterialTheme.typography.bodyLarge,
				color = MaterialTheme.colorScheme.onSurfaceVariant
			)
		}
		return
	}

	// Generate high-res 500x500 QR Code bitmap on background thread
	val qrBitmap by produceState<Bitmap?>(initialValue = null, key1 = card.payload) {
		value = withContext(Dispatchers.IO) {
			try {
				val barcodeEncoder = BarcodeEncoder()
				barcodeEncoder.encodeBitmap(card.payload, BarcodeFormat.QR_CODE, 500, 500)
			} catch (e: Exception) {
				Log.e("CardDetailScreen", "Failed to generate QR bitmap", e)
				null
			}
		}
	}

	val iconRes = when (card.type) {
		QRType.CONTACT -> R.drawable.ic_person
		QRType.WIFI -> R.drawable.ic_wifi
		QRType.URL -> R.drawable.ic_link
		QRType.EVENT -> R.drawable.ic_calendar
		else -> R.drawable.ic_qr_code
	}

	Column(
		modifier = Modifier
			.fillMaxSize()
			.padding(padding)
			.padding(16.dp)
			.verticalScroll(rememberScrollState()),
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.spacedBy(16.dp)
	) {
		// Header Card
		Card(
			colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
			modifier = Modifier.fillMaxWidth()
		) {
			Column(
				modifier = Modifier
					.fillMaxWidth()
					.padding(16.dp),
				horizontalAlignment = Alignment.CenterHorizontally
			) {
				Icon(
					painter = painterResource(iconRes),
					contentDescription = card.type.name,
					tint = MaterialTheme.colorScheme.primary,
					modifier = Modifier.size(36.dp)
				)
				Spacer(modifier = Modifier.height(8.dp))
				Text(
					text = card.title,
					style = MaterialTheme.typography.headlineSmall,
					fontWeight = FontWeight.Bold,
					textAlign = TextAlign.Center
				)
				Text(
					text = "${card.type.name} Card",
					style = MaterialTheme.typography.bodyMedium,
					color = MaterialTheme.colorScheme.onSurfaceVariant,
					textAlign = TextAlign.Center
				)
			}
		}

		// Display Mode Selector for this specific card
		Card(
			colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
			modifier = Modifier.fillMaxWidth()
		) {
			Column(
				modifier = Modifier.padding(16.dp),
				horizontalAlignment = Alignment.CenterHorizontally
			) {
				Text(
					text = "Card Layout Option",
					style = MaterialTheme.typography.titleSmall,
					color = MaterialTheme.colorScheme.onSurfaceVariant,
					modifier = Modifier.padding(bottom = 8.dp)
				)

				val modes = listOf(
					CardDisplayMode.QR_AND_TEXT to "Both",
					CardDisplayMode.QR_ONLY to "QR Only",
					CardDisplayMode.TEXT_ONLY to "Text Only"
				)

				SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
					modes.forEachIndexed { index, (mode, label) ->
						SegmentedButton(
							selected = card.displayMode == mode,
							onClick = { onUpdateCard(card.copy(displayMode = mode)) },
							shape = SegmentedButtonDefaults.itemShape(index = index, count = modes.size)
						) {
							Text(label)
						}
					}
				}
			}
		}

		// Show Large QR Code if mode is QR_AND_TEXT or QR_ONLY
		if (card.displayMode != CardDisplayMode.TEXT_ONLY) {
			Card(
				colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
				elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
				modifier = Modifier.fillMaxWidth()
			) {
				Box(
					modifier = Modifier
						.fillMaxWidth()
						.height(280.dp)
						.padding(24.dp),
					contentAlignment = Alignment.Center
				) {
					if (qrBitmap != null) {
						Image(
							bitmap = qrBitmap!!.asImageBitmap(),
							contentDescription = "QR Code for ${card.title}",
							modifier = Modifier.fillMaxSize()
						)
					} else {
						CircularProgressIndicator()
					}
				}
			}
		}

		// Show Text Details if mode is QR_AND_TEXT or TEXT_ONLY
		if (card.displayMode != CardDisplayMode.QR_ONLY) {
			Card(
				colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
				elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
				modifier = Modifier.fillMaxWidth()
			) {
				Column(
					modifier = Modifier.padding(16.dp),
					horizontalAlignment = Alignment.CenterHorizontally
				) {
					Text(
						text = "Information Details",
						style = MaterialTheme.typography.titleMedium,
						fontWeight = FontWeight.SemiBold,
						color = MaterialTheme.colorScheme.primary,
						textAlign = TextAlign.Center
					)

					Spacer(modifier = Modifier.height(8.dp))
					HorizontalDivider()
					Spacer(modifier = Modifier.height(12.dp))

					val formattedDetails = formatPayloadDetails(card)
					formattedDetails.forEach { (label, value) ->
						if (value.isNotBlank()) {
							Column(
								modifier = Modifier
									.fillMaxWidth()
									.padding(bottom = 12.dp),
								horizontalAlignment = Alignment.CenterHorizontally
							) {
								Text(
									text = label,
									style = MaterialTheme.typography.labelMedium,
									color = MaterialTheme.colorScheme.onSurfaceVariant,
									textAlign = TextAlign.Center
								)
								Text(
									text = value,
									style = MaterialTheme.typography.bodyLarge,
									textAlign = TextAlign.Center,
									modifier = Modifier.padding(top = 2.dp)
								)
							}
						}
					}
				}
			}
		}
	}
}

// Parses raw payloads into user-friendly Label -> Value pairs
private fun formatPayloadDetails(card: QRCodeCard): List<Pair<String, String>> {
	val details = mutableListOf<Pair<String, String>>()

	when (card.type) {
		QRType.WIFI -> {
			val payload = card.payload
			val ssid = payload.substringAfter("S:", "").substringBefore(";").trim()
			val security = payload.substringAfter("T:", "").substringBefore(";").trim()
			val password = payload.substringAfter("P:", "").substringBefore(";").trim()

			if (ssid.isNotBlank()) details.add("Network Name (SSID)" to ssid)
			if (security.isNotBlank()) details.add("Security" to if (security == "nopass") "Open (No Password)" else security)
			if (password.isNotBlank()) details.add("Password" to password)
		}

		QRType.CONTACT -> {
			val payload = card.payload
			payload.lineSequence().forEach { line ->
				when {
					line.startsWith("FN:") -> details.add("Full Name" to line.removePrefix("FN:").trim())
					line.startsWith("ORG:") -> details.add("Company" to line.removePrefix("ORG:").trim())
					line.startsWith("TITLE:") -> details.add("Job Title" to line.removePrefix("TITLE:").trim())
					line.startsWith("TEL") -> details.add("Phone Number" to line.substringAfter(":").trim())
					line.startsWith("EMAIL") -> details.add("Email" to line.substringAfter(":").trim())
					line.startsWith("URL:") -> details.add("Website" to line.removePrefix("URL:").trim())
				}
			}
		}

		QRType.URL -> {
			details.add("Web Address" to card.payload)
		}

		QRType.EVENT -> {
			val payload = card.payload
			payload.lineSequence().forEach { line ->
				when {
					line.startsWith("SUMMARY:") -> details.add("Event Title" to line.removePrefix("SUMMARY:").trim())
					line.startsWith("LOCATION:") -> details.add("Location" to line.removePrefix("LOCATION:").trim())
					line.startsWith("DESCRIPTION:") -> details.add("Description" to line.removePrefix("DESCRIPTION:").trim())
					line.startsWith("DTSTART") -> details.add("Start Time" to line.substringAfter(":").trim())
					line.startsWith("DTEND") -> details.add("End Time" to line.substringAfter(":").trim())
				}
			}
		}

		QRType.GPS -> {
			val payload = card.payload
			val coords = payload.removePrefix("geo:").substringBefore("?")
			val parts = coords.split(",")
			if (parts.size >= 2) {
				details.add("Latitude" to parts[0].trim())
				details.add("Longitude" to parts[1].trim())
			}
			details.add("Full Geo URI" to card.payload)
		}

		else -> {
			details.add("Payload Content" to card.payload)
		}
	}

	if (details.isEmpty() && card.payload.isNotBlank()) {
		details.add("Raw Content" to card.payload)
	}

	return details
}

@Preview(showBackground = true, name = "Light Mode")
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Dark Mode")
@Composable
fun CardDetailScreenPreview() {
	ContactCardQRTheme {
		val sampleCard = QRCodeCard(
			title = "Home Wi-Fi Network",
			type = QRType.WIFI,
			payload = "WIFI:S:MyRouter;T:WPA;P:MySecretPassword;;",
			displayMode = CardDisplayMode.QR_AND_TEXT
		)
		CardDetailScreen(
			padding = PaddingValues(0.dp),
			card = sampleCard
		)
	}
}
