package mx.tec.avisos.ui.components

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

/**
 * Traducir una fecha del servidor a algo que una persona lee es trabajo de la
 * UI, no del dominio: el dominio guarda "2026-09-23 09:48:12", y cómo se dice
 * depende de cuándo y dónde lo lea el usuario.
 *
 * El servidor manda la hora en UTC y sin zona (así la escribe SQLite). Se usa
 * SimpleDateFormat y no java.time porque la app corre desde API 24, y java.time
 * llegó en la 26.
 */
private fun formatoServidor() = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).apply {
    timeZone = TimeZone.getTimeZone("UTC")
}

/** El instante en milisegundos, o null si el texto no es una fecha del servidor. */
fun instanteDe(creadoEn: String): Long? = try {
    formatoServidor().parse(creadoEn)?.time
} catch (e: ParseException) {
    null
}

/** "ahora", "hace 12 min", "hace 3 h", "ayer", "21 sep". */
fun tiempoRelativo(creadoEn: String, ahora: Long = System.currentTimeMillis()): String {
    val instante = instanteDe(creadoEn) ?: return "ahora"
    val minutos = (ahora - instante) / 60_000
    return when {
        minutos < 1 -> "ahora"
        minutos < 60 -> "hace $minutos min"
        minutos < 24 * 60 -> "hace ${minutos / 60} h"
        esAyer(instante, ahora) -> "ayer"
        else -> SimpleDateFormat("d MMM", Locale.forLanguageTag("es-MX")).format(instante).trimEnd('.')
    }
}

/** Menos de 24 horas: merece la etiqueta "Nuevo". */
fun esReciente(creadoEn: String, ahora: Long = System.currentTimeMillis()): Boolean {
    val instante = instanteDe(creadoEn) ?: return true
    return ahora - instante < 24 * 60 * 60 * 1000L
}

/** El mismo día del calendario del usuario, no "hace menos de 24 horas". */
fun esHoy(creadoEn: String, ahora: Long = System.currentTimeMillis()): Boolean {
    val instante = instanteDe(creadoEn) ?: return true
    return mismoDia(instante, ahora)
}

private fun esAyer(instante: Long, ahora: Long): Boolean =
    mismoDia(instante, ahora - 24 * 60 * 60 * 1000L)

private fun mismoDia(a: Long, b: Long): Boolean {
    val ca = Calendar.getInstance().apply { timeInMillis = a }
    val cb = Calendar.getInstance().apply { timeInMillis = b }
    return ca.get(Calendar.YEAR) == cb.get(Calendar.YEAR) &&
        ca.get(Calendar.DAY_OF_YEAR) == cb.get(Calendar.DAY_OF_YEAR)
}
