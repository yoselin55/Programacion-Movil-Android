package com.flores.lavacampus.data.repository

import com.flores.lavacampus.data.local.entity.Notificacion
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
    suspend fun usuarioDeRol(rol: String): Usuario?
    suspend fun existeCorreo(correo: String): Boolean
    suspend fun existeCodigo(codigo: String): Boolean
    suspend fun registrarUsuario(u: Usuario): Usuario
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
    // porAdmin = false cuando el estudiante cancela su propio pedido (el aviso va al administrador)
    suspend fun cambiarEstado(id: Int, estado: EstadoPedido, casillero: String?, porAdmin: Boolean = true)
    suspend fun marcarPagado(id: Int, pagado: Boolean)
    fun observarCasillerosOcupados(): Flow<List<String>>
    fun observarPedidosAtrasados(hoy: String): Flow<List<Int>>

    // Notificaciones: el Repository las registra junto con cada cambio de un pedido
    fun observarNotificaciones(usuarioId: Int): Flow<List<Notificacion>>
    fun observarNoLeidas(usuarioId: Int): Flow<Int>
    suspend fun marcarNotificacionLeida(id: Int)
    suspend fun marcarNotificacionesLeidas(usuarioId: Int)
    suspend fun borrarNotificaciones(usuarioId: Int)

    // Panel
    fun observarPedidosDeFecha(fecha: String): Flow<Int>
    fun observarCantidadPorEstado(estado: EstadoPedido): Flow<Int>
    fun observarIngresosDesde(fechaInicio: String): Flow<Double>
    fun observarIngresosPorServicio(fechaInicio: String): Flow<List<IngresoServicio>>
}
