package com.flores.mycar

import android.app.Application
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import com.flores.mycar.data.remote.wiki.USER_AGENT
import okhttp3.OkHttpClient

/** Application: guarda el AppContainer y configura como se descargan las fotos. */
class MycarApp : Application(), SingletonImageLoader.Factory {

    val container: AppContainer by lazy { AppContainer(this) }

    // Wikimedia pide que quien descarga sus fotos se identifique con un User-Agent.
    override fun newImageLoader(context: PlatformContext): ImageLoader {
        val cliente = OkHttpClient.Builder()
            .addInterceptor { cadena ->
                cadena.proceed(cadena.request().newBuilder().header("User-Agent", USER_AGENT).build())
            }
            .build()
        return ImageLoader.Builder(context)
            .components { add(OkHttpNetworkFetcherFactory(callFactory = { cliente })) }
            .build()
    }
}
