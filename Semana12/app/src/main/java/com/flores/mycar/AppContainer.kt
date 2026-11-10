package com.flores.mycar

import android.content.Context
import com.flores.mycar.data.auth.AuthRepository
import com.flores.mycar.data.auth.FirebaseAuthRepository
import com.flores.mycar.data.autos.AutoRepository
import com.flores.mycar.data.autos.CatalogoAutoRepository
import com.flores.mycar.data.autos.FirebaseAutoRepository
import com.flores.mycar.data.fichas.FichaAutoRepository
import com.flores.mycar.data.fichas.FichaAutoRepositoryWiki
import com.flores.mycar.data.fichas.LogoMarcaWikidata
import com.flores.mycar.data.fichas.LogoMarcaRepository
import com.flores.mycar.data.local.AppDatabase
import com.flores.mycar.data.prefs.AccesoRepository
import com.flores.mycar.data.prefs.AccesoRepositoryPrefs
import com.flores.mycar.data.prefs.ComprasRepository
import com.flores.mycar.data.prefs.ComprasRepositoryPrefs
import com.flores.mycar.data.prefs.PedidosVistosPrefs
import com.flores.mycar.data.prefs.PedidosVistosRepository
import com.flores.mycar.data.prefs.TemaRepository
import com.flores.mycar.data.prefs.TemaRepositoryPrefs
import com.flores.mycar.data.perfil.FirebasePerfilRepository
import com.flores.mycar.data.perfil.FotoPerfilArchivos
import com.flores.mycar.data.perfil.FotoPerfilRepository
import com.flores.mycar.data.perfil.PerfilRepository
import com.flores.mycar.data.remote.RetrofitClient
import com.flores.mycar.data.remote.wiki.WikiClient
import com.flores.mycar.data.repository.ItemRepository
import com.flores.mycar.data.repository.ItemRepositoryImpl
import com.flores.mycar.notification.Notificador
import com.flores.mycar.notification.NotificadorAndroid

/** Crea la API, la base de datos y los repositorios. Los ViewModel lo reciben por su Factory. */
class AppContainer(context: Context) {

    private val database: AppDatabase by lazy { AppDatabase.crear(context) }

    val itemRepository: ItemRepository by lazy {
        ItemRepositoryImpl(RetrofitClient.api, database.itemDao())
    }

    val authRepository: AuthRepository by lazy { FirebaseAuthRepository() }

    // Los autos en venta son las marcas de NHTSA (Room); cada una recibe su foto de Wikipedia.
    val autoRepository: AutoRepository by lazy {
        CatalogoAutoRepository(itemRepository, fichaAutoRepository, FirebaseAutoRepository())
    }

    val fichaAutoRepository: FichaAutoRepository by lazy { FichaAutoRepositoryWiki(WikiClient.es, WikiClient.en) }

    val logoRepository: LogoMarcaRepository by lazy { LogoMarcaWikidata(WikiClient.wikidata, context) }

    val notificador: Notificador by lazy { NotificadorAndroid(context) }

    val temaRepository: TemaRepository by lazy { TemaRepositoryPrefs(context) }

    val accesoRepository: AccesoRepository by lazy { AccesoRepositoryPrefs(context) }

    val comprasRepository: ComprasRepository by lazy { ComprasRepositoryPrefs(context) }

    val pedidosVistosRepository: PedidosVistosRepository by lazy { PedidosVistosPrefs(context) }

    val perfilRepository: PerfilRepository by lazy { FirebasePerfilRepository() }

    val fotoPerfilRepository: FotoPerfilRepository by lazy { FotoPerfilArchivos(context) }
}
