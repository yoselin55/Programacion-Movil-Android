package com.flores.mycar.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import com.flores.mycar.AppContainer
import com.flores.mycar.ui.admin.AdminViewModel
import com.flores.mycar.ui.detalle.DetalleViewModel
import com.flores.mycar.ui.favoritos.FavoritosViewModel
import com.flores.mycar.ui.lista.ListaViewModel
import com.flores.mycar.ui.login.LoginViewModel

/** Crea los ViewModel inyectandoles lo que necesitan desde el AppContainer. */
class MycarViewModelFactory(private val container: AppContainer) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T = when {
        modelClass.isAssignableFrom(AppViewModel::class.java) ->
            AppViewModel(container.authRepository) as T

        modelClass.isAssignableFrom(LoginViewModel::class.java) ->
            LoginViewModel(container.authRepository) as T

        modelClass.isAssignableFrom(ListaViewModel::class.java) ->
            ListaViewModel(container.itemRepository) as T

        modelClass.isAssignableFrom(AdminViewModel::class.java) ->
            AdminViewModel(container.itemRepository) as T

        modelClass.isAssignableFrom(FavoritosViewModel::class.java) ->
            FavoritosViewModel(container.itemRepository) as T

        modelClass.isAssignableFrom(DetalleViewModel::class.java) -> {
            // El id llega como argumento de navegacion y se lee del SavedStateHandle.
            val estado: SavedStateHandle = extras.createSavedStateHandle()
            val id = checkNotNull(estado.get<Int>("id")) { "Falta el argumento id" }
            DetalleViewModel(container.itemRepository, id) as T
        }

        else -> throw IllegalArgumentException("ViewModel desconocido: ${modelClass.name}")
    }
}
