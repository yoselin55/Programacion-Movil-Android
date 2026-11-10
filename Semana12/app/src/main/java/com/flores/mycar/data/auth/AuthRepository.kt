package com.flores.mycar.data.auth

import com.flores.mycar.domain.model.Sesion

/** Contrato de autenticacion. La implementacion con Firebase va en FirebaseAuthRepository. */
interface AuthRepository {
    suspend fun iniciarSesion(correo: String, clave: String): Result<Sesion>
    suspend fun sesionActual(): Sesion?   // null si no hay sesion
    fun cerrarSesion()

    /** Crea una cuenta nueva. Siempre con rol "usuario": el admin solo se crea a mano en la consola. */
    suspend fun registrar(nombre: String, correo: String, clave: String): Result<Sesion>

    /** Envia un correo para restablecer la contraseña. */
    suspend fun recuperarClave(correo: String): Result<Unit>
}

/** El correo o la contraseña no son correctos. Lo lanza el repositorio para que el ViewModel no conozca Firebase. */
class CredencialesInvalidasException : Exception("Correo o contraseña incorrectos")

/** Ya existe una cuenta con ese correo. */
class CorreoEnUsoException : Exception("Ese correo ya tiene una cuenta")

/** La contraseña es demasiado corta o facil de adivinar. */
class ClaveDebilException : Exception("Contraseña demasiado débil")

/** El correo no tiene un formato valido. */
class CorreoInvalidoException : Exception("Correo inválido")
