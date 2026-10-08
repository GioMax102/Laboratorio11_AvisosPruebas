package mx.tec.avisos.ui.state

import androidx.lifecycle.SavedStateHandle
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import mx.tec.avisos.FakeAvisosApi
import mx.tec.avisos.data.AvisosRepository
import mx.tec.avisos.data.ImagenesRepository
import mx.tec.avisos.data.imagenes.CompresorDeImagen
import mx.tec.avisos.data.imagenes.FotosTemporales
import mx.tec.avisos.data.remote.AvisosStream
import mx.tec.avisos.errorDelServidor
import mx.tec.avisos.fotoDePrueba
import okhttp3.OkHttpClient
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * El ViewModel de publicar, armado a mano con un servidor de mentira.
 *
 * Corre en el emulador porque el compresor y las fotos temporales necesitan un
 * Context de verdad. Pero no hay red, ni pantalla, ni Hilt: las piezas se
 * construyen aquí, y por eso se pueden cambiar por las de mentira.
 */
@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class PublicarViewModelTest {

    private val contexto = InstrumentationRegistry.getInstrumentation().targetContext
    private val api = FakeAvisosApi()

    // viewModelScope corre en Dispatchers.Main. En una prueba no hay pantalla que lo
    // atienda: se cambia por uno que ejecuta todo en cuanto se pide.
    @Before
    fun cambiarMain() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun restaurarMain() {
        Dispatchers.resetMain()
    }

    private fun nuevoViewModel(guardado: SavedStateHandle = SavedStateHandle()) = PublicarViewModel(
        avisos = AvisosRepository(api, AvisosStream(OkHttpClient())),
        imagenes = ImagenesRepository(api, CompresorDeImagen(contexto)),
        fotos = FotosTemporales(contexto),
        guardado = guardado
    )

    private fun PublicarViewModel.escribirUnAvisoValido() {
        onTituloChange("Examen parcial")
        onCuerpoChange("El jueves a las 10:00 en el salón de siempre.")
    }

    /** Con imagen, el compresor trabaja en otro hilo: se espera a que el ViewModel termine. */
    private fun PublicarViewModel.esperarQueTermine() {
        val limite = System.currentTimeMillis() + 10_000
        while (uiState.enviando && System.currentTimeMillis() < limite) Thread.sleep(50)
        assertFalse("El ViewModel no terminó en 10 s", uiState.enviando)
    }

    @Test
    fun sin_imagen_se_hace_una_sola_peticion() {
        val viewModel = nuevoViewModel()
        viewModel.escribirUnAvisoValido()
        var publicado = false

        viewModel.publicar { publicado = true }

        assertEquals(true, publicado)
        assertEquals(listOf("crearAviso"), api.llamadas)
        assertNull(api.avisosCreados.single().imagen)
    }

    @Test
    fun con_imagen_primero_sube_y_despues_publica_con_la_clave() {
        val viewModel = nuevoViewModel()
        viewModel.escribirUnAvisoValido()
        viewModel.onImagenElegida(fotoDePrueba(contexto, 800, 600))

        viewModel.publicar { }
        viewModel.esperarQueTermine()

        assertEquals(listOf("subirImagen", "crearAviso"), api.llamadas)
        assertEquals("abc.jpg", api.avisosCreados.single().imagen)
    }

    @Test
    fun tocar_publicar_dos_veces_manda_un_solo_aviso() {
        val viewModel = nuevoViewModel()
        viewModel.escribirUnAvisoValido()
        api.compuerta = CompletableDeferred()   // el primer envío se queda «en el aire»

        viewModel.publicar { }
        viewModel.publicar { }
        api.compuerta?.complete(Unit)

        assertEquals(1, api.avisosCreados.size)
    }

    @Test
    fun si_el_servidor_rechaza_se_muestra_su_mensaje_y_no_se_cierra() {
        val viewModel = nuevoViewModel()
        viewModel.escribirUnAvisoValido()
        api.errorAlCrear = errorDelServidor(413, "La imagen es demasiado grande")
        var publicado = false

        viewModel.publicar { publicado = true }

        assertEquals("La imagen es demasiado grande", viewModel.uiState.error)
        assertEquals(false, publicado)
        assertNull(viewModel.uiState.etapa)
    }

    @Test
    fun si_la_imagen_subio_pero_el_aviso_fallo_la_app_no_dice_que_se_publico() {
        val viewModel = nuevoViewModel()
        viewModel.escribirUnAvisoValido()
        viewModel.onImagenElegida(fotoDePrueba(contexto, 800, 600))
        api.errorAlCrear = errorDelServidor(422, "Esa imagen no existe")
        var publicado = false

        viewModel.publicar { publicado = true }
        viewModel.esperarQueTermine()

        assertEquals(listOf("subirImagen", "crearAviso"), api.llamadas)
        assertEquals(false, publicado)
        assertEquals("Esa imagen no existe", viewModel.uiState.error)
    }

    @Test
    fun lo_que_se_escribio_vuelve_si_android_recrea_el_viewmodel() {
        // El mismo SavedStateHandle en dos ViewModels: es lo que Android hace al recrear
        // la pantalla después de matar el proceso.
        val guardado = SavedStateHandle()
        val antes = nuevoViewModel(guardado)
        antes.escribirUnAvisoValido()

        val despues = nuevoViewModel(guardado)

        assertEquals("Examen parcial", despues.uiState.titulo)
        assertEquals("El jueves a las 10:00 en el salón de siempre.", despues.uiState.cuerpo)
    }
}