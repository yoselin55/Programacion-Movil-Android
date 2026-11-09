package com.flores.firabase

/** Documento de la colección "cursos" en Cloud Firestore. */
data class Curso(
    val id: String = "",
    val uid: String = "",
    val nombre: String = "",
    val docente: String = "",
    val creditos: Int = 0
)
