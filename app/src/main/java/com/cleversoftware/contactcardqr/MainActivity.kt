package com.cleversoftware.contactcardqr

import android.content.res.Configuration
import android.graphics.Bitmap
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.w3c.dom.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.produceState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraph.Companion.findStartDestination
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.navigation.compose.currentBackStackEntryAsState

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
		// ProfileRepository setup
		val context = LocalContext.current
		val repository = remember { ProfileRepository(context) }
		val coroutineScope = rememberCoroutineScope()

		// Navigation Controller
		val navController = rememberNavController()

		// Observe saved profile state automatically from DataStore
		val userProfile by repository.profileFlow.collectAsState(initial = ContactProfile())

		// Track current active navigation route
		val navBackStackEntry by navController.currentBackStackEntryAsState()
		val currentRoute = navBackStackEntry?.destination?.route


		// Helper to navigate safely without exploding the backstack
		fun navigateToTab(route: String) {
			navController.navigate(route) {
				popUpTo(navController.graph.findStartDestination().id) {
					saveState = true
				}
				launchSingleTop = true
				restoreState = true
			}
		}


		Scaffold(
			bottomBar = {
				NavigationBar {
					NavigationBarItem(
						selected = currentRoute == "card",
						onClick = { navigateToTab("card") },
						icon = {
							Icon(
								painter = painterResource(R.drawable.ic_qr_code),
								contentDescription = "Card"
							)
						},
						label = { Text("Card") }
					)
					NavigationBarItem(
						selected = currentRoute == "scan",
						onClick = { navigateToTab("scan") },
						icon = {
							Icon(
								painter = painterResource(R.drawable.ic_scan_line),
								contentDescription = "Scan"
							)
						},
						label = { Text("Scan") }
					)
					NavigationBarItem(
						selected = currentRoute == "saved",
						onClick = { navigateToTab("saved") },
						icon = {
							Icon(
								painter = painterResource(R.drawable.ic_bookmark),
								contentDescription = "Saved"
							)
						},
						label = { Text("Saved") }
					)
					NavigationBarItem(
						selected = currentRoute == "settings" || currentRoute == "profile",
						onClick = { navigateToTab("settings") },
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
			// Handle the navigation between screens.
			NavHost(
				navController = navController,
				startDestination = "card"
			) {
				composable("card") {
					MainCard(padding = innerPadding, newQRContent = userProfile.toVCardString())
				}

				composable("scan") {
					Text(
						"Scan Screen",
						modifier = Modifier.padding(innerPadding)
					)
				}

				composable("saved") {
					Text(
						"Saved Screen",
						modifier = Modifier.padding(innerPadding)
					)
				}

				composable("settings") {
					// Passing padding and navigation callback here:
					SettingsScreen(
						padding = innerPadding,
						onNavigateToProfile = {
							navController.navigate("profile") // Triggers navigation to profile.
						}
					)
				}

				composable("profile") {
					// Passing padding, data, and back-navigation callback here:
					ProfileScreen(
						padding = innerPadding,
						initialProfile = userProfile,
						onSaveProfile = { updatedProfile ->
							// Save profile to disk asynchronously
							coroutineScope.launch {
								repository.saveProfile(updatedProfile)
							}

							// Pops profile off back stack, aka returns to Settings.
							navController.popBackStack()
						}
					)
				}
			}
		}
	}
}

@Composable
fun MainCard(padding: PaddingValues, newQRContent: String) {
	// Generate the QR Code bitmap
	val qrBitmap by produceState<Bitmap?>(initialValue = null, key1 = newQRContent) {
		value = withContext(Dispatchers.IO) {
			try {
				val barcodeEncoder = BarcodeEncoder()
				barcodeEncoder.encodeBitmap(newQRContent, BarcodeFormat.QR_CODE, 500, 500)
			} catch (e: Exception) {
				Log.e("MainActivity", "Failed to get QR code", e)
				null
			}
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
		MainCard(padding = PaddingValues(16.dp), "This is a test")
	}
}

@Preview(showSystemUi = true, name = "Full App Layout")
@Composable
fun MainScreenPreview() {
	MainAppContent()
}
