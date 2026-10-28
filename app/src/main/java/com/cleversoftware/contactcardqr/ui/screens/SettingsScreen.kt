package com.cleversoftware.contactcardqr.ui.screens

import android.content.res.Configuration
import android.util.Log
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cleversoftware.contactcardqr.R
import com.cleversoftware.contactcardqr.ui.theme.ContactCardQRTheme

@Composable
fun SettingsScreen(
	padding: PaddingValues,
	onNavigateToProfile: () -> Unit = {},
	onNavigateToAppSettings: () -> Unit = {}
) {
	val context = LocalContext.current
	val versionName = remember(context) {
		try {
			val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
			packageInfo.versionName ?: "1.0.0"
		} catch (e: Exception) {
			Log.e("SettingsScreen", "Failed to get version name", e)
			"1.0.0"
		}
	}

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
						painter = painterResource(R.drawable.ic_person),
						contentDescription = "Profile"
					)
				},
				modifier = Modifier.clickable {
					onNavigateToProfile()
				}
			)
			HorizontalDivider()
		}

		item {
			ListItem(
				headlineContent = { Text("App Settings") },
				supportingContent = { Text("Customize app behavior and preferences") },
				leadingContent = {
					Icon(
						painter = painterResource(R.drawable.ic_more_horiz),
						contentDescription = "App Settings"
					)
				},
				modifier = Modifier.clickable {
					onNavigateToAppSettings()
				}
			)
			HorizontalDivider()
		}

		item {
			ListItem(
				headlineContent = { Text("About App") },
				supportingContent = { Text("Version $versionName") },
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

@Preview(showBackground = true, name = "Light Mode")
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Dark Mode")
@Composable
fun SettingsScreenPreview() {
	ContactCardQRTheme {
		SettingsScreen(padding = PaddingValues(0.dp))
	}
}
