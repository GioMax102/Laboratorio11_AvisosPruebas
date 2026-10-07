package mx.tec.avisos.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import mx.tec.avisos.R
import mx.tec.avisos.domain.CredencialesValidator
import mx.tec.avisos.ui.components.BotonPrincipal
import mx.tec.avisos.ui.components.CampoTexto
import mx.tec.avisos.ui.state.LoginUiState
import mx.tec.avisos.ui.theme.AvisosTema
import mx.tec.avisos.ui.theme.AvisosTheme

/**
 * La pantalla es tonta: recibe el estado y avisa qué tecleó el usuario. No
 * sabe de tokens, ni de red, ni de dónde se guarda nada.
 *
 * Tampoco sabe de colores ni de tamaños: cada pieza sale del sistema. Lo que
 * decide esta pantalla es el ORDEN y la JERARQUÍA — la marca, luego la
 * elección de modo, luego los campos, y la acción principal abajo.
 */
@Composable
fun LoginScreen(
    uiState: LoginUiState,
    onUsuarioChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onCodigoProfesorChange: (String) -> Unit,
    onAlternarModo: () -> Unit,
    onEnviar: () -> Unit,
    modifier: Modifier = Modifier
) {
    val espaciado = AvisosTema.espaciado

    Scaffold(modifier = modifier) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                // Con la letra al 200 % la pantalla no cabe: se desplaza en vez de cortarse.
                .verticalScroll(rememberScrollState())
                .padding(horizontal = espaciado.xl, vertical = espaciado.xxl)
        ) {
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                shape = MaterialTheme.shapes.large,
                modifier = Modifier.size(64.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(painterResource(R.drawable.ic_megafono), contentDescription = null, modifier = Modifier.size(32.dp))
                }
            }
            Spacer(Modifier.height(espaciado.lg))
            Text("Avisos", style = MaterialTheme.typography.displaySmall)
            Text(
                text = "Entra con tu cuenta del curso",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(espaciado.xxl))

            // Entrar o crear cuenta son dos opciones del mismo nivel: eso es un
            // botón segmentado, no un enlace escondido debajo del botón principal.
            SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
                SegmentedButton(
                    selected = !uiState.modoRegistro,
                    onClick = { if (uiState.modoRegistro) onAlternarModo() },
                    shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
                ) { Text("Entrar") }
                SegmentedButton(
                    selected = uiState.modoRegistro,
                    onClick = { if (!uiState.modoRegistro) onAlternarModo() },
                    shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
                ) { Text("Crear cuenta") }
            }
            Spacer(Modifier.height(espaciado.xl))

            Column(verticalArrangement = Arrangement.spacedBy(espaciado.sm)) {
                CampoTexto(
                    valor = uiState.usuario,
                    onValorChange = onUsuarioChange,
                    etiqueta = "Usuario",
                    icono = Icons.Default.Person,
                    ayuda = "Tu matrícula, en minúsculas"
                )
                CampoTexto(
                    valor = uiState.password,
                    onValorChange = onPasswordChange,
                    etiqueta = "Contraseña",
                    icono = Icons.Default.Lock,
                    ayuda = "Al menos ${CredencialesValidator.PASSWORD_MIN} caracteres",
                    esContrasena = true
                )
                if (uiState.modoRegistro) {
                    CampoTexto(
                        valor = uiState.codigoProfesor,
                        onValorChange = onCodigoProfesorChange,
                        etiqueta = "Código de profesor (opcional)",
                        ayuda = "Sin código, la cuenta es de alumno"
                    )
                }
            }

            val error = uiState.error
            if (error != null) {
                Spacer(Modifier.height(espaciado.sm))
                Text(error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
            }

            Spacer(Modifier.height(espaciado.xl))
            BotonPrincipal(
                texto = if (uiState.modoRegistro) "Crear cuenta" else "Entrar",
                onClick = onEnviar,
                habilitado = uiState.puedeEnviar,
                cargando = uiState.enviando
            )
            Spacer(Modifier.height(espaciado.xl))
            Text(
                text = "Tu contraseña se usa una vez y no se guarda en el teléfono.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(showBackground = true, heightDp = 844)
@Composable
private fun LoginPreview() {
    AvisosTheme {
        LoginScreen(
            uiState = LoginUiState(usuario = "a01234567", password = "secreta123"),
            onUsuarioChange = {}, onPasswordChange = {}, onCodigoProfesorChange = {},
            onAlternarModo = {}, onEnviar = {}
        )
    }
}

@Preview(showBackground = true, heightDp = 844, name = "Registro con error · oscuro")
@Composable
private fun RegistroPreview() {
    AvisosTheme(oscuro = true) {
        LoginScreen(
            uiState = LoginUiState(usuario = "a01234567", modoRegistro = true, error = "El usuario \"a01234567\" ya existe"),
            onUsuarioChange = {}, onPasswordChange = {}, onCodigoProfesorChange = {},
            onAlternarModo = {}, onEnviar = {}
        )
    }
}
