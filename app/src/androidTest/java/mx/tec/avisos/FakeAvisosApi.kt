package mx.tec.avisos

import kotlinx.coroutines.CompletableDeferred
import mx.tec.avisos.data.remote.AvisoDto
import mx.tec.avisos.data.remote.AvisosApi
import mx.tec.avisos.data.remote.Credenciales
import mx.tec.avisos.data.remote.ImagenDto
import mx.tec.avisos.data.remote.MeDto
import mx.tec.avisos.data.remote.NuevoAvisoBody
import mx.tec.avisos.data.remote.RefreshBody
import mx.tec.avisos.data.remote.TokensDto
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.HttpException
import retrofit2.Response

/**
 * Un servidor de mentira. No hay red: responde al instante y anota todo lo que
 * la app le pidió, en orden, para que la prueba pueda preguntar «¿qué hizo?».
 *
 * Es posible porque `AvisosApi` es una interfaz: la app depende de lo que la
 * API promete, no de Retrofit. Es la misma idea de la Práctica 9.
 */
class FakeAvisosApi : AvisosApi {

    /** Lo que la app pidió, en orden: "subirImagen", "crearAviso". */
    val llamadas = mutableListOf<String>()

    /** Cada aviso que la app intentó crear, tal como lo mandó. */
    val avisosCreados = mutableListOf<NuevoAvisoBody>()

    /** Si no es null, crearAviso responde con este error en vez de 201. */
    var errorAlCrear: HttpException? = null

    /** Si no es null, crearAviso se queda esperando aquí: la app queda «a media petición». */
    var compuerta: CompletableDeferred<Unit>? = null

    override suspend fun subirImagen(archivo: MultipartBody.Part): ImagenDto {
        llamadas += "subirImagen"
        return ImagenDto(id = "abc.jpg", tipo = "image/jpeg", bytes = 1234)
    }

    override suspend fun crearAviso(body: NuevoAvisoBody): AvisoDto {
        llamadas += "crearAviso"
        avisosCreados += body
        compuerta?.await()
        errorAlCrear?.let { throw it }
        return AvisoDto(
            id = avisosCreados.size,
            titulo = body.titulo,
            cuerpo = body.cuerpo,
            autor = "ana",
            createdAt = "2026-10-07 15:00:00",
            imagen = body.imagen
        )
    }

    // Lo que estas pruebas no usan. Si algo lo llama, que truene: así se nota.
    override suspend fun register(body: Credenciales): TokensDto = error("no se usa en estas pruebas")
    override suspend fun login(body: Credenciales): TokensDto = error("no se usa en estas pruebas")
    override suspend fun refresh(body: RefreshBody): TokensDto = error("no se usa en estas pruebas")
    override suspend fun logout(body: RefreshBody) = error("no se usa en estas pruebas")
    override suspend fun me(): MeDto = error("no se usa en estas pruebas")
    override suspend fun getAvisos(desde: Int): List<AvisoDto> = error("no se usa en estas pruebas")
    override suspend fun borrarAviso(id: Int): Response<Unit> = error("no se usa en estas pruebas")
}

/** Un error como los que manda el servidor: código y cuerpo `{ "error": … }`. */
fun errorDelServidor(codigo: Int, mensaje: String): HttpException =
    HttpException(
        Response.error<Any>(codigo, """{"error":"$mensaje"}""".toResponseBody("application/json".toMediaType()))
    )