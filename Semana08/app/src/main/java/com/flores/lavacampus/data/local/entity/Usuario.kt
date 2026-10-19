package com.flores.lavacampus.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

// Tabla "usuarios": estudiantes (CLIENTE) y administrador (ADMIN)
@Entity(tableName = "usuarios")
data class Usuario(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val codigo: String,   // codigo de estudiante (vacio para el administrador)
    val correo: String,
    val clave: String,
    val rol: String       // Rol.name: CLIENTE | ADMIN
)
