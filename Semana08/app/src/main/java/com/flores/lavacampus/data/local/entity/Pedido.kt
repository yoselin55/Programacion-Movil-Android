package com.flores.lavacampus.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

// Tabla "pedidos": N pedidos pertenecen a 1 estudiante (FK usuarioId)
@Entity(
    tableName = "pedidos",
    foreignKeys = [
        ForeignKey(
            entity = Usuario::class,
            parentColumns = ["id"],
            childColumns = ["usuarioId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    // Indice en la FK y en estado: aceleran la bandeja y el panel
    indices = [Index("usuarioId"), Index("estado")]
)
data class Pedido(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val usuarioId: Int,
    val fechaRegistro: String,   // aaaa-MM-dd
    val fechaEntrega: String,    // aaaa-MM-dd
    val estado: String,          // EstadoPedido.name
    val total: Double,
    val casillero: String? = null, // nulo hasta estar LISTO
    val pagado: Boolean = false,
    val nota: String = "",
    val fechaCreacion: Long = System.currentTimeMillis()
)
