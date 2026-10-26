package com.cleversoftware.contactcardqr.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.cleversoftware.contactcardqr.data.QRCodeCard
import com.cleversoftware.contactcardqr.data.QRType
import com.cleversoftware.contactcardqr.data.WifiValues

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateWifiScreen(
	padding: PaddingValues,
	initialValues: WifiValues = WifiValues(),
	onSaveCard: (QRCodeCard) -> Unit = {}
) {
	var ssid by remember { mutableStateOf(initialValues.ssid) }
	var password by remember { mutableStateOf(initialValues.password) }
	var securityType by remember { mutableStateOf(initialValues.securityType) }
	var expanded by remember { mutableStateOf(false) }

	Column(
		modifier = Modifier
			.fillMaxSize()
			.padding(padding)
			.padding(16.dp)
			.verticalScroll(rememberScrollState()),
		verticalArrangement = Arrangement.spacedBy(12.dp)
	) {
		Text(
			text = "Wi-Fi QR Code",
			style = MaterialTheme.typography.headlineMedium
		)

		OutlinedTextField(
			value = ssid,
			onValueChange = { ssid = it },
			label = { Text("Network Name (SSID)") },
			modifier = Modifier.fillMaxWidth(),
			singleLine = true
		)

		ExposedDropdownMenuBox(
			expanded = expanded,
			onExpandedChange = { expanded = !expanded }
		) {
			OutlinedTextField(
				value = when (securityType) {
					"nopass" -> "Open (No Password)"
					"WEP" -> "WEP"
					else -> "WPA / WPA2 / WPA3"
				},
				onValueChange = {},
				readOnly = true,
				label = { Text("Security Type") },
				trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
				modifier = Modifier
					.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
					.fillMaxWidth()
			)

			ExposedDropdownMenu(
				expanded = expanded,
				onDismissRequest = { expanded = false }
			) {
				DropdownMenuItem(
					text = { Text("WPA / WPA2 / WPA3") },
					onClick = {
						securityType = "WPA"
						expanded = false
					}
				)
				DropdownMenuItem(
					text = { Text("Open (No Password)") },
					onClick = {
						securityType = "nopass"
						password = ""
						expanded = false
					}
				)
				DropdownMenuItem(
					text = { Text("WEP") },
					onClick = {
						securityType = "WEP"
						expanded = false
					}
				)
			}
		}

		if (securityType != "nopass") {
			OutlinedTextField(
				value = password,
				onValueChange = { password = it },
				label = { Text("Password") },
				visualTransformation = PasswordVisualTransformation(),
				modifier = Modifier.fillMaxWidth(),
				singleLine = true
			)
		}

		Button(
			onClick = {
				val wifiValues = WifiValues(
					ssid = ssid,
					password = if (securityType == "nopass") "" else password,
					securityType = securityType
				)
				val newCard = QRCodeCard(
					title = if (ssid.isBlank()) "Wi-Fi Network" else ssid,
					type = QRType.WIFI,
					payload = wifiValues.toWifiPayload()
				)
				onSaveCard(newCard)
			},
			enabled = ssid.isNotBlank(),
			modifier = Modifier
				.fillMaxWidth()
				.padding(top = 16.dp)
		) {
			Text("Generate Wi-Fi QR Code")
		}
	}
}
