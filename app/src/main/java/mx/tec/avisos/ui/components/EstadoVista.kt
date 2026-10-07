package mx.tec.avisos.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import mx.tec.avisos.R
import mx.tec.avisos.ui.theme.AvisosTema
import mx.tec.avisos.ui.theme.AvisosTheme

/**
 * Lo que se ve cuando no hay datos que mostrar: cargando, vacío o error.
 *
 * Un `object` con tres composables en vez de tres funciones sueltas: al
 * escribir `EstadoVista.` el IDE te enseña las tres, y se lee como lo que es,
 * una familia. Reemplaza a `CargandoView`, `VacioView` y `ErrorView` de la
 * Práctica 6.
 */
object EstadoVista {

    @Composable
    fun Cargando(modifier: Modifier = Modifier) {
        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator()
        }
    }

    @Composable
    fun Vacio(titulo: String, mensaje: String, modifier: Modifier = Modifier) {
        Contenedor(modifier) {
            Icon(
                painter = painterResource(R.drawable.ic_bandeja),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(40.dp)
            )
            Text(titulo, style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center)
            Text(
                text = mensaje,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }

    @Composable
    fun Error(mensaje: String, onReintentar: () -> Unit, modifier: Modifier = Modifier) {
        Contenedor(modifier) {
            Icon(
                painter = painterResource(R.drawable.ic_sin_conexion),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(40.dp)
            )
            Text(mensaje, style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center)
            FilledTonalButton(onClick = onReintentar) { Text("Reintentar") }
        }
    }

    @Composable
    private fun Contenedor(modifier: Modifier, contenido: @Composable () -> Unit) {
        Column(
            modifier = modifier.fillMaxSize().padding(AvisosTema.espaciado.xxl),
            verticalArrangement = Arrangement.spacedBy(AvisosTema.espaciado.sm, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            contenido()
        }
    }
}

@Preview(showBackground = true, heightDp = 240)
@Composable
private fun VacioPreview() {
    AvisosTheme { EstadoVista.Vacio("Todavía no hay avisos", "Cuando un profesor publique, aparece aquí.") }
}

@Preview(showBackground = true, heightDp = 240)
@Composable
private fun ErrorPreview() {
    AvisosTheme { EstadoVista.Error("No hay conexión. Revisa tu internet.", onReintentar = {}) }
}
