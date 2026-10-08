package mx.tec.avisos.ui.components

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * `tiempoRelativo` recibe `ahora` como parámetro, y por eso se puede probar.
 * Con `System.currentTimeMillis()` adentro, la misma prueba daría otro
 * resultado cada minuto.
 */
class FormatoTest {

    private val publicado = "2026-10-07 15:00:00"
    private val instante = instanteDe(publicado)!!

    private fun despuesDe(minutos: Long): Long = instante + minutos * 60_000

    @Test
    fun recien_publicado_dice_ahora() {
        assertEquals("ahora", tiempoRelativo(publicado, despuesDe(0)))
    }

    @Test
    fun a_los_12_minutos_dice_hace_12_min() {
        assertEquals("hace 12 min", tiempoRelativo(publicado, despuesDe(12)))
    }

    @Test
    fun a_los_59_minutos_todavia_son_minutos_y_a_los_60_ya_es_una_hora() {
        assertEquals("hace 59 min", tiempoRelativo(publicado, despuesDe(59)))
        assertEquals("hace 1 h", tiempoRelativo(publicado, despuesDe(60)))
    }

    @Test
    fun una_fecha_que_no_se_entiende_no_truena() {
        assertEquals("ahora", tiempoRelativo("ayer a las tres", despuesDe(5)))
    }

    @Test
    fun es_nuevo_hasta_un_minuto_antes_de_cumplir_24_horas() {
        assertTrue(esReciente(publicado, despuesDe(24 * 60 - 1)))
        assertFalse(esReciente(publicado, despuesDe(24 * 60)))
    }
}