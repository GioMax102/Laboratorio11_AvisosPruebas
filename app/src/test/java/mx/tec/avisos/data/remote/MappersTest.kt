package mx.tec.avisos.data.remote

import mx.tec.avisos.domain.Rol
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/** Lo que llega del servidor, traducido a lo que la app usa. */
class MappersTest {

    @Test
    fun la_sesion_vence_cuando_dijo_el_servidor() {
        val tokens = TokensDto(accessToken = "a", refreshToken = "r", expiresIn = 300, usuario = "ana", rol = "profesor")

        val sesion = tokens.toSesion(ahora = 1_000)

        assertEquals(1_300L, sesion.expiraEn)
        assertEquals(Rol.PROFESOR, sesion.rol)
    }

    @Test
    fun un_rol_desconocido_es_alumno() {
        val tokens = TokensDto(accessToken = "a", refreshToken = "r", expiresIn = 300, usuario = "ana", rol = "director")

        assertEquals(Rol.ALUMNO, tokens.toSesion(ahora = 0).rol)
    }

    @Test
    fun la_clave_de_la_imagen_se_vuelve_una_direccion_del_servidor() {
        val dto = AvisoDto(id = 1, titulo = "Feria", cuerpo = "El viernes.", autor = "ana", createdAt = "2026-10-07 15:00:00", imagen = "abc.jpg")

        assertEquals("${Network.BASE_URL}imagenes/abc.jpg", dto.toDomain().imagenUrl)
    }

    @Test
    fun sin_imagen_no_hay_direccion() {
        val dto = AvisoDto(id = 1, titulo = "Feria", cuerpo = "El viernes.", autor = "ana", createdAt = "2026-10-07 15:00:00")

        assertNull(dto.toDomain().imagenUrl)
    }
}