package com.flores.lavacampus.data.repository

import androidx.room.withTransaction
import com.flores.lavacampus.data.local.LavaDatabase
import com.flores.lavacampus.data.local.entity.DetallePedido
import com.flores.lavacampus.data.local.entity.Pedido
import com.flores.lavacampus.data.local.entity.Servicio
import com.flores.lavacampus.data.local.entity.Usuario
import com.flores.lavacampus.data.local.relation.PedidoConDetalle
import com.flores.lavacampus.model.ClienteResumen
import com.flores.lavacampus.model.Descuento
import com.flores.lavacampus.model.EstadoPedido
import com.flores.lavacampus.model.IngresoServicio
import com.flores.lavacampus.model.LineaPedido
import com.flores.lavacampus.model.Unidad
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

// Implementacion con Room: solo delega en los DAOs. Las reglas viven en los ViewModels
class LavaRepositoryRoom(private val db: LavaDatabase) : LavaRepository {

    private val servicioDao = db.servicioDao()
    private val usuarioDao = db.usuarioDao()
    private val pedidoDao = db.pedidoDao()
    private val detalleDao = db.detalleDao()

    // Usuarios
    override suspend fun login(correo: String, clave: String): Usuario? =
        usuarioDao.login(correo, clave)

    override suspend fun obtenerUsuario(id: Int): Usuario? = usuarioDao.obtener(id)
    override suspend fun usuarioDeRol(rol: String): Usuario? = usuarioDao.primeroPorRol(rol)

    override suspend fun existeCorreo(correo: String): Boolean = usuarioDao.contarPorCorreo(correo) > 0
    override suspend fun existeCodigo(codigo: String): Boolean = usuarioDao.contarPorCodigo(codigo) > 0

    // Devuelve el usuario con el id que le asigno la base
    override suspend fun registrarUsuario(u: Usuario): Usuario = u.copy(id = usuarioDao.insertar(u).toInt())

    override fun observarClientes(): Flow<List<ClienteResumen>> = usuarioDao.observarClientes()

    // Servicios
    override fun observarServicios(): Flow<List<Servicio>> = servicioDao.observarTodos()
    override fun observarServiciosActivos(): Flow<List<Servicio>> = servicioDao.observarActivos()

    // id = 0 es un servicio nuevo; REPLACE tambien sirve para editar uno existente
    override suspend fun guardarServicio(s: Servicio) {
        servicioDao.insertar(s)
    }

    override suspend fun eliminarServicio(s: Servicio) = servicioDao.eliminar(s)
    override suspend fun cambiarActivo(id: Int, activo: Boolean) =
        servicioDao.cambiarActivo(id, activo)

    override suspend fun servicioTieneDetalles(id: Int): Boolean =
        detalleDao.contarPorServicio(id) > 0

    // Pedidos
    // Una sola transaccion: si falla una validacion o un detalle, tambien se deshace el pedido
    override suspend fun crearPedido(
        usuarioId: Int,
        lineas: List<LineaPedido>,
        fechaEntrega: String,
        nota: String
    ): Long = db.withTransaction {
        require(lineas.isNotEmpty()) { "El pedido no tiene servicios" }

        // Se copia el precio del servicio al detalle (precio congelado) y se validan las cantidades
        val detalles = lineas.map { linea ->
            val servicio = servicioDao.obtener(linea.servicioId)
                ?: throw IllegalArgumentException("Servicio ${linea.servicioId} no existe")
            require(servicio.activo) { "El servicio ${servicio.nombre} ya no está disponible" }
            val unidad = Unidad.de(servicio.unidad)
            val enPasos = Math.abs(linea.cantidad / unidad.paso - Math.round(linea.cantidad / unidad.paso)) < 1e-9
            require(linea.cantidad in unidad.minimo..unidad.maximo && enPasos) {
                "Cantidad no válida para ${servicio.nombre}"
            }
            Triple(servicio, linea.cantidad, DetallePedido(
                pedidoId = 0, // se completa cuando se conozca el id del pedido
                servicioId = servicio.id,
                cantidad = linea.cantidad,
                precioUnitario = servicio.precio,
                subtotal = linea.cantidad * servicio.precio
            ))
        }

        // Descuento por volumen: lavado (servicios por kilo) de mas de 5 kg
        val lavado = detalles.filter { it.first.unidad == Unidad.KG.name }
        val descuento = Descuento.sobreLavado(lavado.sumOf { it.second }, lavado.sumOf { it.third.subtotal })

        val pedidoId = pedidoDao.insertar(
            Pedido(
                usuarioId = usuarioId,
                fechaRegistro = LocalDate.now().toString(),
                fechaEntrega = fechaEntrega,
                estado = EstadoPedido.RECIBIDO.name,
                total = detalles.sumOf { it.third.subtotal } - descuento,
                descuento = descuento,
                pagado = false,
                nota = nota
            )
        )
        detalleDao.insertarTodos(detalles.map { it.third.copy(pedidoId = pedidoId.toInt()) })
        pedidoId
    }

    override suspend fun contarActivos(usuarioId: Int): Int = pedidoDao.contarActivos(usuarioId)

    override fun observarPedidosDeUsuario(usuarioId: Int): Flow<List<PedidoConDetalle>> =
        pedidoDao.observarPorUsuario(usuarioId)

    override fun observarPedidos(): Flow<List<PedidoConDetalle>> = pedidoDao.observarTodos()
    override fun observarPedido(id: Int): Flow<PedidoConDetalle?> = pedidoDao.observarUno(id)

    // No valida reglas: eso lo hace el ViewModel
    override suspend fun cambiarEstado(id: Int, estado: EstadoPedido, casillero: String?) =
        pedidoDao.actualizarEstado(id, estado.name, casillero)

    override suspend fun marcarPagado(id: Int, pagado: Boolean) =
        pedidoDao.actualizarPagado(id, pagado)

    override fun observarCasillerosOcupados(): Flow<List<String>> =
        pedidoDao.observarCasillerosOcupados()

    override fun observarPedidosAtrasados(hoy: String): Flow<List<Int>> =
        pedidoDao.observarIdsAtrasados(hoy)

    // Panel
    override fun observarPedidosDeFecha(fecha: String): Flow<Int> = pedidoDao.contarPorFecha(fecha)
    override fun observarCantidadPorEstado(estado: EstadoPedido): Flow<Int> =
        pedidoDao.contarPorEstado(estado.name)

    override fun observarIngresosDesde(fechaInicio: String): Flow<Double> =
        pedidoDao.ingresosDesde(fechaInicio)

    override fun observarIngresosPorServicio(fechaInicio: String): Flow<List<IngresoServicio>> =
        detalleDao.observarIngresosPorServicio(fechaInicio)
}
