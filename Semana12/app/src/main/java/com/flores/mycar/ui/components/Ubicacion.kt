package com.flores.mycar.ui.components

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationManager
import androidx.core.content.ContextCompat
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** true si la app ya tiene permiso para ver la ubicacion. */
fun tienePermisoDeUbicacion(contexto: Context): Boolean =
    ContextCompat.checkSelfPermission(contexto, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(contexto, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

/**
 * Direccion (calle y ciudad) del ultimo lugar conocido del telefono, o null si no se puede saber
 * (sin permiso, sin ubicacion guardada o sin internet para convertirla en direccion).
 */
@SuppressLint("MissingPermission")
@Suppress("DEPRECATION")
suspend fun direccionActual(contexto: Context): String? = withContext(Dispatchers.IO) {
    if (!tienePermisoDeUbicacion(contexto)) return@withContext null
    try {
        val administrador = contexto.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val ubicacion = listOf(LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER, LocationManager.PASSIVE_PROVIDER)
            .mapNotNull { proveedor -> runCatching { administrador.getLastKnownLocation(proveedor) }.getOrNull() }
            .maxByOrNull { it.time }
            ?: return@withContext null
        val lugar = runCatching {
            Geocoder(contexto, Locale.getDefault()).getFromLocation(ubicacion.latitude, ubicacion.longitude, 1)?.firstOrNull()?.getAddressLine(0)
        }.getOrNull()
        lugar ?: "%.5f, %.5f".format(Locale.US, ubicacion.latitude, ubicacion.longitude)
    } catch (e: Exception) {
        null
    }
}
