package com.flores.lavacampus.data.local

import com.flores.lavacampus.data.local.entity.DetallePedido
import com.flores.lavacampus.data.local.entity.Pedido
import com.flores.lavacampus.data.local.entity.Servicio
import com.flores.lavacampus.data.local.entity.Usuario
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

// Datos ficticios (Ley 29733). Se cargan solo la primera vez
object DatosIniciales {

    private val servicios = listOf(
        Servicio(1, "Lavado y secado", "Lavado y secado por kilo", "KG", 8.0, 24, true, "wash"),
        Servicio(2, "Planchado", "Planchado por prenda", "PRENDA", 2.5, 48, true, "iron"),
        Servicio(3, "Lavado en seco", "Prendas delicadas", "PRENDA", 12.0, 48, true, "shirt"),
        Servicio(4, "Edredón / frazada", "Lavado de edredones", "UNIDAD", 20.0, 48, false, "wash")
    )

    private val usuarios = listOf(
        Usuario(1, "Ana García", "U202310234", "ana@correo.test", "1234", "CLIENTE"),
        Usuario(2, "Luis Rojas", "U202310587", "luis@correo.test", "1234", "CLIENTE"),
        Usuario(3, "Marta Quispe", "U202311042", "marta@correo.test", "1234", "CLIENTE"),
        Usuario(4, "Administrador", "", "admin@lavacampus.test", "admin", "ADMIN")
    )

    // Pedido de ejemplo: lineas = (servicioId, cantidad)
    private class Ejemplo(
        val usuarioId: Int,
        val diasAtras: Long,
        val estado: String,
        val pagado: Boolean,
        val casillero: String?,
        val lineas: List<Pair<Int, Double>>
    )

    private val ejemplos = listOf(
        Ejemplo(1, 3, "ENTREGADO", true, null, listOf(1 to 4.0, 2 to 3.0)),
        Ejemplo(2, 2, "ENTREGADO", true, null, listOf(1 to 5.0)),
        Ejemplo(3, 2, "ENTREGADO", true, null, listOf(3 to 2.0)),
        Ejemplo(1, 1, "LISTO", false, "C-05", listOf(1 to 3.0)),
        Ejemplo(2, 1, "LISTO", false, "C-07", listOf(2 to 6.0)),
        Ejemplo(3, 1, "EN_PROCESO", false, null, listOf(1 to 2.5, 2 to 4.0)),
        Ejemplo(2, 0, "EN_PROCESO", false, null, listOf(3 to 1.0)),
        Ejemplo(1, 0, "RECIBIDO", false, null, listOf(1 to 3.5)),
        Ejemplo(3, 0, "RECIBIDO", false, null, listOf(2 to 5.0)),
        Ejemplo(2, 0, "RECIBIDO", false, null, listOf(1 to 2.0, 2 to 2.0))
    )

    suspend fun sembrar(db: LavaDatabase) {
        if (db.usuarioDao().contar() != 0) return // ya sembrado

        db.servicioDao().insertarTodos(servicios)
        db.usuarioDao().insertarTodos(usuarios)

        val hoy = LocalDate.now()
        // Los pedidos nunca caen antes del lunes, para que cuenten en los ingresos de la semana
        val lunes = hoy.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))

        for (e in ejemplos) {
            val registro = maxOf(hoy.minusDays(e.diasAtras), lunes)
            val horas = e.lineas.maxOf { (id, _) -> servicios.first { it.id == id }.horasEntrega }
            var entrega = registro.plusDays((horas / 24).toLong())
            if (entrega.dayOfWeek == DayOfWeek.SUNDAY) entrega = entrega.plusDays(1)

            // El total sale de la suma de sus detalles (precios congelados)
            val subtotales = e.lineas.map { (id, cant) -> cant * servicios.first { it.id == id }.precio }
            val pedidoId = db.pedidoDao().insertar(
                Pedido(
                    usuarioId = e.usuarioId,
                    fechaRegistro = registro.toString(),
                    fechaEntrega = entrega.toString(),
                    estado = e.estado,
                    total = subtotales.sum(),
                    casillero = e.casillero,
                    pagado = e.pagado
                )
            ).toInt()

            db.detalleDao().insertarTodos(
                e.lineas.mapIndexed { i, (id, cant) ->
                    DetallePedido(
                        pedidoId = pedidoId,
                        servicioId = id,
                        cantidad = cant,
                        precioUnitario = servicios.first { it.id == id }.precio,
                        subtotal = subtotales[i]
                    )
                }
            )
        }
    }
}
