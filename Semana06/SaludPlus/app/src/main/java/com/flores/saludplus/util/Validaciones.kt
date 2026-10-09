package com.flores.saludplus.util

// Relaciones:
// - La usan RegistroScreen (los 4 campos) y LoginScreen (teléfono) en ui/screens/auth
// - La prueba ValidacionesTest (app/src/test)
// - Es Kotlin puro (sin clases de Android) para poder probarla con JUnit

// Fase 2: reglas de validación de los formularios. Cada función devuelve el mensaje de error
// que se muestra bajo el campo, o null si el dato es válido.
object
Validaciones {

    // Fase 2: largo máximo del nombre y del correo
    const val MAX_NOMBRE = 40
    const val MAX_CORREO = 60

    // Fase 3: largo máximo del motivo de consulta
    const val MAX_MOTIVO = 200

    // Fase 2: solo letras (incluidas tildes, ü y ñ), espacios y guion; sin números ni símbolos
    private val REGEX_CARACTERES_NOMBRE = Regex("^[A-Za-zÁÉÍÓÚÜÑáéíóúüñ\\s-]+$")

    // Fase 2: una sola letra (con tildes, ü y ñ); se usa para contar letras y para la contraseña
    private val REGEX_LETRA = Regex("[A-Za-zÁÉÍÓÚÜÑáéíóúüñ]")

    // Fase 2: uno o más espacios seguidos; separa las palabras del nombre
    private val REGEX_ESPACIOS = Regex("\\s+")

    // Fase 2: teléfono peruano: empieza con 9 y le siguen exactamente 8 dígitos (9 en total)
    private val REGEX_TELEFONO = Regex("^9\\d{8}$")

    // Fase 2: correo texto@dominio.extension:
    // usuario con letras, números, punto, guion o guion bajo; dominio con al menos un punto
    // y extensión final de 2 o más letras; no admite espacios
    private val REGEX_CORREO = Regex("^[A-Za-z0-9._-]+@([A-Za-z0-9-]+\\.)+[A-Za-z]{2,}$")

    // Fase 2: un dígito cualquiera (la contraseña debe tener al menos uno)
    private val REGEX_DIGITO = Regex("\\d")

    // Fase 2: cualquier espacio en blanco (la contraseña no puede tenerlos)
    private val REGEX_ESPACIO = Regex("\\s")

    // Fase 2: dominios mal escritos frecuentes y el dominio que el usuario quiso escribir
    private val DOMINIOS_CORREGIDOS = mapOf(
        "gamil.com" to "gmail.com",
        "gmial.com" to "gmail.com",
        "gmai.com" to "gmail.com",
        "hotmial.com" to "hotmail.com",
        "hotmal.com" to "hotmail.com",
        "outlok.com" to "outlook.com"
    )

    // Fase 2: valida el nombre completo (se evalúa sin espacios al inicio y al final).
    // Devuelve el mensaje de error o null si es válido.
    fun errorNombre(nombre: String): String? {
        val limpio = nombre.trim()
        // Valida que no esté vacío
        if (limpio.isEmpty()) return "Ingresa tu nombre completo"
        // Valida que solo tenga letras, espacios y guion (sin números ni símbolos)
        if (!REGEX_CARACTERES_NOMBRE.matches(limpio)) return "El nombre solo puede tener letras"
        // Valida el largo máximo de 40 caracteres
        if (limpio.length > MAX_NOMBRE) return "Máximo $MAX_NOMBRE caracteres"
        val palabras = limpio.split(REGEX_ESPACIOS)
        // Valida que haya al menos nombre y apellido (2 palabras)
        if (palabras.size < 2) return "Ingresa tu nombre completo"
        // Valida que cada palabra tenga 2 o más letras ("J P" no es válido)
        if (palabras.any { palabra -> REGEX_LETRA.findAll(palabra).count() < 2 }) {
            return "Ingresa tu nombre completo"
        }
        return null
    }

    // Fase 2: valida el teléfono: exactamente 9 dígitos y empieza con 9.
    // Devuelve el mensaje de error o null si es válido.
    fun errorTelefono(telefono: String): String? =
        if (REGEX_TELEFONO.matches(telefono)) null
        else "El teléfono debe tener 9 dígitos y empezar con 9"

    // Fase 2: valida el correo, que es OPCIONAL (vacío es válido).
    // Devuelve "Correo no válido", un aviso de dominio mal escrito o null si es válido.
    fun errorCorreo(correo: String): String? {
        val limpio = correo.trim()
        // Valida que sea opcional: sin texto no hay error
        if (limpio.isEmpty()) return null
        // Valida el largo máximo de 60 caracteres
        if (limpio.length > MAX_CORREO) return "Correo no válido"
        // Valida el formato texto@dominio.extension y que no tenga espacios
        if (!REGEX_CORREO.matches(limpio)) return "Correo no válido"
        // Valida que el dominio no sea un error de escritura frecuente (gamil.com -> gmail.com)
        val dominio = limpio.substringAfter('@').lowercase()
        DOMINIOS_CORREGIDOS[dominio]?.let { correcto -> return "¿Quisiste decir $correcto?" }
        return null
    }

    // Fase 2: valida la contraseña: mínimo 6 caracteres, al menos una letra y un número, sin espacios.
    // Devuelve el mensaje de error o null si es válida.
    fun errorContrasena(contrasena: String): String? {
        val valida = contrasena.length >= 6 &&          // mínimo 6 caracteres
            REGEX_LETRA.containsMatchIn(contrasena) &&   // al menos una letra
            REGEX_DIGITO.containsMatchIn(contrasena) &&  // al menos un número
            !REGEX_ESPACIO.containsMatchIn(contrasena)   // sin espacios
        return if (valida) null else "Mínimo 6 caracteres, con letras y números"
    }
}
