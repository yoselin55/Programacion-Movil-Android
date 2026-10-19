package com.flores.lavacampus.model

// Perfiles de la app. En la base se guarda como String (name)
enum class Rol(val texto: String) {
    CLIENTE("Estudiante"),
    ADMIN("Administrador")
}
