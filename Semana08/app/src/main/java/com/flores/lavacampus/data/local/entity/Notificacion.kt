package com.flores.lavacampus.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

// Tabla "notificaciones": N notificaciones pertenecen a 1 usuario (FK usuarioId).
// La recibe el estudiante cuando cambia su pedido y el administrador cuando llega uno nuevo
@Entity(
    tableName = "notificaciones",
    foreignKeys = [
        ForeignKey(
            entity = Usuario::class,
            parentColumns = ["id"],
            childColumns = ["usuarioId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("usuarioId")]
)
data class Notificacion(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val usuarioId: Int,          // quien la recibe
    val pedidoId: Int? = null,   // pedido al que se refiere (al tocarla se abre)
    val titulo: String,
    val mensaje: String,
    val tipo: String,            // TipoNotificacion.name: define el icono y el color
    val fecha: Long = System.currentTimeMillis(), // el momento en que se guardo
    val leida: Boolean = false
)
