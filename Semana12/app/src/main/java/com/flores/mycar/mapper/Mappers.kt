package com.flores.mycar.mapper

import com.flores.mycar.data.remote.dto.ItemDto
import com.flores.mycar.domain.model.Item

/** Convierte lo que responde la API (ItemDto) al modelo que usa la UI (Item). */
fun ItemDto.aItem(esFavorito: Boolean = false): Item = Item(
    id = id,
    titulo = nombre.aTitulo(),
    subtitulo = if (tipo == "Passenger Car") "Auto de pasajeros" else (tipo ?: "Auto"),
    imagenUrl = null,
    esFavorito = esFavorito
)

/** "ASTON MARTIN" -> "Aston Martin". Las siglas de 3 letras o menos (BMW, GMC) se conservan. */
private fun String.aTitulo(): String = trim().split(" ").joinToString(" ") { palabra ->
    if (palabra.length <= 3) palabra
    else palabra.lowercase().replace(Regex("\\b[a-z]")) { it.value.uppercase() }
}
