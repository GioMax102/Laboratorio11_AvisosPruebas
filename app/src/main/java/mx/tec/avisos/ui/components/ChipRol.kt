package mx.tec.avisos.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import mx.tec.avisos.domain.Rol
import mx.tec.avisos.ui.theme.AvisosTheme

/**
 * El rol, a la vista. Cada rol tiene su contenedor: alumno el secundario,
 * profesor el terciario —el mismo acento que marca lo "Nuevo"—, para que el
 * rol con más permisos se distinga sin leer.
 *
 * Es una etiqueta, no un botón: por eso es un Surface y no un AssistChip,
 * que en Material es algo que se toca.
 */
@Composable
fun ChipRol(rol: Rol, modifier: Modifier = Modifier) {
    val (fondo, texto) = when (rol) {
        Rol.ALUMNO -> MaterialTheme.colorScheme.secondaryContainer to MaterialTheme.colorScheme.onSecondaryContainer
        Rol.PROFESOR -> MaterialTheme.colorScheme.tertiaryContainer to MaterialTheme.colorScheme.onTertiaryContainer
    }
    Surface(modifier = modifier, color = fondo, contentColor = texto, shape = MaterialTheme.shapes.small) {
        Text(
            text = if (rol == Rol.PROFESOR) "Profesor" else "Alumno",
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ChipRolPreview() {
    AvisosTheme {
        Row { ChipRol(Rol.ALUMNO); ChipRol(Rol.PROFESOR, Modifier.padding(start = 8.dp)) }
    }
}
