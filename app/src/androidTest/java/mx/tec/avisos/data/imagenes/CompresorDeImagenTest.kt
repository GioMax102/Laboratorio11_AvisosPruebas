package mx.tec.avisos.data.imagenes

import android.graphics.BitmapFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.runBlocking
import mx.tec.avisos.fotoDePrueba
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

/**
 * La prueba que faltaba: ningún mutante del compresor la ponía en rojo, porque
 * nadie comprobaba el tamaño de lo que sale. Se mide decodificando solo las
 * medidas del JPEG que devuelve, sin cargarlo.
 */
@RunWith(AndroidJUnit4::class)
class CompresorDeImagenTest {

    private val contexto = InstrumentationRegistry.getInstrumentation().targetContext
    private val compresor = CompresorDeImagen(contexto)

    private fun medidasDe(jpeg: ByteArray): Pair<Int, Int> {
        val opciones = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeByteArray(jpeg, 0, jpeg.size, opciones)
        return opciones.outWidth to opciones.outHeight
    }

    @Test
    fun una_foto_grande_sale_con_1280_px_en_su_lado_mas_largo() = runBlocking {
        val (ancho, alto) = medidasDe(compresor.comprimir(fotoDePrueba(contexto, 3000, 2000)))

        assertEquals(1280, ancho)
        assertEquals(853, alto)
    }

    @Test
    fun una_foto_chica_no_se_agranda() = runBlocking {
        val (ancho, alto) = medidasDe(compresor.comprimir(fotoDePrueba(contexto, 800, 600)))

        assertEquals(800, ancho)
        assertEquals(600, alto)
    }
}