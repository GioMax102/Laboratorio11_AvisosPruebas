package mx.tec.avisos.notificaciones

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

/**
 * Pide el permiso de notificaciones una vez, al entrar al tablón.
 *
 * No pinta nada. Se pide DESPUÉS de que el usuario entró y ya vio para qué
 * sirve la app —no en el arranque, cuando todavía no sabe qué le van a
 * notificar—. Si dice que no, la app sigue funcionando: solo no avisa.
 */
@Composable
fun PedirPermisoDeNotificaciones() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return

    val context = LocalContext.current
    val pedir = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { }

    LaunchedEffect(Unit) {
        val concedido = ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
            PackageManager.PERMISSION_GRANTED
        if (!concedido) pedir.launch(Manifest.permission.POST_NOTIFICATIONS)
    }
}
