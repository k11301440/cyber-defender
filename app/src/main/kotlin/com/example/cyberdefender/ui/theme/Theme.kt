package com.example.cyberdefender.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val WindowsXpColorScheme = lightColorScheme(
    primary = XpBlueTitleStart,
    onPrimary = XpTextWhite,
    primaryContainer = XpWindowBg,
    onPrimaryContainer = XpTextDark,
    secondary = XpStartGreen,
    onSecondary = XpTextWhite,
    secondaryContainer = XpPanelBg,
    onSecondaryContainer = XpTextDark,
    tertiary = XpWarningYellow,
    onTertiary = XpTextDark,
    background = XpWindowBg,
    onBackground = XpTextDark,
    surface = XpWindowBg,
    onSurface = XpTextDark,
    surfaceVariant = XpPanelBg,
    onSurfaceVariant = XpTextMuted,
    error = XpAlertRed,
    onError = XpTextWhite
)

@Composable
fun CyberDefenderTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = WindowsXpColorScheme,
        typography = Typography,
        content = content
    )
}
