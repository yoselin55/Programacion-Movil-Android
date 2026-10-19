package com.flores.lavacampus.data.repository

import com.flores.lavacampus.data.local.entity.Servicio
import com.flores.lavacampus.data.local.entity.Usuario
import com.flores.lavacampus.data.local.relation.PedidoConDetalle
import com.flores.lavacampus.model.ClienteResumen
import com.flores.lavacampus.model.EstadoPedido
import com.flores.lavacampus.model.IngresoServicio
import com.flores.lavacampus.model.LineaPedido
import kotlinx.coroutines.flow.Flow

// El contrato: los ViewModels solo conocen esta interface (luego se podra cambiar por una API)
// Regla: lectura que actualiza la pantalla -> Flow; escritura o accion unica -> suspend
interface LavaRepository {
    // Usuarios
    suspend fun login(correo: String, clave: String): Usuario?
    suspend fun obtenerUsuario(id: Int): Usuario?
    fun observarClientes(): Flow<List<ClienteResumen>>

    // Servicios (ejemplo resuelto)
    fun observarServicios(): Flow<List<Servicio>>
    fun observarServiciosActivos(): Flow<List<Servicio>>
    suspend fun guardarServicio(s: Servicio)
    suspend fun eliminarServicio(s: Servicio)
    suspend fun cambiarActivo(id: Int, activo: Boolean)
    suspend fun servicioTieneDetalles(id: Int): Boolean

    // Pedidos
    suspend fun crearPedido(
        usuarioId: Int,
        lineas: List<LineaPedido>,
        fechaEntrega: String,
        nota: String
    ): Long
    suspend fun contarActivos(usuarioId: Int): Int
    fun observarPedidosDeUsuario(usuarioId: Int): Flow<List<PedidoConDetalle>>
    fun observarPedidos(): Flow<List<PedidoConDetalle>>
    fun observarPedido(id: Int): Flow<PedidoConDetalle?>
    suspend fun cambiarEstado(id: Int, estado: EstadoPedido, casillero: String?)
    suspend fun marcarPagado(id: Int, pagado: Boolean)
    fun observarCasillerosOcupados(): Flow<List<String>>

    // Panel
    fun observarPedidosDeFecha(fecha: String): Flow<Int>
    fun observarCantidadPorEstado(estado: EstadoPedido): Flow<Int>
    fun observarIngresosDesde(fechaInicio: String): Flow<Double>
    fun observarIngresosPorServicio(fechaInicio: String): Flow<List<IngresoServicio>>
}
