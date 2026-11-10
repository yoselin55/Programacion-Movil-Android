package com.flores.mycar.data.fichas

import android.content.Context
import com.flores.mycar.data.remote.wiki.WikidataApi
import java.util.concurrent.ConcurrentHashMap
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.jsonPrimitive

/** Busca el logo de una marca. Devuelve null si no encuentra uno (entonces se muestra la inicial). */
interface LogoMarcaRepository {
    suspend fun logo(marca: String): String?
}

/**
 * Logo oficial de la marca segun Wikidata (propiedad "logo image"); la imagen la sirve Wikimedia Commons.
 * Cada logo encontrado se guarda en el telefono, asi no se vuelve a consultar.
 */
class LogoMarcaWikidata(
    private val wikidata: WikidataApi,
    context: Context
) : LogoMarcaRepository {

    private val prefs = context.applicationContext.getSharedPreferences("mycar_logos", Context.MODE_PRIVATE)
    private val fallos = ConcurrentHashMap<String, Long>()
    private val turno = Mutex()   // una consulta a la vez: Wikimedia limita las consultas muy seguidas

    override suspend fun logo(marca: String): String? {
        prefs.getString("logo_$marca", null)?.let { return it }
        val fallo = fallos[marca]
        if (fallo != null && System.currentTimeMillis() - fallo < REINTENTO_MS) return null
        return turno.withLock {
            prefs.getString("logo_$marca", null) ?: buscar(marca).also { url ->
                if (url != null) prefs.edit().putString("logo_$marca", url).apply()
                else fallos[marca] = System.currentTimeMillis()
                delay(350)
            }
        }
    }

    private suspend fun buscar(marca: String): String? = try {
        val candidatas = wikidata.buscar(marca).search
            .filter { e ->
                val d = e.description.lowercase()
                listOf("manufacturer", "automaker", "carmaker", "company", "brand", "marque", "division", "automobile")
                    .any { it in d } && "model" !in d
            }
            .take(3)
        var archivo: String? = null
        for (candidata in candidatas) {
            archivo = wikidata.logo(candidata.id).claims["P154"]
                ?.firstOrNull()?.mainsnak?.datavalue?.value?.jsonPrimitive?.content
            if (archivo != null) break
            delay(200)
        }
        archivo?.let { "https://commons.wikimedia.org/wiki/Special:FilePath/${java.net.URLEncoder.encode(it, "UTF-8").replace("+", "%20")}?width=160" }
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        null
    }

    private companion object {
        const val REINTENTO_MS = 5 * 60_000L
    }
}
