package com.cleversoftware.contactcardqr

import android.os.Bundle
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

class MainActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()
		setContent {
			ContactCardQRTheme {
				Scaffold(
					bottomBar = {
						BottomAppBar(
							actions = {
								IconButton(onClick = { }){
									Icon(
										painter = painterResource(R.drawable.ic_qr_code),
										contentDescription = "Card"
									)
								}
								IconButton(onClick = { }){
									Icon(
										painter = painterResource(R.drawable.ic_scan_line),
										contentDescription = "Scan"
									)
								}
								IconButton(onClick = { }){
									Icon(
										painter = painterResource(R.drawable.ic_bookmark),
										contentDescription = "Saved"
									)
								}
								IconButton(onClick = { }){
									Icon(
										painter = painterResource(R.drawable.ic_more_horiz),
										contentDescription = "More"
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
							}
						)
					}
				) { innerPadding ->
					MainCard(padding = innerPadding)
				}
			}
		}
	}
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
	Text(
		text = "Hello $name!",
		modifier = modifier
	)
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
	ContactCardQRTheme {
		Greeting("Android")
	}
}

@Composable
fun MainCard(padding: PaddingValues) {
	Card(
		colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
		elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
		modifier = Modifier
			.fillMaxWidth()
			.padding(padding)
	) {
		Column(modifier = Modifier.padding(16.dp)) {
			Text(text = "Card Title")
			Text(text = "This is the card body description.", modifier = Modifier.padding(top = 4.dp))
		}
	}
}
