package mx.tec.avisos.notificaciones

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import mx.tec.avisos.MainActivity
import mx.tec.avisos.R
import mx.tec.avisos.domain.Aviso

/**
 * Lo único que sabe pintar una notificación. No sabe de dónde vino el aviso:
 * lo mismo lo llama el stream que el worker.
 *
 * Una notificación son tres cosas que suelen olvidarse por separado:
 *   - un CANAL, que existe desde Android 8 y es lo que el usuario apaga o
 *     silencia en Ajustes — por app, por canal;
 *   - el PERMISO, que desde Android 13 se pide en tiempo de ejecución y el
 *     usuario puede negar;
 *   - un PendingIntent, que es lo que pasa al tocarla.
 */
class Notificador @Inject constructor(@ApplicationContext private val context: Context) {

    /** Idempotente: crear un canal que ya existe no hace nada. Se llama al arrancar la app. */
    fun crearCanal() {
        val canal = NotificationChannel(CANAL, "Avisos del tablón", NotificationManager.IMPORTANCE_HIGH).apply {
            description = "Un aviso nuevo publicado por un profesor"
        }
        context.getSystemService(NotificationManager::class.java).createNotificationChannel(canal)
    }

    fun tienePermiso(): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
            PackageManager.PERMISSION_GRANTED

    fun mostrar(aviso: Aviso) {
        // Sin permiso, `notify` no truena: simplemente no se ve. Mejor no llamarlo.
        if (!tienePermiso()) return

        // Al tocarla se abre la app. FLAG_IMMUTABLE: nadie más puede modificar este intent.
        val abrir = PendingIntent.getActivity(
            context,
            0,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notificacion = NotificationCompat.Builder(context, CANAL)
            .setSmallIcon(R.drawable.ic_aviso)
            .setContentTitle(aviso.titulo)
            .setContentText(aviso.cuerpo)
            .setStyle(NotificationCompat.BigTextStyle().bigText(aviso.cuerpo))
            .setSubText(aviso.autor)
            .setContentIntent(abrir)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        // El id es el del aviso: el mismo aviso dos veces reemplaza, no duplica.
        NotificationManagerCompat.from(context).notify(aviso.id, notificacion)
    }

    private companion object {
        const val CANAL = "avisos"
    }
}
