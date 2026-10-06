package com.cleversoftware.contactcardqr.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
	primary = snow_accent,
	onPrimary = snow_onAccent,
	secondary = snow_text,
	onSecondary = snow_bg,
	tertiary = snow_muted,
	onTertiary = snow_onTertiary,
	background = snow_bg,
	onBackground = snow_text,
	surface = snow_card,
	onSurface = snow_text
)

private val LightColorScheme = lightColorScheme(
	primary = carbon_accent,
	onPrimary = carbon_onAccent,
	secondary = carbon_text,
	onSecondary = carbon_bg,
	tertiary = carbon_muted,
	onTertiary = carbon_bg,
	background = carbon_bg,
	onBackground = carbon_text,
	surface = carbon_card,
	onSurface = carbon_text
)

@Composable
fun ContactCardQRTheme(
	darkTheme: Boolean = isSystemInDarkTheme(),
	// Dynamic color is available on Android 12+
	dynamicColor: Boolean = true,
	content: @Composable () -> Unit
) {
	val colorScheme = when {
		dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
			val context = LocalContext.current
			if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
		}

		darkTheme -> DarkColorScheme
		else -> LightColorScheme
	}

	MaterialTheme(
		colorScheme = colorScheme,
		typography = Typography,
		content = content
	)
}