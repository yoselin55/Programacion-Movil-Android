package com.flores.mycar.ui

import com.flores.mycar.data.autos.FotosLocales
import com.flores.mycar.data.fichas.LogoMarcaRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Pide el logo de cada marca cuando su tarjeta aparece en pantalla y guarda los que encuentra. */
class CargadorLogos(
    private val repo: LogoMarcaRepository?,
    private val alcance: CoroutineScope
) {
    private val _urls = MutableStateFlow<Map<String, String>>(emptyMap())
    val urls: StateFlow<Map<String, String>> = _urls.asStateFlow()

    private val enCurso = mutableSetOf<String>()

    fun pedir(marca: String) {
        // Logo propio de la app (ej. Audi): no hace falta buscarlo en internet.
        FotosLocales.logoDe(marca)?.let { propio -> _urls.update { it + (marca to propio) }; return }
        if (repo == null || marca in _urls.value || !enCurso.add(marca)) return
        alcance.launch {
            val url = repo.logo(marca)
            if (url != null) _urls.update { it + (marca to url) }
            enCurso.remove(marca)   // si no hubo logo, se vuelve a intentar mas tarde
        }
    }
}
