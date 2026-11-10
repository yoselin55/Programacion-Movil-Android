package com.flores.mycar.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flores.mycar.data.autos.AutoRepository
import com.flores.mycar.domain.model.UsuarioResumen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

/** Las personas registradas en la app (lo ve el administrador). */
class UsuariosViewModel(
    repo: AutoRepository
) : ViewModel() {

    private val _usuarios = MutableStateFlow<List<UsuarioResumen>?>(null)
    val usuarios: StateFlow<List<UsuarioResumen>?> = _usuarios.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        viewModelScope.launch {
            repo.usuarios()
                .catch { _error.value = "No se pudo cargar la lista de usuarios. Revisa las reglas de Firebase." }
                .collect { _usuarios.value = it }
        }
    }
}
