package com.flores.murocampus.model

import kotlinx.serialization.Serializable

// Commit 3: modelos del JSON de JSONPlaceholder (Post, Comentario y NuevoPost).
@Serializable
data class Post(
    val userId: Int,
    val id: Int,
    val title: String,
    val body: String
)

@Serializable
data class Comentario(
    val postId: Int,
    val id: Int,
    val name: String,
    val email: String,
    val body: String
)

@Serializable
data class NuevoPost(
    val userId: Int,
    val title: String,
    val body: String
)

// IA commit 6: cuerpo parcial del PATCH (solo el titulo cambia).
@Serializable
data class EditarPost(
    val title: String
)

// Ejemplo entregado del laboratorio.
@Serializable
data class Usuario(
    val id: Int,
    val name: String,
    val email: String
)
