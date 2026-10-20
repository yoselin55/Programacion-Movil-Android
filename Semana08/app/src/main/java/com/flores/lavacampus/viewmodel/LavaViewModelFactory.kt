package com.flores.lavacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.flores.lavacampus.data.local.entity.Usuario
import com.flores.lavacampus.data.prefs.PreferenciasStore
import com.flores.lavacampus.data.repository.LavaRepository

// Crea los ViewModels pasandoles el repository (interface) y las preferencias
class LavaViewModelFactory(
    private val repo: LavaRepository,
    private val prefs: PreferenciasStore,
    private val usuario: Usuario? = null, // para los ViewModels que dependen del estudiante
    private val pedidoId: Int = 0         // para el detalle de un pedido
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = when {
        modelClass.isAssignableFrom(SesionViewModel::class.java) -> SesionViewModel(repo, prefs) as T
        modelClass.isAssignableFrom(InicioViewModel::class.java) -> InicioViewModel(repo, usuario!!) as T
        modelClass.isAssignableFrom(NuevoPedidoViewModel::class.java) -> NuevoPedidoViewModel(repo, usuario!!) as T
        modelClass.isAssignableFrom(DetallePedidoViewModel::class.java) -> DetallePedidoViewModel(repo, pedidoId) as T
        modelClass.isAssignableFrom(MisPedidosViewModel::class.java) -> MisPedidosViewModel(repo, usuario!!) as T
        modelClass.isAssignableFrom(TarifasViewModel::class.java) -> TarifasViewModel(repo) as T
        modelClass.isAssignableFrom(PanelViewModel::class.java) -> PanelViewModel(repo) as T
        modelClass.isAssignableFrom(BandejaViewModel::class.java) -> BandejaViewModel(repo) as T
        modelClass.isAssignableFrom(GestionPedidoViewModel::class.java) -> GestionPedidoViewModel(repo, pedidoId) as T
        else -> throw IllegalArgumentException("ViewModel desconocido: ${modelClass.name}")
    }
}
