package com.cleversoftware.contactcardqr

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cleversoftware.contactcardqr.ui.theme.ContactCardQRTheme
import com.cleversoftware.contactcardqr.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder

class MainActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()
		setContent {
			MainAppContent()
		}
	}
}

@Composable
fun MainAppContent() {
	ContactCardQRTheme {
		var selectedIndex by remember { mutableIntStateOf(0) }
		Scaffold(
			bottomBar = {
				NavigationBar {
					NavigationBarItem(
						selected = selectedIndex == 0,
						onClick = { selectedIndex = 0 },
						icon = {
							Icon(
								painter = painterResource(R.drawable.ic_qr_code),
								contentDescription = "Card"
							)
						},
						label = { Text("Card") }
					)
					NavigationBarItem(
						selected = selectedIndex == 1,
						onClick = { selectedIndex = 1 },
						icon = {
							Icon(
								painter = painterResource(R.drawable.ic_scan_line),
								contentDescription = "Scan"
							)
						},
						label = { Text("Scan") }
					)
					NavigationBarItem(
						selected = selectedIndex == 2,
						onClick = { selectedIndex = 2 },
						icon = {
							Icon(
								painter = painterResource(R.drawable.ic_bookmark),
								contentDescription = "Saved"
							)
						},
						label = { Text("Saved") }
					)
					NavigationBarItem(
						selected = selectedIndex == 3,
						onClick = { selectedIndex = 3 },
						icon = {
							Icon(
								painter = painterResource(R.drawable.ic_more_horiz),
								contentDescription = "Settings"
							)
						},
						label = { Text("Settings") }
					)
				}
			},
			floatingActionButton = {
				FloatingActionButton(
					onClick = { },
					containerColor = BottomAppBarDefaults.bottomAppBarFabColor
				) {
					Icon(
						painter = painterResource(R.drawable.ic_plus),
						contentDescription = "Add"
					)
				}
			},
			floatingActionButtonPosition = FabPosition.End
		) { innerPadding ->
			when (selectedIndex) {
				0 -> MainCard(padding = innerPadding)
				1 -> Text("Scan Screen", modifier = Modifier.padding(innerPadding))
				2 -> Text("Saved Screen", modifier = Modifier.padding(innerPadding))
				3 -> SettingsScreen(padding = innerPadding)
			}

		}
	}
}

@Composable
fun MainCard(padding: PaddingValues) {
	// Generate the QR Code bitmap
	val qrContent = "This is a test"
	val qrBitmap = remember(qrContent) {
		try {
			val barcodeEncoder = BarcodeEncoder()
			barcodeEncoder.encodeBitmap(qrContent, BarcodeFormat.QR_CODE, 500, 500)
		} catch( e: Exception) {
			Log.e("MainActivity", "Failed to get QR code", e)
			null
		}
	}

	Card(
		colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
		elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
		modifier = Modifier
			.fillMaxWidth()
			.padding(padding)
	) {
		Column(modifier = Modifier.padding(16.dp)) {
			Text(text = "Card Title")
			Text(text = "Scan the QR code below.", modifier = Modifier.padding(top = 4.dp))

			// Show the QR Code
			qrBitmap?.let { bitmap ->
				Image(
					bitmap = bitmap.asImageBitmap(),
					contentDescription = "Contact Card QR Code",
					modifier = Modifier.size(200.dp)
				)
			}
		}
	}
}

// Previews
@Preview(showBackground = true, name = "Main Card Light")
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Main Card Dark")
@Composable
fun MainCardPreview() {
	ContactCardQRTheme {
		MainCard(padding = PaddingValues(16.dp))
	}
}

@Preview(showSystemUi = true, name = "Full App Layout")
@Composable
fun MainScreenPreview() {
	MainAppContent()
}
