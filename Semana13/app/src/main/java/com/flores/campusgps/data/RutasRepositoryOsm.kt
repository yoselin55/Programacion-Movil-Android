package com.flores.campusgps.data

import com.flores.campusgps.model.Posicion
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

// Mejora 3: calcula la ruta a pie con el servicio gratuito de OpenStreetMap (no necesita clave).
// La respuesta es un GeoJSON con las coordenadas del camino, que siguen las calles y veredas.
class RutasRepositoryOsm : RutasRepository {

    override suspend fun obtenerRuta(desde: Posicion, hasta: Posicion): Result<List<Posicion>> =
        withContext(Dispatchers.IO) { // la red nunca se usa en el hilo principal
            try {
                // El servicio pide primero la longitud y después la latitud.
                val direccion = "https://routing.openstreetmap.de/routed-foot/route/v1/foot/" +
                    "${desde.longitud},${desde.latitud};${hasta.longitud},${hasta.latitud}" +
                    "?overview=full&geometries=geojson"

                val conexion = (URL(direccion).openConnection() as HttpURLConnection).apply {
                    connectTimeout = 8000
                    readTimeout = 8000
                    setRequestProperty("User-Agent", "CampusGPS/1.0 (proyecto academico)")
                }
                try {
                    val texto = conexion.inputStream.bufferedReader().use { it.readText() }
                    val coordenadas = JSONObject(texto)
                        .getJSONArray("routes").getJSONObject(0)
                        .getJSONObject("geometry").getJSONArray("coordinates")

                    Result.success(
                        List(coordenadas.length()) { i ->
                            val punto = coordenadas.getJSONArray(i)
                            Posicion(latitud = punto.getDouble(1), longitud = punto.getDouble(0))
                        }
                    )
                } finally {
                    conexion.disconnect()
                }
            } catch (e: CancellationException) {
                throw e // si se cancela la búsqueda, no se trata como error
            } catch (e: Exception) {
                Result.failure(e) // sin internet o sin ruta: el ViewModel decide qué mostrar
            }
        }
}
