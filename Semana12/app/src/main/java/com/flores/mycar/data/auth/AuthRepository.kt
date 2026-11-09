package com.flores.mycar.data.auth

import com.flores.mycar.domain.model.Sesion

/** Contrato de autenticacion. La implementacion con Firebase va en FirebaseAuthRepository. */
interface AuthRepository {
    suspend fun iniciarSesion(correo: String, clave: String): Result<Sesion>
    suspend fun sesionActual(): Sesion?   // null si no hay sesion
    fun cerrarSesion()
}

/** El correo o la contraseña no son correctos. Lo lanza el repositorio para que el ViewModel no conozca Firebase. */
class CredencialesInvalidasException : Exception("Correo o contraseña incorrectos")
