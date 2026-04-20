package app.binthere.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val LightColors =
    lightColorScheme(
        primary = BinthereGreen,
        onPrimary = BinthereSand,
        secondary = BinthereAmber,
        onSecondary = BinthereInk,
        background = BinthereSand,
        onBackground = BinthereInk,
        surface = BinthereSand,
        onSurface = BinthereInk,
    )

private val DarkColors =
    darkColorScheme(
        primary = BinthereAmber,
        onPrimary = BinthereInk,
        secondary = BinthereGreen,
        onSecondary = BinthereSand,
        background = BinthereGreenDark,
        onBackground = BinthereSand,
        surface = BinthereGreenDark,
        onSurface = BinthereSand,
    )

@Composable
fun BinthereTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit,
) {
    val colors =
        when {
            dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                val context = LocalContext.current
                if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            }
            darkTheme -> DarkColors
            else -> LightColors
        }
    MaterialTheme(
        colorScheme = colors,
        typography = BinthereTypography,
        content = content,
    )
}
