package mx.tec.avisos.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Cinco radios, y cada componente usa uno por su TAMAÑO, no por gusto:
 *
 *   extraSmall  6  ·  small  8   chips, etiquetas
 *   medium     12              campos de texto
 *   large      16              banner, botón flotante, marca
 *   extraLarge 24              tarjetas
 *
 * Lo que va redondo del todo —botones principales, avatares— usa
 * `CircleShape`, que no es un token: es "tan redondo como alcance".
 */
val FormasAvisos = Shapes(
    extraSmall = RoundedCornerShape(6.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(24.dp)
)
