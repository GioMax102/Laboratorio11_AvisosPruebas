package mx.tec.avisos.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

/**
 * Los colores de la app, por ROL y no por nombre.
 *
 * Nadie en la app escribe `Color(0xFF006B55)`: escribe
 * `MaterialTheme.colorScheme.primary`. Así el mismo código pinta el tema claro
 * y el oscuro, y cambiar la marca es cambiar este archivo.
 *
 * Cada rol viene con su pareja para el texto encima (`primary` / `onPrimary`).
 * La pareja es la que garantiza el contraste: si pones texto `onSurface` sobre
 * `primary`, el sistema ya no te protege.
 *
 * Los valores salen de una paleta tonal generada desde el verde del curso
 * (#0E5C4A), la misma técnica que usa Material Theme Builder.
 */
val ColoresClaros = lightColorScheme(
    primary = Color(0xFF006B55),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFF9EF2D5),
    onPrimaryContainer = Color(0xFF00513F),
    inversePrimary = Color(0xFF82D5BA),

    secondary = Color(0xFF4B635A),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFCDE8DC),
    onSecondaryContainer = Color(0xFF344C43),

    tertiary = Color(0xFF7C5800),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFFFDEA6),
    onTertiaryContainer = Color(0xFF5E4200),

    error = Color(0xFFBA1A1A),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF93000A),

    background = Color(0xFFF5FBF7),
    onBackground = Color(0xFF171D1A),
    surface = Color(0xFFF5FBF7),
    onSurface = Color(0xFF171D1A),
    surfaceVariant = Color(0xFFDBE5DF),
    onSurfaceVariant = Color(0xFF3F4945),
    surfaceTint = Color(0xFF006B55),
    inverseSurface = Color(0xFF2C322F),
    inverseOnSurface = Color(0xFFECF2EE),
    outline = Color(0xFF6F7975),
    outlineVariant = Color(0xFFBFC9C3),
    scrim = Color(0xFF000000),

    surfaceBright = Color(0xFFF5FBF7),
    surfaceDim = Color(0xFFD5DBD7),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFEFF5F1),
    surfaceContainer = Color(0xFFE9EFEB),
    surfaceContainerHigh = Color(0xFFE4EAE5),
    surfaceContainerHighest = Color(0xFFDEE4E0)
)

val ColoresOscuros = darkColorScheme(
    primary = Color(0xFF82D5BA),
    onPrimary = Color(0xFF00382B),
    primaryContainer = Color(0xFF005140),
    onPrimaryContainer = Color(0xFF9EF2D5),
    inversePrimary = Color(0xFF006B55),

    secondary = Color(0xFFB2CCC1),
    onSecondary = Color(0xFF1D352D),
    secondaryContainer = Color(0xFF344C43),
    onSecondaryContainer = Color(0xFFCDE8DC),

    tertiary = Color(0xFFF1BD6C),
    onTertiary = Color(0xFF422C00),
    tertiaryContainer = Color(0xFF5E4200),
    onTertiaryContainer = Color(0xFFFFDEA6),

    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),

    background = Color(0xFF0F1512),
    onBackground = Color(0xFFDEE4E0),
    surface = Color(0xFF0F1512),
    onSurface = Color(0xFFDEE4E0),
    surfaceVariant = Color(0xFF3F4945),
    onSurfaceVariant = Color(0xFFBFC9C3),
    surfaceTint = Color(0xFF82D5BA),
    inverseSurface = Color(0xFFDEE4E0),
    inverseOnSurface = Color(0xFF2C322F),
    outline = Color(0xFF89938E),
    outlineVariant = Color(0xFF3F4945),
    scrim = Color(0xFF000000),

    surfaceBright = Color(0xFF353B38),
    surfaceDim = Color(0xFF0F1512),
    surfaceContainerLowest = Color(0xFF0A0F0D),
    surfaceContainerLow = Color(0xFF171D1A),
    surfaceContainer = Color(0xFF1B211E),
    surfaceContainerHigh = Color(0xFF252B28),
    surfaceContainerHighest = Color(0xFF303633)
)
