package mx.tec.avisos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.activity.compose.setContent
import dagger.hilt.android.AndroidEntryPoint
import mx.tec.avisos.ui.navigation.AvisosApp
import mx.tec.avisos.ui.theme.AvisosTheme

/** `@AndroidEntryPoint`: sin esto, `hiltViewModel()` no tiene de dónde sacar los ViewModels. */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // La app dibuja detrás de las barras del sistema, y los íconos de la
        // barra de estado cambian de color con el tema claro u oscuro.
        enableEdgeToEdge()
        setContent { AvisosTheme { AvisosApp() } }
    }
}
