package com.cleversoftware.contactcardqr.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cleversoftware.contactcardqr.data.CardDisplayMode
import com.cleversoftware.contactcardqr.ui.theme.ContactCardQRTheme

@Composable
fun AppSettingsScreen(
	padding: PaddingValues,
	showSavedQrCodes: Boolean = true,
	onToggleShowSavedQrCodes: (Boolean) -> Unit = {},
	defaultDisplayMode: CardDisplayMode = CardDisplayMode.QR_AND_TEXT,
	onSelectDefaultDisplayMode: (CardDisplayMode) -> Unit = {}
) {
	Column(
		modifier = Modifier
			.fillMaxSize()
			.padding(padding)
			.verticalScroll(rememberScrollState()),
		verticalArrangement = Arrangement.spacedBy(12.dp)
	) {
		Text(
			text = "App Settings",
			style = MaterialTheme.typography.headlineMedium,
			modifier = Modifier.padding(16.dp)
		)

		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = 16.dp),
			horizontalArrangement = Arrangement.SpaceBetween,
			verticalAlignment = Alignment.CenterVertically
		) {
			Column(modifier = Modifier.weight(1f).padding(end = 16.dp)) {
				Text(
					text = "Show QR Codes in Saved Items",
					style = MaterialTheme.typography.titleMedium
				)
				Text(
					text = "Display rendered QR code images on cards in the Saved Items screen.",
					style = MaterialTheme.typography.bodyMedium,
					color = MaterialTheme.colorScheme.onSurfaceVariant
				)
			}

			Switch(
				checked = showSavedQrCodes,
				onCheckedChange = { onToggleShowSavedQrCodes(it) }
			)
		}

		HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

		Column(modifier = Modifier.padding(horizontal = 16.dp)) {
			Text(
				text = "Default Card Display Mode",
				style = MaterialTheme.typography.titleMedium
			)
			Text(
				text = "Default layout style for new cards.",
				style = MaterialTheme.typography.bodyMedium,
				color = MaterialTheme.colorScheme.onSurfaceVariant,
				modifier = Modifier.padding(bottom = 12.dp)
			)

			val modes = listOf(
				CardDisplayMode.QR_AND_TEXT to "Both",
				CardDisplayMode.QR_ONLY to "QR Only",
				CardDisplayMode.TEXT_ONLY to "Text Only"
			)

			SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
				modes.forEachIndexed { index, (mode, label) ->
					SegmentedButton(
						selected = defaultDisplayMode == mode,
						onClick = { onSelectDefaultDisplayMode(mode) },
						shape = SegmentedButtonDefaults.itemShape(index = index, count = modes.size)
					) {
						Text(label)
					}
				}
			}
		}

		HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
	}
}

@Preview(showBackground = true, name = "Light Mode")
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Dark Mode")
@Composable
fun AppSettingsScreenPreview() {
	ContactCardQRTheme {
		AppSettingsScreen(
			padding = PaddingValues(0.dp),
			showSavedQrCodes = true,
			defaultDisplayMode = CardDisplayMode.QR_AND_TEXT
		)
	}
}
