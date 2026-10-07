package mx.tec.avisos.trabajo

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager

/**
 * Solo existe en builds de debug. Encola UNA ejecución del mismo worker que
 * el sistema corre cada 15 minutos, para no esperar 15 minutos en clase:
 *
 *   adb shell am broadcast -n mx.tec.avisos/.trabajo.RevisarAhoraReceiver
 *
 * En producción nadie lo dispara: lo dispara el sistema, cuando le toca.
 */
class RevisarAhoraReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val peticion = OneTimeWorkRequestBuilder<RevisarAvisosWorker>()
            .setConstraints(RevisarAvisosWorker.restricciones())
            .build()
        WorkManager.getInstance(context).enqueue(peticion)
    }
}
