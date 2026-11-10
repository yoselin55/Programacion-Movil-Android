package com.flores.mycar.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import com.flores.mycar.AppContainer
import com.flores.mycar.ui.admin.AdminViewModel
import com.flores.mycar.ui.admin.CatalogoAdminViewModel
import com.flores.mycar.ui.admin.PromocionesAdminViewModel
import com.flores.mycar.ui.admin.UsuariosViewModel
import com.flores.mycar.ui.autos.AgendarCitaViewModel
import com.flores.mycar.ui.admin.GestionViewModel
import com.flores.mycar.ui.autos.AutoDetalleViewModel
import com.flores.mycar.ui.autos.AutosViewModel
import com.flores.mycar.ui.autos.PedidosViewModel
import com.flores.mycar.ui.carrito.CarritoViewModel
import com.flores.mycar.ui.perfil.PerfilViewModel
import com.flores.mycar.ui.detalle.DetalleViewModel
import com.flores.mycar.ui.favoritos.FavoritosViewModel
import com.flores.mycar.ui.lista.ListaViewModel
import com.flores.mycar.ui.perfil.TemaViewModel
import com.flores.mycar.ui.login.LoginViewModel

/** Crea los ViewModel inyectandoles lo que necesitan desde el AppContainer. */
class MycarViewModelFactory(private val container: AppContainer) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T = when {
        modelClass.isAssignableFrom(AppViewModel::class.java) ->
            AppViewModel(container.authRepository) as T

        modelClass.isAssignableFrom(LoginViewModel::class.java) ->
            LoginViewModel(container.authRepository, container.accesoRepository) as T

        modelClass.isAssignableFrom(ListaViewModel::class.java) ->
            ListaViewModel(container.itemRepository, container.notificador, container.logoRepository, container.autoRepository.desactivados) as T

        modelClass.isAssignableFrom(TemaViewModel::class.java) ->
            TemaViewModel(container.temaRepository) as T

        modelClass.isAssignableFrom(AvisosViewModel::class.java) ->
            AvisosViewModel(container.autoRepository, container.notificador, container.pedidosVistosRepository) as T

        modelClass.isAssignableFrom(CarritoViewModel::class.java) ->
            CarritoViewModel(container.autoRepository, container.comprasRepository) as T

        modelClass.isAssignableFrom(MenuViewModel::class.java) ->
            MenuViewModel(container.comprasRepository, container.fotoPerfilRepository) as T

        modelClass.isAssignableFrom(PerfilViewModel::class.java) ->
            PerfilViewModel(container.perfilRepository, container.fotoPerfilRepository) as T

        modelClass.isAssignableFrom(AutosViewModel::class.java) ->
            AutosViewModel(container.autoRepository, container.comprasRepository, container.logoRepository) as T

        modelClass.isAssignableFrom(PedidosViewModel::class.java) ->
            PedidosViewModel(container.autoRepository) as T

        modelClass.isAssignableFrom(GestionViewModel::class.java) ->
            GestionViewModel(container.autoRepository) as T

        modelClass.isAssignableFrom(AutoDetalleViewModel::class.java) -> {
            val estado: SavedStateHandle = extras.createSavedStateHandle()
            val id = checkNotNull(estado.get<String>("autoId")) { "Falta el argumento autoId" }
            AutoDetalleViewModel(container.autoRepository, container.comprasRepository, id) as T
        }

        modelClass.isAssignableFrom(AdminViewModel::class.java) ->
            AdminViewModel(container.itemRepository, container.notificador, container.autoRepository) as T

        modelClass.isAssignableFrom(CatalogoAdminViewModel::class.java) ->
            CatalogoAdminViewModel(container.autoRepository, container.itemRepository) as T

        modelClass.isAssignableFrom(PromocionesAdminViewModel::class.java) ->
            PromocionesAdminViewModel(container.autoRepository) as T

        modelClass.isAssignableFrom(UsuariosViewModel::class.java) ->
            UsuariosViewModel(container.autoRepository) as T

        modelClass.isAssignableFrom(AgendarCitaViewModel::class.java) -> {
            val estado: SavedStateHandle = extras.createSavedStateHandle()
            val id = checkNotNull(estado.get<String>("autoId")) { "Falta el argumento autoId" }
            AgendarCitaViewModel(container.autoRepository, id) as T
        }

        modelClass.isAssignableFrom(FavoritosViewModel::class.java) ->
            FavoritosViewModel(container.itemRepository, container.logoRepository) as T

        modelClass.isAssignableFrom(DetalleViewModel::class.java) -> {
            // El id llega como argumento de navegacion y se lee del SavedStateHandle.
            val estado: SavedStateHandle = extras.createSavedStateHandle()
            val id = checkNotNull(estado.get<Int>("id")) { "Falta el argumento id" }
            DetalleViewModel(container.itemRepository, container.autoRepository, id) as T
        }

        else -> throw IllegalArgumentException("ViewModel desconocido: ${modelClass.name}")
    }
}
