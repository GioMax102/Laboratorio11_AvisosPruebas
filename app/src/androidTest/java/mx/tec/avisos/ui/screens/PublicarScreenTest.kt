package mx.tec.avisos.ui.screens

import android.net.Uri
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import mx.tec.avisos.ui.state.PublicarUiState
import mx.tec.avisos.ui.theme.AvisosTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * La pantalla de publicar, sola, sin ViewModel: es "tonta" (recibe un estado y
 * avisa con funciones), y por eso se puede dibujar con cualquier estado que la
 * prueba invente. Las pruebas buscan los elementos como lo haría TalkBack: por
 * su texto o por su descripción, no por su posición.
 */
@RunWith(AndroidJUnit4::class)
class PublicarScreenTest {

    @get:Rule
    val compose = createComposeRule()

    private var publicaciones = 0

    private fun mostrar(estado: PublicarUiState) {
        compose.setContent {
            AvisosTheme {
                PublicarScreen(
                    uiState = estado,
                    autor = "ana",
                    onTituloChange = {}, onCuerpoChange = {}, onGaleria = {}, onCamara = {}, onQuitarImagen = {},
                    onPublicar = { publicaciones++ }, onCancelar = {}
                )
            }
        }
    }

    @Test
    fun con_un_titulo_demasiado_corto_no_se_puede_publicar() {
        mostrar(PublicarUiState(titulo = "Ab", cuerpo = "El jueves a las 10:00."))

        compose.onNodeWithText("Publicar").assertIsNotEnabled()
    }

    @Test
    fun con_datos_validos_se_puede_publicar() {
        mostrar(PublicarUiState(titulo = "Examen parcial", cuerpo = "El jueves a las 10:00."))

        compose.onNodeWithText("Publicar").assertIsEnabled().performClick()

        assertEquals(1, publicaciones)
    }

    @Test
    fun mientras_sube_el_boton_dice_en_que_va_y_no_se_puede_tocar() {
        mostrar(PublicarUiState(titulo = "Examen parcial", cuerpo = "El jueves a las 10:00.", etapa = "Subiendo la imagen…"))

        compose.onNodeWithText("Subiendo la imagen…").assertIsDisplayed().assertIsNotEnabled()
    }

    @Test
    fun el_mensaje_del_servidor_se_ve() {
        mostrar(PublicarUiState(titulo = "Examen parcial", cuerpo = "El jueves a las 10:00.", error = "La imagen es demasiado grande"))

        compose.onNodeWithText("La imagen es demasiado grande").assertIsDisplayed()
    }

    @Test
    fun la_imagen_elegida_se_anuncia_y_se_puede_quitar() {
        mostrar(PublicarUiState(titulo = "Examen parcial", cuerpo = "El jueves a las 10:00.", imagen = Uri.parse("content://prueba/foto.jpg")))

        // Sin descripción, TalkBack diría solo "imagen": quien no ve no sabría qué es.
        compose.onNodeWithContentDescription("Imagen del aviso").assertExists()
        compose.onNodeWithText("Quitar").assertIsDisplayed()
    }
}