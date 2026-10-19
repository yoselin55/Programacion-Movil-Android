package com.flores.lavacampus.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

// Tabla "servicios": catalogo de la lavanderia (ejemplo resuelto del laboratorio)
@Entity(tableName = "servicios")
data class Servicio(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val descripcion: String,
    val unidad: String,       // KG, PRENDA, UNIDAD
    val precio: Double,
    val horasEntrega: Int,    // 24, 48, 72
    val activo: Boolean = true,
    val icono: String         // wash, iron, shirt
)
