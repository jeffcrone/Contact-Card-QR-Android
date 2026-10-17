package com.cleversoftware.contactcardqr

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen(padding: PaddingValues) {
	LazyColumn(
		modifier = Modifier
			.fillMaxSize()
			.padding(padding)
	) {
		item {
			Text(
				text = "Settings",
				style = MaterialTheme.typography.headlineMedium,
				modifier = Modifier.padding(16.dp)
			)
		}

		item {
			ListItem(
				headlineContent = { Text("Profile Information") },
				supportingContent = { Text("Edit your name, email, and contact details") },
				leadingContent = {
					Icon(
						painter = painterResource(R.drawable.ic_plus),
						contentDescription = "Profile"
					)
				},
				modifier = Modifier.clickable {
					// Handle click
				}
			)
			HorizontalDivider()
		}

		item {
			ListItem(
				headlineContent = { Text("QR Code Design") },
				supportingContent = { Text("Customize colors and style of your QR code") },
				leadingContent = {
					Icon(
						painter = painterResource(R.drawable.ic_qr_code),
						contentDescription = "QR Design"
					)
				},
				modifier = Modifier.clickable {
					// Handle click
				}
			)
			HorizontalDivider()
		}

		item {
			ListItem(
				headlineContent = { Text("About App") },
				supportingContent = { Text("Version 0.1.0") },
				leadingContent = {
					Icon(
						painter = painterResource(R.drawable.ic_more_horiz),
						contentDescription = "About"
					)
				},
				modifier = Modifier.clickable {
					// Handle click
				}
			)
			HorizontalDivider()
		}
	}

}