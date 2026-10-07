package mx.tec.avisos.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import mx.tec.avisos.R
import mx.tec.avisos.ui.theme.AvisosTheme

/**
 * El campo de texto de la app. Envuelve a `OutlinedTextField` y decide, una
 * vez, lo que antes cada pantalla decidía a su manera:
 *
 *   - la forma (`shapes.medium`, 12 dp, en vez de los 4 de Material);
 *   - el ícono al inicio, si lo hay;
 *   - la línea de abajo: la ayuda a la izquierda, el contador a la derecha, y
 *     si hay error, el error en lugar de la ayuda;
 *   - el ojo para mostrar la contraseña, si el campo es de contraseña.
 *
 * El estado de "¿se ve la contraseña?" vive AQUÍ, no en el ViewModel: es un
 * detalle de cómo se dibuja, no un dato de la app.
 */
@Composable
fun CampoTexto(
    valor: String,
    onValorChange: (String) -> Unit,
    etiqueta: String,
    modifier: Modifier = Modifier,
    icono: ImageVector? = null,
    ayuda: String? = null,
    error: String? = null,
    contador: String? = null,
    esContrasena: Boolean = false,
    lineasMinimas: Int = 1
) {
    var visible by rememberSaveable { mutableStateOf(false) }
    val ocultar = esContrasena && !visible

    OutlinedTextField(
        value = valor,
        onValueChange = onValorChange,
        label = { Text(etiqueta) },
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        singleLine = lineasMinimas == 1,
        minLines = lineasMinimas,
        isError = error != null,
        leadingIcon = icono?.let { { Icon(it, contentDescription = null) } },
        trailingIcon = if (esContrasena) {
            {
                IconButton(onClick = { visible = !visible }) {
                    Icon(
                        painter = painterResource(if (visible) R.drawable.ic_ojo_tachado else R.drawable.ic_ojo),
                        contentDescription = if (visible) "Ocultar contraseña" else "Mostrar contraseña"
                    )
                }
            }
        } else null,
        visualTransformation = if (ocultar) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = if (esContrasena) KeyboardOptions(keyboardType = KeyboardType.Password) else KeyboardOptions.Default,
        supportingText = if (ayuda != null || error != null || contador != null) {
            {
                Row(Modifier.fillMaxWidth()) {
                    Text(error ?: ayuda ?: "", modifier = Modifier.weight(1f))
                    if (contador != null) Text(contador)
                }
            }
        } else null
    )
}

@Preview(showBackground = true)
@Composable
private fun CampoTextoPreview() {
    AvisosTheme {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            CampoTexto("a01234567", {}, "Usuario", icono = Icons.Default.Person, ayuda = "Tu matrícula, en minúsculas")
            CampoTexto("corta", {}, "Contraseña", icono = Icons.Default.Lock, error = "Al menos 8 caracteres", esContrasena = true)
            CampoTexto("Examen parcial", {}, "Título", ayuda = "De 3 a 60 caracteres", contador = "14/60")
        }
    }
}
