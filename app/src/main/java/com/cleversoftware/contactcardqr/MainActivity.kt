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
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cleversoftware.contactcardqr.ui.theme.ContactCardQRTheme
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FabPosition
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.asImageBitmap
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.produceState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraph.Companion.findStartDestination
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.CircularProgressIndicator
import com.cleversoftware.contactcardqr.data.AppDatabase
import com.cleversoftware.contactcardqr.data.ContactProfile
import com.cleversoftware.contactcardqr.data.ProfileRepository
import com.cleversoftware.contactcardqr.ui.screens.ProfileScreen
import com.cleversoftware.contactcardqr.ui.screens.SettingsScreen
import com.cleversoftware.contactcardqr.ui.screens.MainCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.unit.dp
import com.cleversoftware.contactcardqr.data.QRCodeCard
import com.cleversoftware.contactcardqr.ui.screens.CardListScreen
import com.cleversoftware.contactcardqr.ui.screens.CreateContactScreen
import com.cleversoftware.contactcardqr.ui.screens.CreateEventScreen
import com.cleversoftware.contactcardqr.ui.screens.CreateUrlScreen
import com.cleversoftware.contactcardqr.ui.screens.CreateWifiScreen

class MainActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()
		setContent {
			MainAppContent()
		}
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppContent() {
	ContactCardQRTheme {
		// ProfileRepository setup
		val context = LocalContext.current
		val repository = remember { ProfileRepository(context) }
		val coroutineScope = rememberCoroutineScope()

		// Room Database setup
		val db = remember { AppDatabase.getDatabase(context) }
		val qrDao = db.qrCodeDao()

		// Observe saved cards list automatically from Room
		val savedCards by qrDao.getAllCards().collectAsState(initial = emptyList())

		// Navigation Controller
		val navController = rememberNavController()

		// Observe saved profile state automatically from DataStore
		val userProfile by repository.profileFlow.collectAsState(initial = ContactProfile())

		// Track current active navigation route
		val navBackStackEntry by navController.currentBackStackEntryAsState()
		val currentRoute = navBackStackEntry?.destination?.route

		var showTypeSelector by remember { mutableStateOf(false) }


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
					onClick = { showTypeSelector = true },
					containerColor = BottomAppBarDefaults.bottomAppBarFabColor
				) {
					Icon(
						painter = painterResource(R.drawable.ic_plus),
						contentDescription = "Add QR Code"
					)
				}
			},
			floatingActionButtonPosition = FabPosition.End
		) { innerPadding ->

			// Shows sheet when showTypeSelector is true
			if (showTypeSelector) {
				ModalBottomSheet(onDismissRequest = { showTypeSelector = false }) {
					Column(modifier = Modifier.padding(16.dp)) {
						Text(
							text = "Create New QR Code",
							style = MaterialTheme.typography.titleLarge,
							modifier = Modifier.padding(bottom = 16.dp)
						)

						ListItem(
							headlineContent = { Text("Contact Card (vCard)") },
							supportingContent = { Text("Share your name, phone, and email") },
							leadingContent = { Icon(painterResource(R.drawable.ic_person), "Contact") },
							modifier = Modifier.clickable {
								showTypeSelector = false
								navController.navigate("create_contact")
							}
						)

						ListItem(
							headlineContent = { Text("Wi-Fi Network") },
							supportingContent = { Text("Allow guests to connect to Wi-Fi instantly") },
							leadingContent = { Icon(painterResource(R.drawable.ic_wifi), "Wi-Fi") },
							modifier = Modifier.clickable {
								showTypeSelector = false
								navController.navigate("create_wifi")
							}
						)

						ListItem(
							headlineContent = { Text("Website Link") },
							supportingContent = { Text("Share a website or social media profile URL") },
							leadingContent = { Icon(painterResource(R.drawable.ic_link), "URL") },
							modifier = Modifier.clickable {
								showTypeSelector = false
								navController.navigate("create_url")
							}
						)

						ListItem(
							headlineContent = { Text("Calendar Event") },
							supportingContent = { Text("Share event dates, times, and locations") },
							leadingContent = { Icon(painterResource(R.drawable.ic_calendar), "Event") },
							modifier = Modifier.clickable {
								showTypeSelector = false
								navController.navigate("create_event")
							}
						)
					}
				}
			}

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
					CardListScreen(
						padding = innerPadding,
						cards = savedCards,
						onCardClick = { card ->
							// TODO: Handle card tap (e.g. view full QR code)
						},
						onDeleteCard = { cardToDelete: QRCodeCard ->
							coroutineScope.launch {
								qrDao.deleteCard(cardToDelete)
							}
						}
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

				composable("create_contact") {
					CreateContactScreen(
						padding = innerPadding,
						// initialProfile = userProfile, // Pre-fills form with user's saved profile if available
						onSaveCard = { newCard ->
							coroutineScope.launch {
								qrDao.insertCard(newCard)
							}
							// Pop back to previous screen
							navController.popBackStack()
						}
					)
				}

				composable("create_wifi") {
					CreateWifiScreen(
						padding = innerPadding,
						onSaveCard = { newCard ->
							coroutineScope.launch {
								qrDao.insertCard(newCard)
							}
							navController.popBackStack()
						}
					)
				}

				composable("create_url") {
					CreateUrlScreen(
						padding = innerPadding,
						onSaveCard = { newCard ->
							coroutineScope.launch {
								qrDao.insertCard(newCard)
							}
							navController.popBackStack()
						}
					)
				}

				composable("create_event") {
					CreateEventScreen(
						padding = innerPadding,
						onSaveCard = { newCard ->
							coroutineScope.launch {
								qrDao.insertCard(newCard)
							}
							navController.popBackStack()
						}
					)
				}
			}
		}
	}
}

// Preview
@Preview(showSystemUi = true, name = "Full App Layout")
@Composable
fun MainScreenPreview() {
	MainAppContent()
}
