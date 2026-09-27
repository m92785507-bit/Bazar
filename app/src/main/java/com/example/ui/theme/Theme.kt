package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = ForestGreenPrimaryDark,
    onPrimary = ForestGreenOnPrimaryDark,
    primaryContainer = ForestGreenContainerDark,
    onPrimaryContainer = ForestGreenOnContainerDark,
    secondary = SageSecondaryDark,
    onSecondary = SageOnSecondaryDark,
    secondaryContainer = SageSecondaryContainerDark,
    onSecondaryContainer = SageOnSecondaryContainerDark,
    tertiary = WarmAmberTertiary,
    background = OrganicBackgroundDark,
    surface = OrganicSurfaceDark,
    surfaceVariant = OrganicSurfaceVariantDark,
    onBackground = Color(0xFFE2E4DE),
    onSurface = Color(0xFFE2E4DE),
  )

private val LightColorScheme =
  lightColorScheme(
    primary = ForestGreenPrimary,
    onPrimary = ForestGreenOnPrimary,
    primaryContainer = ForestGreenContainer,
    onPrimaryContainer = ForestGreenOnContainer,
    secondary = SageSecondary,
    onSecondary = SageOnSecondary,
    secondaryContainer = SageSecondaryContainer,
    onSecondaryContainer = SageOnSecondaryContainer,
    tertiary = WarmAmberTertiary,
    onTertiary = WarmAmberOnTertiary,
    background = OrganicBackground,
    surface = OrganicSurface,
    surfaceVariant = OrganicSurfaceVariant,
    outline = OrganicOutline,
    outlineVariant = OrganicOutlineVariant,
    onBackground = OrganicTextDark,
    onSurface = OrganicTextDark,
    onSurfaceVariant = OrganicTextMuted,
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Set dynamicColor to false by default so our custom organic earthy branding is consistently showcased
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
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
