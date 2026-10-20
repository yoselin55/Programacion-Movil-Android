package com.flores.lavacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flores.lavacampus.data.repository.LavaRepository
import com.flores.lavacampus.model.ClienteResumen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

// Lista de estudiantes con su cantidad de pedidos y lo gastado
class ClientesViewModel(repo: LavaRepository) : ViewModel() {

    private val _busqueda = MutableStateFlow("")
    val busqueda: StateFlow<String> = _busqueda.asStateFlow()

    val clientes: StateFlow<List<ClienteResumen>> = combine(repo.observarClientes(), _busqueda) { lista, texto ->
        val q = texto.trim().lowercase()
        if (q.isEmpty()) lista
        else lista.filter { it.nombre.lowercase().contains(q) || it.codigo.lowercase().contains(q) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun buscar(texto: String) {
        _busqueda.value = texto
    }
}
