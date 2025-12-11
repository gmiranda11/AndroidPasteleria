package cl.pasteleriamilsabores.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = colorPrimary,
    onPrimary = colorOnPrimary,

    secondary = colorSecondary,
    onSecondary = colorOnSecondary,

    tertiary = colorTertiary,
    onTertiary = colorOnPrimary, // Negro sobre terciario también

    background = colorBackground,
    onBackground = colorOnBackground,

    surface = colorSurface,
    onSurface = colorOnSurface,

    surfaceVariant = colorSecondary.copy(alpha = 0.3f),
    onSurfaceVariant = colorOnSurface, // Negro sobre variantes

    error = colorError,
    onError = colorOnError
)

@Composable
fun PasteleriaMilSaboresTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = LightColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}