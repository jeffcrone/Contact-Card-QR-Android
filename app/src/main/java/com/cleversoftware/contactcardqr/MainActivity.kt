package com.cleversoftware.contactcardqr

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
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
										contentDescription = "QR button."
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
					Greeting(
						name = "Android",
						modifier = Modifier.padding(innerPadding)
					)
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