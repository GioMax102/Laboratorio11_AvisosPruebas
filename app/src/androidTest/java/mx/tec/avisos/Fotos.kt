package mx.tec.avisos

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import java.io.File

/**
 * Una foto de verdad, escrita en el caché de la app, del tamaño que la prueba pida.
 * El compresor la abre igual que abriría una de la galería: por su Uri.
 */
fun fotoDePrueba(contexto: Context, ancho: Int, alto: Int): Uri {
    val bitmap = Bitmap.createBitmap(ancho, alto, Bitmap.Config.ARGB_8888).apply { eraseColor(Color.rgb(14, 92, 74)) }
    val archivo = File(contexto.cacheDir, "prueba-${ancho}x$alto.jpg")
    archivo.outputStream().use { bitmap.compress(Bitmap.CompressFormat.JPEG, 90, it) }
    bitmap.recycle()
    return Uri.fromFile(archivo)
}