package com.flores.mycar.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import com.flores.mycar.AppContainer
import com.flores.mycar.ui.detalle.DetalleViewModel
import com.flores.mycar.ui.lista.ListaViewModel

/** Crea los ViewModel inyectandoles lo que necesitan desde el AppContainer. */
class MycarViewModelFactory(private val container: AppContainer) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T = when {
        modelClass.isAssignableFrom(ListaViewModel::class.java) ->
            ListaViewModel(container.itemRepository) as T

        modelClass.isAssignableFrom(DetalleViewModel::class.java) -> {
            // El id llega como argumento de navegacion y se lee del SavedStateHandle.
            val estado: SavedStateHandle = extras.createSavedStateHandle()
            val id = checkNotNull(estado.get<Int>("id")) { "Falta el argumento id" }
            DetalleViewModel(container.itemRepository, id) as T
        }

        else -> throw IllegalArgumentException("ViewModel desconocido: ${modelClass.name}")
    }
}
