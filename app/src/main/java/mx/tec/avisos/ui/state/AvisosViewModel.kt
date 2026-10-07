package mx.tec.avisos.ui.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import mx.tec.avisos.data.AvisosRepository
import mx.tec.avisos.data.local.AvisosVistosStore
import mx.tec.avisos.domain.Aviso
import retrofit2.HttpException
import java.io.IOException

/**
 * La lista del tablón. Carga una vez y después se queda escuchando: cada
 * aviso nuevo que empuja el servidor entra a la lista sin que nadie recargue.
 *
 * Escuchar cuesta una conexión abierta, y una conexión abierta sin nadie
 * mirando es batería tirada. Por eso `cargar()` la abre y `dejarDeEscuchar()`
 * la cierra, y quien llama a las dos es la pantalla, según su ciclo de vida.
 */
@HiltViewModel
class AvisosViewModel @Inject constructor(
    private val repository: AvisosRepository,
    private val vistos: AvisosVistosStore
) : ViewModel() {

    var avisos by mutableStateOf<UiState<List<Aviso>>>(UiState.Cargando)
        private set

    private var escucha: Job? = null

    /** Carga el tablón y, si cargó, se queda escuchando avisos nuevos hasta que lo cancelen. */
    fun cargar() {
        escucha?.cancel()
        escucha = viewModelScope.launch {
            avisos = UiState.Cargando
            val lista = try {
                repository.obtener()
            } catch (e: IOException) {
                avisos = UiState.Error("No hay conexión. Revisa tu internet.")
                return@launch
            } catch (e: HttpException) {
                avisos = UiState.Error(mensajeDe(e))
                return@launch
            }
            mostrar(lista)

            repository.observar(desde = lista.maxOfOrNull { it.id } ?: 0).collect { nuevo ->
                val actual = (avisos as? UiState.Exito)?.datos ?: emptyList()
                if (actual.none { it.id == nuevo.id }) mostrar(listOf(nuevo) + actual)
            }
        }
    }

    fun dejarDeEscuchar() {
        escucha?.cancel()
        escucha = null
    }

    /** Lo que está en pantalla ya se vio: el worker no tiene por qué notificarlo después. */
    private suspend fun mostrar(lista: List<Aviso>) {
        avisos = UiState.Exito(lista)
        lista.maxOfOrNull { it.id }?.let { vistos.marcar(it) }
    }
}
