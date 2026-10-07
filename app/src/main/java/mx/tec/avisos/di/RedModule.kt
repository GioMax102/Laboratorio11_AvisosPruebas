package mx.tec.avisos.di

import dagger.Lazy
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import mx.tec.avisos.data.SesionRepository
import mx.tec.avisos.data.remote.AuthInterceptor
import mx.tec.avisos.data.remote.AvisosApi
import mx.tec.avisos.data.remote.Network
import mx.tec.avisos.data.remote.TokenAuthenticator
import okhttp3.OkHttpClient
import javax.inject.Singleton

/**
 * Lo que era el `AppContainer` de las prácticas 6 a 8: el cliente y la API.
 * Lo demás (repositorios, stores, notificador) tiene `@Inject constructor` y
 * Hilt sabe construirlo solo.
 */
@Module
@InstallIn(SingletonComponent::class)
object RedModule {

    /**
     * Un solo cliente para toda la app: lo comparten Retrofit y el stream, para
     * que todo vaya firmado y todo se renueve solo.
     *
     * El ciclo de siempre —el cliente necesita el token, que está en el
     * repositorio de sesión, que necesita la API, que necesita el cliente— se
     * rompe con `Lazy`: Hilt entrega una caja vacía y el repositorio se
     * construye la primera vez que alguien llama a `get()`, cuando ya sale la
     * primera petición. Es el `by lazy` del contenedor viejo.
     */
    @Provides
    @Singleton
    fun cliente(sesion: Lazy<SesionRepository>): OkHttpClient =
        Network.crearCliente(
            interceptor = AuthInterceptor { sesion.get().tokenActual() },
            authenticator = TokenAuthenticator(
                tokenActual = { sesion.get().tokenActual() },
                refrescar = { sesion.get().refrescarToken() }
            )
        )

    @Provides
    @Singleton
    fun api(cliente: OkHttpClient): AvisosApi = Network.crearApi(cliente)
}
