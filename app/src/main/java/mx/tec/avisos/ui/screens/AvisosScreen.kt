package mx.tec.avisos.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import mx.tec.avisos.domain.Aviso
import mx.tec.avisos.domain.Rol
import mx.tec.avisos.domain.Sesion
import mx.tec.avisos.ui.components.Avatar
import mx.tec.avisos.ui.components.BannerSesion
import mx.tec.avisos.ui.components.ChipRol
import mx.tec.avisos.ui.components.EstadoVista
import mx.tec.avisos.ui.components.TarjetaAviso
import mx.tec.avisos.ui.components.esHoy
import mx.tec.avisos.ui.state.UiState
import mx.tec.avisos.ui.theme.AvisosTema
import mx.tec.avisos.ui.theme.AvisosTheme

/**
 * El tablón. Recibe la sesión para el encabezado, el banner y la decisión de
 * mostrar el botón de publicar —que sigue siendo cortesía: el servidor decide.
 *
 * Tres decisiones de diseño que no son de ningún componente, sino de esta
 * pantalla:
 *   - los avisos se agrupan en "Hoy" y "Anteriores";
 *   - el banner entra a la lista y se va con el scroll, en vez de quedarse fijo;
 *   - "Salir" se muda al menú de la cuenta: es una acción rara y destructiva,
 *     no merece un ícono junto a "Recargar".
 */
@Composable
fun AvisosScreen(
    sesion: Sesion,
    avisos: UiState<List<Aviso>>,
    onRecargar: () -> Unit,
    onPublicar: () -> Unit,
    onSalir: () -> Unit,
    modifier: Modifier = Modifier
) {
    val espaciado = AvisosTema.espaciado

    Scaffold(
        modifier = modifier,
        topBar = { EncabezadoTablon(sesion = sesion, onRecargar = onRecargar, onSalir = onSalir) },
        floatingActionButton = {
            if (sesion.puedePublicar) {
                ExtendedFloatingActionButton(
                    onClick = onPublicar,
                    icon = { Icon(Icons.Default.Edit, contentDescription = null) },
                    text = { Text("Publicar") }
                )
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding),
            // Abajo, espacio para que el botón flotante no tape el último aviso.
            contentPadding = PaddingValues(start = espaciado.lg, end = espaciado.lg, top = espaciado.sm, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(espaciado.md)
        ) {
            item { BannerSesion(sesion) }

            when (avisos) {
                is UiState.Cargando -> item { EstadoVista.Cargando(Modifier.fillParentMaxHeight(0.6f)) }

                is UiState.Error -> item {
                    EstadoVista.Error(avisos.mensaje, onReintentar = onRecargar, modifier = Modifier.fillParentMaxHeight(0.6f))
                }

                is UiState.Exito -> if (avisos.datos.isEmpty()) {
                    item {
                        EstadoVista.Vacio(
                            titulo = "Todavía no hay avisos",
                            mensaje = "Cuando un profesor publique, aparece aquí.",
                            modifier = Modifier.fillParentMaxHeight(0.6f)
                        )
                    }
                } else {
                    val (hoy, anteriores) = avisos.datos.partition { esHoy(it.creadoEn) }
                    seccion("Hoy", hoy)
                    seccion("Anteriores", anteriores)
                }
            }
        }
    }
}

/** Un encabezado de sección y sus tarjetas. Si no hay avisos, no hay encabezado. */
private fun LazyListScope.seccion(titulo: String, avisos: List<Aviso>) {
    if (avisos.isEmpty()) return
    item(key = "seccion-$titulo") {
        Text(
            text = titulo,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(top = AvisosTema.espaciado.sm)
        )
    }
    items(avisos, key = { it.id }) { aviso -> TarjetaAviso(aviso) }
}

@Composable
private fun EncabezadoTablon(sesion: Sesion, onRecargar: () -> Unit, onSalir: () -> Unit) {
    val espaciado = AvisosTema.espaciado
    var menuAbierto by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(start = espaciado.lg, end = espaciado.xs, bottom = espaciado.sm)
    ) {
        Row(modifier = Modifier.align(Alignment.End), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onRecargar) {
                Icon(Icons.Default.Refresh, contentDescription = "Recargar")
            }
            Box {
                IconButton(onClick = { menuAbierto = true }) {
                    Avatar(
                        nombre = sesion.usuario,
                        tamano = 36.dp,
                        fondo = MaterialTheme.colorScheme.secondaryContainer,
                        texto = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
                DropdownMenu(expanded = menuAbierto, onDismissRequest = { menuAbierto = false }) {
                    DropdownMenuItem(
                        text = { Text("Salir") },
                        leadingIcon = { Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null) },
                        onClick = {
                            menuAbierto = false
                            onSalir()
                        }
                    )
                }
            }
        }
        Text("Tablón", style = MaterialTheme.typography.displaySmall)
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(espaciado.sm)) {
            Text(
                text = sesion.usuario,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            ChipRol(sesion.rol)
        }
    }
}

private val demoAvisos = listOf(
    Aviso(3, "Examen parcial", "El parcial es el jueves a las 10:00 en el salón de siempre. Traigan lápiz.", "profe.prueba", "2099-01-01 00:00:00"),
    Aviso(1, "Bienvenidos al tablón", "Este aviso lo publicó el servidor al crear la tabla. Los siguientes los publica un profesor desde la app.", "profesor", "2026-09-21 16:51:55")
)

@Preview(showBackground = true, heightDp = 844)
@Composable
private fun TablonPreview() {
    AvisosTheme {
        AvisosScreen(
            sesion = Sesion("profe.prueba", Rol.PROFESOR, "x", "y", System.currentTimeMillis() / 1000 + 252),
            avisos = UiState.Exito(demoAvisos),
            onRecargar = {}, onPublicar = {}, onSalir = {}
        )
    }
}

@Preview(showBackground = true, heightDp = 844, name = "Alumno · oscuro · sin conexión")
@Composable
private fun TablonErrorPreview() {
    AvisosTheme(oscuro = true) {
        AvisosScreen(
            sesion = Sesion("a01234567", Rol.ALUMNO, "x", "y", System.currentTimeMillis() / 1000 + 42),
            avisos = UiState.Error("No hay conexión. Revisa tu internet."),
            onRecargar = {}, onPublicar = {}, onSalir = {}
        )
    }
}
