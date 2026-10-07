package mx.tec.avisos.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import mx.tec.avisos.R

/**
 * Dos familias, con trabajos distintos:
 *   - Bricolage Grotesque para lo que se lee de un vistazo: títulos.
 *   - Atkinson Hyperlegible Next para lo que se lee con cuidado: todo lo demás.
 *     Está diseñada para que letras parecidas (l, I, 1; O, 0) no se confundan.
 *
 * Los archivos van en `res/font` —no se descargan— para que la app no dependa
 * de Play Services. Las dos son OFL: se pueden empaquetar; la licencia está en
 * `licencias/`.
 */
val Bricolage = FontFamily(
    Font(R.font.bricolage_semibold, FontWeight.SemiBold),
    Font(R.font.bricolage_bold, FontWeight.Bold)
)

val Atkinson = FontFamily(
    Font(R.font.atkinson_regular, FontWeight.Normal),
    Font(R.font.atkinson_semibold, FontWeight.SemiBold)
)

// Se parte de la escala de Material y se cambia lo que el diseño pide. Los
// tamaños que no se tocan conservan los de Material: son una escala probada.
private val base = Typography()

val TipografiaAvisos = Typography(
    displayLarge = base.displayLarge.copy(fontFamily = Bricolage, fontWeight = FontWeight.Bold),
    displayMedium = base.displayMedium.copy(fontFamily = Bricolage, fontWeight = FontWeight.Bold),
    displaySmall = base.displaySmall.copy(
        fontFamily = Bricolage, fontWeight = FontWeight.Bold, letterSpacing = (-0.5).sp
    ),
    headlineLarge = base.headlineLarge.copy(fontFamily = Bricolage, fontWeight = FontWeight.Bold),
    headlineMedium = base.headlineMedium.copy(fontFamily = Bricolage, fontWeight = FontWeight.SemiBold),
    headlineSmall = base.headlineSmall.copy(fontFamily = Bricolage, fontWeight = FontWeight.SemiBold),
    titleLarge = base.titleLarge.copy(fontFamily = Bricolage, fontWeight = FontWeight.SemiBold),
    titleMedium = base.titleMedium.copy(fontFamily = Atkinson, fontWeight = FontWeight.SemiBold),
    titleSmall = base.titleSmall.copy(fontFamily = Atkinson, fontWeight = FontWeight.SemiBold),
    bodyLarge = base.bodyLarge.copy(fontFamily = Atkinson),
    // 15/22 en vez de 14/20: el cuerpo de un aviso es lo que más se lee en la app.
    bodyMedium = base.bodyMedium.copy(fontFamily = Atkinson, fontSize = 15.sp, lineHeight = 22.sp),
    bodySmall = base.bodySmall.copy(fontFamily = Atkinson),
    labelLarge = base.labelLarge.copy(fontFamily = Atkinson, fontWeight = FontWeight.SemiBold),
    labelMedium = base.labelMedium.copy(fontFamily = Atkinson, fontWeight = FontWeight.SemiBold),
    labelSmall = base.labelSmall.copy(fontFamily = Atkinson, fontWeight = FontWeight.SemiBold)
)
