package com.flores.mycar.domain.model

/** Ficha de un auto: modelo representativo, una descripcion y varias fotos, tomadas de Wikipedia. */
data class FichaAuto(
    val descripcion: String?,
    val imagenes: List<String>,
    val fuente: String,
    val modelo: String = ""
)
