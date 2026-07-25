package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

val LocalAppTheme = staticCompositionLocalOf { "default" }

private val DarkColorScheme = darkColorScheme(
    primary = OrangePrimary,
    secondary = GoldYellow,
    tertiary = EmeraldGreen,
    background = DarkBackground,
    surface = DarkSurface,
    onPrimary = DarkBackground,
    onSecondary = DarkBackground,
    onTertiary = DarkBackground,
    onBackground = DarkOnBackground,
    onSurface = DarkOnSurface
)

private val LightColorScheme = lightColorScheme(
    primary = OrangeFlame,
    secondary = GoldYellow,
    tertiary = EmeraldGreen,
    background = LightBackground,
    surface = LightSurface,
    onPrimary = LightBackground,
    onSecondary = LightOnBackground,
    onTertiary = LightBackground,
    onBackground = LightOnBackground,
    onSurface = LightOnSurface
)

private val ElectroColorScheme = darkColorScheme(
    primary = ElectroPrimary,
    secondary = ElectroSecondary,
    tertiary = EmeraldGreen,
    background = ElectroBackground,
    surface = ElectroSurface,
    onPrimary = ElectroBackground,
    onSecondary = ElectroBackground,
    onTertiary = ElectroBackground,
    onBackground = DarkOnBackground,
    onSurface = DarkOnSurface
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    appTheme: String = "default",
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        appTheme == "electro" -> ElectroColorScheme
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    CompositionLocalProvider(LocalAppTheme provides appTheme) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
