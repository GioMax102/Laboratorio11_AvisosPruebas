package mx.tec.avisos.domain

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Las reglas de un aviso, en sus orillas. Un validador casi nunca falla en el
 * medio («Examen parcial»): falla en el 2 contra el 3, en el 60 contra el 61,
 * y en lo que parece texto pero son puros espacios.
 */
class AvisoValidatorTest {

    @Test
    fun un_titulo_de_3_y_uno_de_60_caracteres_son_validos() {
        assertTrue(AvisoValidator.tituloValido("Abc"))
        assertTrue(AvisoValidator.tituloValido("a".repeat(60)))
    }

    @Test
    fun un_titulo_de_2_y_uno_de_61_caracteres_no_son_validos() {
        assertFalse(AvisoValidator.tituloValido("Ab"))
        assertFalse(AvisoValidator.tituloValido("a".repeat(61)))
    }

    @Test
    fun los_espacios_de_las_orillas_no_cuentan() {
        assertFalse(AvisoValidator.tituloValido("     "))
        assertFalse(AvisoValidator.tituloValido("  Ab  "))
    }

    @Test
    fun sin_un_cuerpo_valido_no_se_puede_publicar() {
        assertFalse(AvisoValidator.esValido("Examen parcial", "corto"))
        assertTrue(AvisoValidator.esValido("Examen parcial", "El jueves a las 10:00."))
    }
}