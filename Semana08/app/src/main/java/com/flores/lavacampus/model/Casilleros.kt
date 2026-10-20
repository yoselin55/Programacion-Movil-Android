package com.flores.lavacampus.model

// Casilleros de recojo: C-01 a C-30
object Casilleros {
    val todos: List<String> = (1..30).map { "C-%02d".format(it) }

    fun esValido(c: String) = c in todos
}
