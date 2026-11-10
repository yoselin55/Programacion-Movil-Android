package com.flores.mycar.domain.model

/** Lo que la persona guardo en su carrito y en sus favoritos (ids de autos). */
data class ComprasEstado(
    val carrito: Set<String> = emptySet(),
    val favoritos: Set<String> = emptySet()
)

/** Datos personales editables del perfil. */
data class PerfilDatos(
    val nombre: String = "",
    val telefono: String = "",
    val ciudad: String = ""
)
