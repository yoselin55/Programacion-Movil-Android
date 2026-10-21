package com.flores.lavacampus.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.flores.lavacampus.data.local.entity.DetallePedido
import com.flores.lavacampus.model.IngresoServicio
import kotlinx.coroutines.flow.Flow

@Dao
interface DetalleDao {
    @Insert
    suspend fun insertarTodos(lista: List<DetallePedido>)

    // Si es mayor que 0, el servicio no se puede eliminar (solo desactivar)
    @Query("SELECT COUNT(*) FROM detalles_pedido WHERE servicioId = :servicioId")
    suspend fun contarPorServicio(servicioId: Int): Int

    // Ingresos por servicio: JOIN + SUM + GROUP BY, solo pedidos pagados.
    // El descuento por volumen es del lavado (servicios por kilo): se resta a esas lineas,
    // repartido segun el peso de cada linea dentro del lavado del pedido.
    @Query(
        """
        SELECT s.nombre AS nombre,
               SUM(d.subtotal - CASE
                   WHEN s.unidad = 'KG' AND p.descuento > 0 THEN
                       p.descuento * d.subtotal / (
                           SELECT SUM(d2.subtotal) FROM detalles_pedido d2
                           INNER JOIN servicios s2 ON d2.servicioId = s2.id
                           WHERE d2.pedidoId = p.id AND s2.unidad = 'KG')
                   ELSE 0.0 END) AS total
        FROM detalles_pedido d
        INNER JOIN servicios s ON d.servicioId = s.id
        INNER JOIN pedidos p ON d.pedidoId = p.id
        WHERE p.pagado = 1 AND p.fechaRegistro >= :fechaInicio
        GROUP BY s.id
        """
    )
    fun observarIngresosPorServicio(fechaInicio: String): Flow<List<IngresoServicio>>
}
