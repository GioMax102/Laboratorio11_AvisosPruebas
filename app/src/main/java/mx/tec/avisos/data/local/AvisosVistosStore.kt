package mx.tec.avisos.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "avisos_vistos")

/**
 * Hasta qué aviso ya vio el usuario. Es un solo número, y de él depende que
 * el worker no notifique dos veces lo mismo — ni lo que la pantalla ya mostró.
 *
 * Va en su propio archivo, no en el de la sesión: no es un secreto y no tiene
 * por qué pasar por el Keystore.
 */
@Singleton
class AvisosVistosStore @Inject constructor(@ApplicationContext private val context: Context) {

    private val ULTIMO_ID = intPreferencesKey("ultimo_id")

    /** 0 = nunca se ha visto el tablón. */
    val ultimoId: Flow<Int> = context.dataStore.data.map { it[ULTIMO_ID] ?: 0 }

    /** Solo avanza: un id menor al guardado no cambia nada. */
    suspend fun marcar(id: Int) {
        context.dataStore.edit { prefs ->
            if (id > (prefs[ULTIMO_ID] ?: 0)) prefs[ULTIMO_ID] = id
        }
    }
}
