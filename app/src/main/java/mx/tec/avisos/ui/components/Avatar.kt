package mx.tec.avisos.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * La inicial de un usuario en un círculo. Pequeño, pero lo usan la tarjeta y
 * el encabezado: por eso es componente y no dos `Box` parecidos.
 *
 * Los colores llegan como parámetros con valor por omisión DEL TEMA: quien lo
 * usa puede cambiarlos, pero si no dice nada, el sistema decide.
 */
@Composable
fun Avatar(
    nombre: String,
    modifier: Modifier = Modifier,
    tamano: Dp = 32.dp,
    fondo: Color = MaterialTheme.colorScheme.primaryContainer,
    texto: Color = MaterialTheme.colorScheme.onPrimaryContainer
) {
    Box(
        modifier = modifier.size(tamano).background(fondo, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = nombre.take(1).uppercase(),
            style = MaterialTheme.typography.labelLarge,
            color = texto
        )
    }
}
