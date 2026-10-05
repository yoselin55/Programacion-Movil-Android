package com.flores.saludplus.data.model

// Relaciones:
// - Lo usa Repositorio (colección usuarios y usuarioActual)
// Paciente registrado en la app
data class Usuario(
    val id: Int,
    val nombre: String,
    val telefono: String,
    val correo: String,
    val contrasena: String
)
