package com.flores.lavacampus.model

// Reglas de los campos del registro de estudiantes. Devuelven el mensaje de error o null si es valido
object ValidacionRegistro {
    private val NOMBRE = Regex("^[A-Za-zÁÉÍÓÚÜÑáéíóúüñ]+(?: [A-Za-zÁÉÍÓÚÜÑáéíóúüñ]+)+$") // nombre y apellido
    private val CODIGO = Regex("^U\\d{9}$")                                            // ej. U202310234
    private val CORREO = Regex("^[^@\\s]+@[^@\\s]+\\.[^@\\s]{2,}$")

    const val CLAVE_MINIMA = 4

    fun nombre(valor: String): String? = when {
        valor.isBlank() -> "Escribe tu nombre completo"
        !NOMBRE.matches(valor.trim()) -> "Escribe nombre y apellido, solo letras"
        valor.trim().length < 5 -> "El nombre es muy corto"
        else -> null
    }

    fun codigo(valor: String): String? = when {
        valor.isBlank() -> "Escribe tu código de estudiante"
        !CODIGO.matches(valor.trim().uppercase()) -> "Formato: U y 9 números (ej. U202310234)"
        else -> null
    }

    fun correo(valor: String): String? = when {
        valor.isBlank() -> "Escribe tu correo"
        !CORREO.matches(valor.trim()) -> "Escribe un correo válido (ej. ana@correo.test)"
        else -> null
    }

    fun clave(valor: String): String? = when {
        valor.isBlank() -> "Escribe una contraseña"
        valor.length < CLAVE_MINIMA -> "Mínimo $CLAVE_MINIMA caracteres"
        valor.contains(' ') -> "La contraseña no puede tener espacios"
        else -> null
    }

    fun confirmacion(clave: String, confirmacion: String): String? = when {
        confirmacion.isBlank() -> "Repite la contraseña"
        confirmacion != clave -> "Las contraseñas no coinciden"
        else -> null
    }
}
