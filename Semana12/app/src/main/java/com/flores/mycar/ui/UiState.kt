package com.flores.mycar.ui

/** Estado que expone cada ViewModel a la pantalla: Cargando, Exito o Error. */
sealed interface UiState<out T> {
    data object Cargando : UiState<Nothing>
    data class Exito<T>(val datos: T) : UiState<T>
    data class Error(val mensaje: String) : UiState<Nothing>
}
