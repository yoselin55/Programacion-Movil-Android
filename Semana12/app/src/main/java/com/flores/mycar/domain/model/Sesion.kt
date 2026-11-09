package com.flores.mycar.domain.model

/** Rol de la persona que inicio sesion. Se lee de Firestore (usuarios/{uid}.rol). */
enum class Rol { ADMIN, USUARIO }

/** Sesion activa: quien es y que rol tiene. */
data class Sesion(
    val uid: String,
    val correo: String,
    val rol: Rol
)
