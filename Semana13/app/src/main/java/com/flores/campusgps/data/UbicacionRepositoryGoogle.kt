package com.flores.campusgps.data

import android.annotation.SuppressLint
import android.content.Context
import com.flores.campusgps.model.Posicion
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.tasks.await

// Commit 1: obtiene la posición con FusedLocationProviderClient (solo aquí se usa Google).
class UbicacionRepositoryGoogle(private val context: Context) : UbicacionRepository {

    private val cliente = LocationServices.getFusedLocationProviderClient(context)

    // Commit 9: comprueba si el dispositivo tiene Google Play Services disponibles.
    override fun playServicesDisponible(): Boolean =
        GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS

    // El permiso se pide en la pantalla (commit 3), por eso se silencia el aviso aquí.
    @SuppressLint("MissingPermission")
    override suspend fun obtenerPosicion(): Result<Posicion> = try {
        // Pide la ubicación actual y espera el resultado con await().
        val ubicacion = cliente
            .getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, CancellationTokenSource().token)
            .await()
        if (ubicacion != null) {
            Result.success(Posicion(ubicacion.latitude, ubicacion.longitude))
        } else {
            // Commit 9: getCurrentLocation devuelve null si no puede determinar la ubicación (GPS apagado).
            Result.failure(GpsApagadoException())
        }
    } catch (e: SecurityException) {
        Result.failure(e)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
