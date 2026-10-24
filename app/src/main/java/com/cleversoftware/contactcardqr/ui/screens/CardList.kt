package com.cleversoftware.contactcardqr.ui.screens

import android.content.res.Configuration
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cleversoftware.contactcardqr.R
import com.cleversoftware.contactcardqr.data.QRCodeCard
import com.cleversoftware.contactcardqr.data.QRType
import com.cleversoftware.contactcardqr.ui.theme.ContactCardQRTheme
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue


@Composable
fun CardListScreen(
	padding: PaddingValues,
	cards: List<QRCodeCard>,
	onCardClick: (QRCodeCard) -> Unit = {},
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

	if (cards.isEmpty()) {
		Box(
			modifier = Modifier
				.fillMaxSize()
				.padding(padding),
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
			contentPadding = padding,
			verticalArrangement = Arrangement.spacedBy(12.dp)
		) {
			items(
				items = cards,
				key = { card -> card.id }
			) { card ->
				InfoCard(
					card = card,
					onClick = { onCardClick(card) },
					onDelete = { cardToDelete = card }
				)
			}
		}
	}

}

@Composable
fun InfoCard(
	card: QRCodeCard,
	onClick: () -> Unit = {},
	onDelete: () -> Unit = {}
) {
	val iconRes = when (card.type) {
		QRType.CONTACT -> R.drawable.ic_person
		QRType.WIFI -> R.drawable.ic_wifi
		QRType.URL -> R.drawable.ic_link
		QRType.EVENT -> R.drawable.ic_calendar
		else -> R.drawable.ic_qr_code
	}

	Card(
		modifier = Modifier
			.fillMaxWidth()
			.padding(horizontal = 16.dp)
			.clickable { onClick() },
		elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
		colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
	) {
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(16.dp),
			verticalAlignment = Alignment.CenterVertically
		) {
			Icon(
				painter = painterResource(iconRes),
				contentDescription = card.type.name,
				tint = MaterialTheme.colorScheme.primary,
				modifier = Modifier.padding(end = 16.dp)
			)

			Column(modifier = Modifier.weight(1f)) {
				Text(
					text = card.title,
					style = MaterialTheme.typography.titleMedium
				)
				Spacer(modifier = Modifier.height(4.dp))
				Text(
					text = card.type.name,
					style = MaterialTheme.typography.bodyMedium,
					color = MaterialTheme.colorScheme.onSurfaceVariant
				)
			}

			IconButton(onClick = onDelete) {
				Icon(
					painter = painterResource(R.drawable.ic_trash),
					contentDescription = "Delete Card",
					tint = MaterialTheme.colorScheme.outline
				)
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
			QRCodeCard(title = "My Contact Card", type = QRType.CONTACT, payload = ""),
			QRCodeCard(title = "Home Wi-Fi", type = QRType.WIFI, payload = ""),
			QRCodeCard(title = "Personal Website", type = QRType.URL, payload = "")
		)
		CardListScreen(
			padding = PaddingValues(16.dp),
			cards = sampleCards
		)
	}
}

@Preview(showBackground = true, name = "Empty State")
@Composable
fun CardListScreenEmptyPreview() {
	ContactCardQRTheme {
		CardListScreen(
			padding = PaddingValues(16.dp),
			cards = emptyList()
		)
	}
}