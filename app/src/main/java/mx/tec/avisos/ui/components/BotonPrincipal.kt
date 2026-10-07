package mx.tec.avisos.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import mx.tec.avisos.ui.theme.AvisosTema
import mx.tec.avisos.ui.theme.AvisosTheme

/**
 * LA acción de cada pantalla: Entrar, Publicar. Una por pantalla, nunca dos.
 *
 * Envuelve al `Button` de Material y le fija tres decisiones del sistema:
 *   - 56 dp de alto, en vez de los 40 de Material: es la acción que más se
 *     toca, y va abajo, al alcance del pulgar.
 *   - redondo del todo.
 *   - un estado `cargando` con indicador. Antes cada pantalla lo resolvía
 *     cambiando el texto a "Un momento…"; ahora se resuelve aquí, una vez.
 *
 * Mientras carga, el botón queda deshabilitado: tocarlo otra vez no vuelve a
 * mandar la petición.
 */
@Composable
fun BotonPrincipal(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    habilitado: Boolean = true,
    cargando: Boolean = false
) {
    Button(
        onClick = onClick,
        enabled = habilitado && !cargando,
        shape = CircleShape,
        modifier = modifier.fillMaxWidth().heightIn(min = 56.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AvisosTema.espaciado.md)
        ) {
            if (cargando) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.5.dp,
                    color = LocalContentColor.current
                )
            }
            Text(texto, style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun BotonPrincipalPreview() {
    AvisosTheme {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            BotonPrincipal("Publicar", onClick = {})
            BotonPrincipal("Publicando…", onClick = {}, cargando = true)
            BotonPrincipal("Publicar", onClick = {}, habilitado = false)
        }
    }
}
