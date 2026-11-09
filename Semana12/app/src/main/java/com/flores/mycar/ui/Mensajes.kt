package com.flores.mycar.ui

import java.io.IOException
import retrofit2.HttpException

/** Convierte una excepcion en un mensaje claro para la persona. */
fun mensajeDe(e: Throwable): String = when (e) {
    is IOException -> "No hay conexión a internet"
    is HttpException -> "El servidor respondió con error ${e.code()}"
    else -> "Ocurrió un error inesperado"
}
