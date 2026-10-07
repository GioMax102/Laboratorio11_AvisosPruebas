package mx.tec.avisos.data.remote

import javax.inject.Inject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.sse.EventSource
import okhttp3.sse.EventSourceListener
import okhttp3.sse.EventSources
import java.util.concurrent.TimeUnit

/**
 * La conexión que se queda abierta: `GET /avisos/stream`, Server-Sent Events.
 *
 * Retrofit no sirve aquí. Retrofit es "una petición, una respuesta"; esto es
 * una respuesta que nunca termina y va soltando eventos. Por eso se habla
 * directo con OkHttp, con el MISMO cliente: el interceptor firma la petición
 * y el authenticator la renueva si el token venció.
 */
class AvisosStream @Inject constructor(cliente: OkHttpClient) {

    private val json = Json { ignoreUnknownKeys = true }

    // El mismo cliente (interceptor, authenticator), pero con más paciencia:
    // el readTimeout por omisión es de 10 s, y una conexión SSE puede pasar
    // 15 s sin decir nada entre un `: ping` y el siguiente. Con 10 s, OkHttp
    // la daría por muerta y reconectaría en bucle.
    private val clienteLargo: OkHttpClient = cliente.newBuilder()
        .readTimeout(ESPERA_SIN_DATOS_S, TimeUnit.SECONDS)
        .build()

    /**
     * Un Flow de avisos nuevos, a partir de `desde`.
     *
     * Se conecta cuando alguien lo recolecta y se cierra cuando lo cancelan:
     * no existe una conexión sin nadie escuchando. Si el servidor cuelga —lo
     * hace a propósito cuando el token expira— espera unos segundos y vuelve
     * a conectar desde el último aviso que sí llegó.
     */
    fun observar(desde: Int): Flow<AvisoDto> = flow {
        var ultimo = desde
        while (true) {
            emitAll(conectar(ultimo).onEach { ultimo = it.id })
            delay(ESPERA_MS)
        }
    }

    /** Una conexión. Termina —sin error— cuando el servidor la cierra o la red se cae. */
    private fun conectar(desde: Int): Flow<AvisoDto> = callbackFlow {
        val request = Request.Builder()
            .url("${Network.BASE_URL}avisos/stream")
            // El estándar de SSE: "mándame lo que pasó después de este id".
            .header("Last-Event-ID", desde.toString())
            .build()

        val listener = object : EventSourceListener() {
            override fun onEvent(eventSource: EventSource, id: String?, type: String?, data: String) {
                if (type == "aviso") trySend(json.decodeFromString<AvisoDto>(data))
            }

            override fun onClosed(eventSource: EventSource) {
                close()
            }

            override fun onFailure(eventSource: EventSource, t: Throwable?, response: Response?) {
                // Sin red, o el servidor colgó. No es fatal: el Flow de arriba reconecta.
                close()
            }
        }

        val fuente = EventSources.createFactory(clienteLargo).newEventSource(request, listener)
        awaitClose { fuente.cancel() }
    }

    private companion object {
        const val ESPERA_MS = 5_000L
        const val ESPERA_SIN_DATOS_S = 30L
    }
}
