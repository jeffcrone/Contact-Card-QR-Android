package com.cleversoftware.contactcardqr.ui.screens

import android.content.res.Configuration
import android.graphics.Bitmap
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cleversoftware.contactcardqr.ui.theme.ContactCardQRTheme
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun MainCard( padding: PaddingValues, newQRContent: String ) {
	// Offload QR Code bitmap generation to Dispatchers.IO background thread
	val qrBitmap by produceState<Bitmap?>(initialValue = null, key1 = newQRContent) {
		value = withContext(Dispatchers.IO) {
			try {
				val barcodeEncoder = BarcodeEncoder()
				barcodeEncoder.encodeBitmap(newQRContent, BarcodeFormat.QR_CODE, 500, 500)
			} catch (e: Exception) {
				Log.e("MainCard", "Failed to generate QR code", e)
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
			.padding(16.dp)
	) {
		Column(
			modifier = Modifier.padding(16.dp),
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			Text(text = "Card Title", style = MaterialTheme.typography.titleMedium)
			Text(
				text = "Scan the QR code below.",
				modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
			)

			// Reserves a fixed square box so layout doesn't shift while loading
			Box(
				modifier = Modifier.size(200.dp),
				contentAlignment = Alignment.Center
			) {
				if (qrBitmap != null) {
					Image(
						bitmap = qrBitmap!!.asImageBitmap(),
						contentDescription = "Contact Card QR Code",
						modifier = Modifier.fillMaxSize()
					)
				} else {
					CircularProgressIndicator()
				}
			}
		}
	}
}

@Preview(showBackground = true, name = "Main Card Light")
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Main Card Dark")
@Composable
fun MainCardPreview() {
	ContactCardQRTheme {
		MainCard(padding = PaddingValues(0.dp), newQRContent = "This is a test")
	}
}