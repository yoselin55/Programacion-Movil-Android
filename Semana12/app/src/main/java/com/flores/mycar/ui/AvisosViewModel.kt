package com.flores.mycar.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flores.mycar.data.autos.AutoRepository
import com.flores.mycar.data.prefs.PedidosVistosRepository
import com.flores.mycar.domain.model.Auto
import com.flores.mycar.domain.model.EstadoAuto
import com.flores.mycar.domain.model.EstadoCita
import com.flores.mycar.domain.model.Rol
import com.flores.mycar.domain.model.Sesion
import com.flores.mycar.notification.Notificador
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Avisa con una notificacion cuando cambia algo que le importa a la persona:
 * - Cliente: su pedido pasa a "pagado" o "entregado", o le confirman una cita.
 * - Admin: un cliente paga un auto o agenda una cita.
 * Funciona mientras la app esta abierta o en segundo plano.
 */
class AvisosViewModel(
    private val repo: AutoRepository,
    private val notificador: Notificador,
    private val vistosRepo: PedidosVistosRepository
) : ViewModel() {

    private var trabajo: Job? = null
    private var uid: String = ""

    private val pedidos = MutableStateFlow<List<Auto>>(emptyList())
    private val vistos = MutableStateFlow<Set<String>>(emptySet())
    private val esAdminFlow = MutableStateFlow(false)

    /**
     * Cambios de pedidos que la persona todavia no vio. Cliente: pedidos que el admin paso a pagado o entregado.
     * Admin: pedidos nuevos en proceso. Se actualiza solo, en tiempo real.
     */
    val sinVer: StateFlow<Int> = combine(pedidos, vistos, esAdminFlow) { lista, vistos, admin ->
        lista.count { clave(it) !in vistos && (if (admin) it.estado == EstadoAuto.PAGO_REALIZADO else it.estado != EstadoAuto.PAGO_REALIZADO) }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    private fun clave(pedido: Auto) = "${pedido.id}:${pedido.estado.clave}"

    /** La persona abrio sus pedidos: todo lo actual queda visto. */
    fun marcarVistos() {
        val nuevos = vistos.value + pedidos.value.map(::clave)
        vistos.value = nuevos
        if (uid.isNotBlank()) vistosRepo.guardar(uid, nuevos)
    }

    fun iniciar(sesion: Sesion) {
        if (trabajo?.isActive == true) return
        val esAdmin = sesion.rol == Rol.ADMIN
        uid = sesion.uid
        esAdminFlow.value = esAdmin
        vistos.value = vistosRepo.vistos(sesion.uid)

        trabajo = viewModelScope.launch {
            var autosPrevios: Map<String, EstadoAuto>? = null
            var citasPrevias: Map<String, EstadoCita>? = null

            launch {
                repo.pedidos(if (esAdmin) null else sesion.uid).catch { }.collect { lista ->
                    pedidos.value = lista
                    val previos = autosPrevios
                    // La primera lectura solo se guarda: no se avisa de lo que ya existia.
                    if (previos != null) {
                        lista.forEach { auto ->
                            val antes = previos[auto.id]
                            if (antes == null && esAdmin) {
                                notificador.mostrar(
                                    "Nuevo pedido",
                                    "${auto.compradorCorreo ?: "Un cliente"} compró ${auto.nombre}"
                                )
                            } else if (antes != null && antes != auto.estado && !esAdmin &&
                                (auto.estado == EstadoAuto.PAGADO || auto.estado == EstadoAuto.ENTREGADO || auto.estado == EstadoAuto.NO_RECOGIDO)
                            ) {
                                notificador.mostrar("Tu pedido cambió", "${auto.nombre}: ${auto.estado.etiqueta}")
                            }
                        }
                    }
                    autosPrevios = lista.associate { it.id to it.estado }
                }
            }

            launch {
                repo.citas(if (esAdmin) null else sesion.uid).catch { }.collect { lista ->
                    val previas = citasPrevias
                    if (previas != null) {
                        lista.forEach { cita ->
                            val antes = previas[cita.id]
                            if (antes == null && esAdmin) {
                                notificador.mostrar("Nueva cita", "${cita.correo} agendó ${cita.autoNombre} el ${cita.fecha}")
                            } else if (antes != null && antes != cita.estado && !esAdmin && cita.estado == EstadoCita.CONFIRMADA) {
                                notificador.mostrar("Cita confirmada", "${cita.autoNombre}: ${cita.fecha} a las ${cita.hora}")
                            }
                        }
                    }
                    citasPrevias = lista.associate { it.id to it.estado }
                }
            }
        }
    }
}
