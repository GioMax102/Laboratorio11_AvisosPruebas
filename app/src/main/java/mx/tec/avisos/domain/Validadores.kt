package mx.tec.avisos.domain

/**
 * Las mismas reglas que aplica el servidor. Que vivan también aquí no es
 * duplicar por gusto: el cliente valida para ser amable, el servidor porque
 * no puede confiar.
 */
object CredencialesValidator {

    const val USUARIO_MIN = 3
    const val USUARIO_MAX = 32
    const val PASSWORD_MIN = 8

    private val USUARIO_RE = Regex("^[a-z0-9._-]{$USUARIO_MIN,$USUARIO_MAX}$")

    fun usuarioValido(usuario: String): Boolean = USUARIO_RE.matches(usuario.trim().lowercase())

    fun passwordValida(password: String): Boolean = password.length >= PASSWORD_MIN

    fun sonValidas(usuario: String, password: String): Boolean =
        usuarioValido(usuario) && passwordValida(password)
}

object AvisoValidator {

    const val TITULO_MIN = 3
    const val TITULO_MAX = 60
    const val CUERPO_MIN = 10
    const val CUERPO_MAX = 400

    fun tituloValido(titulo: String): Boolean = titulo.trim().length in TITULO_MIN..TITULO_MAX

    fun cuerpoValido(cuerpo: String): Boolean = cuerpo.trim().length in CUERPO_MIN..CUERPO_MAX

    fun esValido(titulo: String, cuerpo: String): Boolean = tituloValido(titulo) && cuerpoValido(cuerpo)
}
