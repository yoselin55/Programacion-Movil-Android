package com.flores.tecsupfit.model

// Usuario registrado en memoria (se pierde al cerrar la app)
data class User(
    val fullName: String,
    val email: String,
    val password: String
) {
    // Primer nombre, usado en el saludo del Home
    val firstName: String
        get() = fullName.trim().split(" ").first()

    // Iniciales para el avatar del perfil (máximo 2 letras)
    val initials: String
        get() = fullName.trim()
            .split(" ")
            .filter { it.isNotBlank() }
            .take(2)
            .joinToString("") { it.first().uppercase() }
}
