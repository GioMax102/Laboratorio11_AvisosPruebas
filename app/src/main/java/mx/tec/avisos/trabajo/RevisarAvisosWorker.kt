package mx.tec.avisos.trabajo

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import mx.tec.avisos.data.AvisosRepository
import mx.tec.avisos.data.SesionRepository
import mx.tec.avisos.data.local.AvisosVistosStore
import mx.tec.avisos.notificaciones.Notificador
import retrofit2.HttpException
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * Lo que corre cuando la app está cerrada: pregunta si hay avisos nuevos desde
 * el último que el usuario vio, y los notifica.
 *
 * No lo lanza la app: lo lanza el SISTEMA, cuando le conviene — con red, con
 * batería, agrupado con el trabajo de otras apps. A cambio, la app no necesita
 * quedarse viva ni tener una conexión abierta. Es el trato de WorkManager.
 *
 * Cada `return` dice algo distinto:
 *   - success: ya está, hasta la próxima vez.
 *   - retry:   no se pudo (sin red); vuelve a intentar con espera creciente.
 *   - failure: no tiene caso reintentar (el servidor dijo que no).
 *
 * `@HiltWorker` + `@AssistedInject`: el contexto y los parámetros los pone
 * WorkManager al crearlo (`@Assisted`); lo demás lo inyecta Hilt.
 */
@HiltWorker
class RevisarAvisosWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val sesionRepository: SesionRepository,
    private val avisosRepository: AvisosRepository,
    private val avisosVistos: AvisosVistosStore,
    private val notificador: Notificador
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        // Sin sesión no hay a quién avisarle. No es un error: es que nadie ha entrado.
        sesionRepository.sesion.first() ?: return Result.success()

        val desde = avisosVistos.ultimoId.first()

        val nuevos = try {
            avisosRepository.obtener(desde)
        } catch (e: IOException) {
            return Result.retry()
        } catch (e: HttpException) {
            return Result.failure()
        }
        if (nuevos.isEmpty()) return Result.success()

        // La primera vez no se notifica nada: marcar y ya. Si no, un usuario
        // recién instalado recibiría cien notificaciones de golpe.
        if (desde > 0) nuevos.forEach { notificador.mostrar(it) }
        avisosVistos.marcar(nuevos.maxOf { it.id })
        return Result.success()
    }

    companion object {

        private const val NOMBRE = "revisar-avisos"

        /** Solo con red. Sin esto, el worker correría en modo avión, fallaría y reintentaría a ciegas. */
        fun restricciones(): Constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        /**
         * Cada 15 minutos —el mínimo que Android permite— y solo con red.
         * `KEEP`: si ya está programado, no se duplica. Se llama al arrancar la app.
         */
        fun programar(context: Context) {
            val peticion = PeriodicWorkRequestBuilder<RevisarAvisosWorker>(15, TimeUnit.MINUTES)
                .setConstraints(restricciones())
                .build()

            WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(NOMBRE, ExistingPeriodicWorkPolicy.KEEP, peticion)
        }
    }
}
