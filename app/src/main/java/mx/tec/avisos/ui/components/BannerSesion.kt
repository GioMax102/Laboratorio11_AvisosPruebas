package mx.tec.avisos.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import mx.tec.avisos.R
import mx.tec.avisos.domain.Rol
import mx.tec.avisos.domain.Sesion
import mx.tec.avisos.ui.theme.AvisosTema
import mx.tec.avisos.ui.theme.AvisosTheme

/**
 * Los tres estados en que puede estar el acceso, y cómo se ve cada uno.
 * Son un tipo aparte de la pantalla: los decide el tiempo, no el usuario.
 */
enum class EstadoAcceso { ACTIVO, POR_RENOVARSE, VENCIDO }

fun estadoAcceso(segundosRestantes: Long): EstadoAcceso = when {
    segundosRestantes <= 0 -> EstadoAcceso.VENCIDO
    segundosRestantes <= 60 -> EstadoAcceso.POR_RENOVARSE
    else -> EstadoAcceso.ACTIVO
}

/**
 * Cuánto le queda al token de acceso, a la vista. En la Práctica 6 era una
 * franja que competía con la lista; aquí es una tarjeta chica con una barra,
 * y su color cambia con el estado —contenedor neutro, terciario, de error—
 * para que el cambio se note sin leer.
 */
@Composable
fun BannerSesion(sesion: Sesion, modifier: Modifier = Modifier) {
    var restantes by remember(sesion.expiraEn) { mutableLongStateOf(sesion.segundosRestantes()) }

    // Un tic por segundo mientras el banner esté en pantalla. Se reinicia solo
    // cuando llega un token nuevo, porque `expiraEn` cambia.
    LaunchedEffect(sesion.expiraEn) {
        while (true) {
            restantes = sesion.segundosRestantes()
            delay(1_000)
        }
    }

    BannerSesion(restantes = restantes, modifier = modifier)
}

/** La versión sin reloj: recibe los segundos. Es la que usan las previews. */
@Composable
fun BannerSesion(restantes: Long, modifier: Modifier = Modifier) {
    val colores = MaterialTheme.colorScheme
    val estado = estadoAcceso(restantes)

    val (fondo, texto, acento) = when (estado) {
        EstadoAcceso.ACTIVO -> Triple(colores.surfaceContainer, colores.onSurface, colores.primary)
        EstadoAcceso.POR_RENOVARSE -> Triple(colores.tertiaryContainer, colores.onTertiaryContainer, colores.onTertiaryContainer)
        EstadoAcceso.VENCIDO -> Triple(colores.errorContainer, colores.onErrorContainer, colores.onErrorContainer)
    }
    val titulo = when (estado) {
        EstadoAcceso.ACTIVO -> "Sesión activa"
        EstadoAcceso.POR_RENOVARSE -> "Sesión por renovarse"
        EstadoAcceso.VENCIDO -> "Acceso vencido"
    }
    val detalle = when (estado) {
        EstadoAcceso.ACTIVO -> "se renueva sola en ${restantes.comoMinutos()}"
        EstadoAcceso.POR_RENOVARSE -> "en ${restantes.comoMinutos()}"
        EstadoAcceso.VENCIDO -> "se renueva al recargar"
    }
    val espaciado = AvisosTema.espaciado

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = fondo,
        contentColor = texto,
        shape = MaterialTheme.shapes.large
    ) {
        Column(
            modifier = Modifier.padding(horizontal = espaciado.lg, vertical = espaciado.md),
            verticalArrangement = Arrangement.spacedBy(espaciado.sm)
        ) {
            // Título y detalle apilados, no lado a lado: con la letra al 200 % en
            // una fila no caben los dos, y el que tiene `weight` se parte en sílabas.
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(R.drawable.ic_reloj),
                    contentDescription = null,
                    tint = acento,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.size(espaciado.md))
                Column(Modifier.weight(1f)) {
                    Text(titulo, style = MaterialTheme.typography.titleSmall)
                    Text(detalle, style = MaterialTheme.typography.bodySmall)
                }
            }
            if (estado != EstadoAcceso.VENCIDO) {
                LinearProgressIndicator(
                    progress = { (restantes / DURACION_ACCESO_S.toFloat()).coerceIn(0f, 1f) },
                    modifier = Modifier.fillMaxWidth(),
                    color = acento,
                    trackColor = acento.copy(alpha = 0.24f),
                    strokeCap = StrokeCap.Round,
                    gapSize = 0.dp,
                    drawStopIndicator = {}
                )
            }
        }
    }
}

/** Lo que dura un token de acceso en la API del curso. */
private const val DURACION_ACCESO_S = 300

private fun Long.comoMinutos(): String = "%d:%02d".format(this / 60, this % 60)

@Preview(showBackground = true)
@Composable
private fun BannerSesionPreview() {
    AvisosTheme {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            BannerSesion(restantes = 252)
            BannerSesion(restantes = 42)
            BannerSesion(restantes = 0)
        }
    }
}

@Preview(showBackground = true, name = "Con sesión")
@Composable
private fun BannerConSesionPreview() {
    AvisosTheme {
        BannerSesion(
            Sesion("a01234567", Rol.ALUMNO, "x", "y", System.currentTimeMillis() / 1000 + 200),
            Modifier.padding(16.dp)
        )
    }
}
