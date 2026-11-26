package com.cleversoftware.contactcardqr.ui.screens

import android.content.res.Configuration
import android.graphics.Bitmap
import android.util.Log
import android.util.LruCache
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
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

// In-memory volatile LRU cache for QR Bitmaps (Automatically cleared on low memory/app exit)
private object QrBitmapCache {
	private val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()
	private val cacheSize = maxMemory / 8 // Use 1/8th of available app memory
	private val cache = object : LruCache<String, Bitmap>(cacheSize) {
		override fun sizeOf(key: String, bitmap: Bitmap): Int {
			return bitmap.byteCount / 1024
		}
	}

	fun get(key: String): Bitmap? = cache.get(key)
	fun put(key: String, bitmap: Bitmap) { cache.put(key, bitmap) }
}

@Composable
fun CardListScreen(
	padding: PaddingValues,
	cards: List<QRCodeCard>,
	showQrCodes: Boolean = true,
	onCardClick: (QRCodeCard) -> Unit = {},
	onUpdateCard: (QRCodeCard) -> Unit = {},
	onDeleteCard: (QRCodeCard) -> Unit = {}
) {
	var cardToDelete by remember { mutableStateOf<QRCodeCard?>(null) }

	// Material 3 Delete Card Confirmation Dialog
	cardToDelete?.let { card ->
		AlertDialog(
			onDismissRequest = { cardToDelete = null },
			icon = {
				Icon(
					painter = painterResource(R.drawable.ic_trash),
					contentDescription = "Delete Icon",
					tint = MaterialTheme.colorScheme.error
				)
			},
			title = { Text(text = "Delete QR Code?") },
			text = {
				Text(text = "Are you sure you want to delete \"${card.title}\"? This action cannot be undone.")
			},
			confirmButton = {
				TextButton(
					onClick = {
						onDeleteCard(card)
						cardToDelete = null
					}
				) {
					Text(
						text = "Delete",
						color = MaterialTheme.colorScheme.error
					)
				}
			},
			dismissButton = {
				TextButton(onClick = { cardToDelete = null }) {
					Text(text = "Cancel")
				}
			}
		)
	}

	Column(
		modifier = Modifier
			.fillMaxSize()
			.padding(padding)
	) {
		Text(
			text = "Saved Items",
			style = MaterialTheme.typography.headlineMedium,
			modifier = Modifier.padding(16.dp)
		)

		if (cards.isEmpty()) {
			Box(
				modifier = Modifier.fillMaxSize(),
				contentAlignment = Alignment.Center
			) {
				Text(
					text = "No saved QR codes yet.\nTap + to create one!",
					textAlign = TextAlign.Center,
					style = MaterialTheme.typography.bodyLarge,
					color = MaterialTheme.colorScheme.onSurfaceVariant
				)
			}
		} else {
			LazyColumn(
				modifier = Modifier.fillMaxSize(),
				verticalArrangement = Arrangement.spacedBy(12.dp)
			) {
				items(
					items = cards,
					key = { card -> card.id }
				) { card ->
					InfoCard(
						card = card,
						showQrCode = showQrCodes,
						onClick = { onCardClick(card) },
						onModeChange = { newMode -> onUpdateCard(card.copy(displayMode = newMode)) },
						onDelete = { cardToDelete = card }
					)
				}
			}
		}
	}
}

@Composable
fun InfoCard(
	card: QRCodeCard,
	showQrCode: Boolean = true,
	onClick: () -> Unit = {},
	onModeChange: (CardDisplayMode) -> Unit = {},
	onDelete: () -> Unit = {}
) {
	val iconRes = when (card.type) {
		QRType.CONTACT -> R.drawable.ic_person
		QRType.WIFI -> R.drawable.ic_wifi
		QRType.URL -> R.drawable.ic_link
		QRType.EVENT -> R.drawable.ic_calendar
		else -> R.drawable.ic_qr_code
	}

	val shouldShowQr = showQrCode && card.displayMode != CardDisplayMode.TEXT_ONLY && card.payload.isNotBlank()
	val shouldShowText = card.displayMode != CardDisplayMode.QR_ONLY

	// Lazy load QR Code bitmap asynchronously on Dispatchers.IO with LRU memory caching
	var qrBitmap by remember(card.id, card.payload, shouldShowQr) { mutableStateOf<Bitmap?>(null) }

	if (shouldShowQr) {
		LaunchedEffect(card.id, card.payload) {
			val cached = QrBitmapCache.get(card.id)
			if (cached != null) {
				qrBitmap = cached
			} else {
				withContext(Dispatchers.IO) {
					try {
						val encoder = BarcodeEncoder()
						val bitmap = encoder.encodeBitmap(card.payload, BarcodeFormat.QR_CODE, 200, 200)
						QrBitmapCache.put(card.id, bitmap)
						withContext(Dispatchers.Main) {
							qrBitmap = bitmap
						}
					} catch (e: Exception) {
						Log.e("CardList", "Failed to generate QR for card ${card.id}", e)
					}
				}
			}
		}
	}

	Card(
		modifier = Modifier
			.fillMaxWidth()
			.padding(horizontal = 16.dp)
			.clickable { onClick() },
		elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
		colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
	) {
		Column(
			modifier = Modifier
				.fillMaxWidth()
				.padding(16.dp),
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			Row(
				modifier = Modifier.fillMaxWidth(),
				verticalAlignment = Alignment.CenterVertically
			) {
				Icon(
					painter = painterResource(iconRes),
					contentDescription = card.type.name,
					tint = MaterialTheme.colorScheme.primary,
					modifier = Modifier.padding(end = 12.dp)
				)

				Column(
					modifier = Modifier.weight(1f),
					horizontalAlignment = Alignment.CenterHorizontally
				) {
					Text(
						text = card.title,
						style = MaterialTheme.typography.titleMedium,
						textAlign = TextAlign.Center
					)
					if (shouldShowText) {
						Spacer(modifier = Modifier.height(2.dp))
						Text(
							text = card.type.name,
							style = MaterialTheme.typography.bodyMedium,
							color = MaterialTheme.colorScheme.onSurfaceVariant,
							textAlign = TextAlign.Center
						)
					}
				}

				IconButton(onClick = onDelete) {
					Icon(
						painter = painterResource(R.drawable.ic_trash),
						contentDescription = "Delete Card",
						tint = MaterialTheme.colorScheme.outline
					)
				}
			}

			// Quick Mode Switcher on the Card
			val modes = listOf(
				CardDisplayMode.QR_AND_TEXT to "Both",
				CardDisplayMode.QR_ONLY to "QR Only",
				CardDisplayMode.TEXT_ONLY to "Text Only"
			)

			SingleChoiceSegmentedButtonRow(
				modifier = Modifier
					.fillMaxWidth()
					.padding(top = 10.dp)
			) {
				modes.forEachIndexed { index, (mode, label) ->
					SegmentedButton(
						selected = card.displayMode == mode,
						onClick = { onModeChange(mode) },
						shape = SegmentedButtonDefaults.itemShape(index = index, count = modes.size)
					) {
						Text(label, style = MaterialTheme.typography.labelSmall)
					}
				}
			}

			// Render QR Code Image if setting & card mode permit
			if (shouldShowQr) {
				Spacer(modifier = Modifier.height(12.dp))
				Box(
					modifier = Modifier
						.fillMaxWidth()
						.height(140.dp),
					contentAlignment = Alignment.Center
				) {
					if (qrBitmap != null) {
						Image(
							bitmap = qrBitmap!!.asImageBitmap(),
							contentDescription = "QR Code for ${card.title}",
							modifier = Modifier.size(130.dp)
						)
					} else {
						CircularProgressIndicator(modifier = Modifier.size(24.dp))
					}
				}
			}
		}
	}
}

// Previews
@Preview(showBackground = true, name = "Light Mode")
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Dark Mode")
@Composable
fun CardListScreenPreview() {
	ContactCardQRTheme {
		val sampleCards = listOf(
			QRCodeCard(title = "My Contact Card", type = QRType.CONTACT, payload = "test"),
			QRCodeCard(title = "Home Wi-Fi", type = QRType.WIFI, payload = "test"),
			QRCodeCard(title = "Personal Website", type = QRType.URL, payload = "test")
		)
		CardListScreen(
			padding = PaddingValues(0.dp),
			cards = sampleCards,
			showQrCodes = true
		)
	}
}

@Preview(showBackground = true, name = "Empty State")
@Composable
fun CardListScreenEmptyPreview() {
	ContactCardQRTheme {
		CardListScreen(
			padding = PaddingValues(0.dp),
			cards = emptyList()
		)
	}
}
