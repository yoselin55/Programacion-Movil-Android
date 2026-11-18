package com.flores.campusgps.util

import java.text.Normalizer

// Mejora 3 (búsqueda): quita las tildes para que "cafeteria" encuentre "Cafetería".
fun String.sinTildes(): String =
    Normalizer.normalize(this, Normalizer.Form.NFD).replace(Regex("\\p{Mn}+"), "")

// Mejora 3: true si el texto contiene la consulta, sin importar mayúsculas ni tildes.
fun String.contieneBusqueda(consulta: String): Boolean =
    sinTildes().contains(consulta.trim().sinTildes(), ignoreCase = true)
