package com.flores.mycar.data.fichas

import com.flores.mycar.data.remote.wiki.WikiApi
import com.flores.mycar.domain.model.FichaAuto
import kotlinx.coroutines.CancellationException

/** Busca la descripcion y las fotos de un modelo de auto. Devuelve null si no encuentra nada. */
interface FichaAutoRepository {
    suspend fun ficha(marca: String, modelo: String): FichaAuto?
}

/** Implementacion con Wikipedia: prueba primero en español y luego en ingles. */
class FichaAutoRepositoryWiki(
    private val es: WikiApi,
    private val en: WikiApi
) : FichaAutoRepository {

    // Para no repetir la misma consulta mientras la app esta abierta.
    private val memoria = mutableMapOf<String, FichaAuto?>()

    override suspend fun ficha(marca: String, modelo: String): FichaAuto? {
        val clave = "$marca|$modelo".lowercase()
        if (memoria.containsKey(clave)) return memoria[clave]

        val titulo = "$marca $modelo".trim().replace(' ', '_')
        val resultado = buscar(es, titulo) ?: buscar(en, titulo)
        // Solo se guarda lo encontrado; un fallo por falta de internet se puede reintentar.
        if (resultado != null) memoria[clave] = resultado
        return resultado
    }

    private suspend fun buscar(api: WikiApi, titulo: String): FichaAuto? = try {
        val resumen = api.resumen(titulo)
        if (resumen.type == "disambiguation") {
            null
        } else {
            val imagenes = mutableListOf<String>()
            (resumen.originalimage?.source ?: resumen.thumbnail?.source)?.let { imagenes += it }
            // Mas fotos del articulo, dejando fuera logos, iconos, banderas y dibujos vectoriales.
            runCatching { api.medios(titulo) }.getOrNull()?.items.orEmpty()
                .filter { it.type == "image" && esFotoDeAuto(it.title) }
                .mapNotNull { medio -> medio.srcset.lastOrNull()?.src?.let(::conHttps) }
                .forEach { imagenes += it }

            val lista = imagenes.distinct().take(8)
            if (lista.isEmpty() && resumen.extract.isNullOrBlank()) null
            else FichaAuto(resumen.extract, lista, "Wikipedia")
        }
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        null   // 404 (no existe el articulo) o sin internet
    }

    private fun esFotoDeAuto(titulo: String): Boolean {
        val t = titulo.lowercase()
        val malo = listOf(".svg", ".gif", ".ogg", ".webm", "logo", "icon", "flag", "symbol", "commons-", "wikidata", "diagram", "map")
        return malo.none { it in t } && (t.endsWith(".jpg") || t.endsWith(".jpeg") || t.endsWith(".png") || t.endsWith(".webp"))
    }

    private fun conHttps(url: String) = if (url.startsWith("//")) "https:$url" else url
}
