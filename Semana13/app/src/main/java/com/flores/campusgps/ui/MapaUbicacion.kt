package com.flores.campusgps.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import com.flores.campusgps.model.LugarConDistancia
import com.flores.campusgps.model.UbicacionUi
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberUpdatedMarkerState

// Commit 1: esqueleto del mapa. Commit 4: muestra tu ubicación. Commit 5: agrega los marcadores de los lugares.
@Composable
fun MapaUbicacion(
    estado: UbicacionUi,
    lugares: List<LugarConDistancia>,
    seleccionado: Int?,
    masCercano: Int? = null, // Commit 8: id del lugar más cercano; se dibuja en verde
    modifier: Modifier = Modifier
) {
    val camara = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(-12.0464, -77.0428), 12f)
    }

    // Commit 4: cuando llega la ubicación (estado Lista), la cámara viaja a tu posición con zoom 16.
    LaunchedEffect(estado) {
        if (estado is UbicacionUi.Lista) {
            val pos = estado.posicion
            camara.animate(CameraUpdateFactory.newLatLngZoom(LatLng(pos.latitud, pos.longitud), 16f))
        }
    }

    // Commit 7: al seleccionar un lugar (desde la lista), la cámara viaja hasta él con zoom 17.
    LaunchedEffect(seleccionado) {
        lugares.firstOrNull { it.lugar.id == seleccionado }?.let {
            camara.animate(
                CameraUpdateFactory.newLatLngZoom(LatLng(it.lugar.latitud, it.lugar.longitud), 17f)
            )
        }
    }

    GoogleMap(modifier = modifier, cameraPositionState = camara) {
        // Commit 4: marcador rojo "Estás aquí", solo cuando ya se conoce la ubicación.
        if (estado is UbicacionUi.Lista) {
            Marker(
                state = rememberUpdatedMarkerState(
                    position = LatLng(estado.posicion.latitud, estado.posicion.longitud)
                ),
                title = "Estás aquí"
            )
        }

        // Commit 5: un marcador azul por cada lugar; key(id) identifica a cada uno.
        // Este composable solo dibuja la lista que recibe, no consulta ningún Repository.
        lugares.forEach { item ->
            key(item.lugar.id) {
                val estadoMarcador = rememberUpdatedMarkerState(
                    position = LatLng(item.lugar.latitud, item.lugar.longitud)
                )
                // Commit 7: si este lugar es el seleccionado, el mapa muestra su nombre.
                LaunchedEffect(seleccionado) {
                    if (seleccionado == item.lugar.id) estadoMarcador.showInfoWindow()
                }
                Marker(
                    state = estadoMarcador,
                    title = item.lugar.nombre,      // al tocar: nombre del lugar
                    snippet = item.lugar.categoria, // y su categoría
                    // Commit 8: el más cercano en verde; los demás siguen en azul.
                    icon = BitmapDescriptorFactory.defaultMarker(
                        if (item.lugar.id == masCercano) BitmapDescriptorFactory.HUE_GREEN
                        else BitmapDescriptorFactory.HUE_AZURE
                    )
                )
            }
        }
    }
}
