package mx.tec.avisos.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * El único token que Material NO trae: el espaciado.
 *
 * Seis pasos y nada en medio. Un `13.dp` suelto en una pantalla es una
 * decisión que nadie más va a respetar; `espaciado.md` es una que ya tomó el
 * sistema.
 */
@Immutable
data class Espaciado(
    val xs: Dp = 4.dp,
    val sm: Dp = 8.dp,
    val md: Dp = 12.dp,
    val lg: Dp = 16.dp,
    val xl: Dp = 24.dp,
    val xxl: Dp = 32.dp
)

/**
 * Un CompositionLocal es un valor que baja solo por el árbol de Compose, sin
 * pasarlo de parámetro en parámetro. Es el mismo mecanismo con el que
 * `MaterialTheme.colorScheme` llega a cualquier composable.
 */
val LocalEspaciado = staticCompositionLocalOf { Espaciado() }
