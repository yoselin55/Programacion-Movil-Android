package com.flores.mycar.ui.lista

import androidx.lifecycle.ViewModel
import com.flores.mycar.domain.model.Item
import com.flores.mycar.ui.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * ViewModel de la lista. En este hito usa datos de prueba;
 * mas adelante se conecta al Repository (Retrofit + Room).
 */
class ListaViewModel : ViewModel() {

    // Debe declararse ANTES del bloque init: Kotlin inicializa en orden.
    private val datosDePrueba = listOf(
        Item(1, "Toyota", "Marca japonesa", null),
        Item(2, "Honda", "Marca japonesa", null),
        Item(3, "Nissan", "Marca japonesa", null),
        Item(4, "Ford", "Marca estadounidense", null),
        Item(5, "Chevrolet", "Marca estadounidense", null),
        Item(6, "Hyundai", "Marca surcoreana", null),
        Item(7, "Kia", "Marca surcoreana", null),
        Item(8, "BMW", "Marca alemana", null)
    )

    private val _estado = MutableStateFlow<UiState<List<Item>>>(UiState.Cargando)
    val estado: StateFlow<UiState<List<Item>>> = _estado.asStateFlow()

    init {
        cargar()
    }

    fun refrescar() = cargar()

    private fun cargar() {
        _estado.value = UiState.Exito(datosDePrueba)
    }
}
