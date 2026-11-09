package com.flores.mycar.data.auth

import com.flores.mycar.domain.model.Sesion

/** Contrato de autenticacion. La implementacion con Firebase va en FirebaseAuthRepository. */
interface AuthRepository {
    suspend fun iniciarSesion(correo: String, clave: String): Result<Sesion>
    suspend fun sesionActual(): Sesion?   // null si no hay sesion
    fun cerrarSesion()
}
