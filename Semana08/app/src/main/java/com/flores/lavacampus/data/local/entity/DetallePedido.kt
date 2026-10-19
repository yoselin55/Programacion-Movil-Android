package com.flores.lavacampus.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

// Tabla "detalles_pedido": una linea por servicio dentro de un pedido
@Entity(
    tableName = "detalles_pedido",
    foreignKeys = [
        // CASCADE: al borrar un pedido se borran sus lineas
        ForeignKey(
            entity = Pedido::class,
            parentColumns = ["id"],
            childColumns = ["pedidoId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Servicio::class,
            parentColumns = ["id"],
            childColumns = ["servicioId"]
        )
    ],
    indices = [Index("pedidoId"), Index("servicioId")]
)
data class DetallePedido(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val pedidoId: Int,
    val servicioId: Int,
    val cantidad: Double,
    val precioUnitario: Double, // "foto" del precio al momento del pedido
    val subtotal: Double        // cantidad x precioUnitario
)
