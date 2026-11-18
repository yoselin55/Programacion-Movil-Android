package com.flores.campusgps.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.flores.campusgps.model.LugarConDistancia
import com.flores.campusgps.model.Posicion
import com.flores.campusgps.model.UbicacionUi
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.Dash
import com.google.android.gms.maps.model.Gap
import com.google.android.gms.maps.model.JointType
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.RoundCap
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberUpdatedMarkerState

// Mejora 1: cámara que muestra a la vez tu posición y el destino, con un margen de 160 px.
private fun encuadrar(a: LatLng, b: LatLng) =
    CameraUpdateFactory.newLatLngBounds(LatLngBounds.builder().include(a).include(b).build(), 160)

// Commit 1: esqueleto del mapa. Commit 4: muestra tu ubicación. Commit 5: agrega los marcadores de los lugares.
@Composable
fun MapaUbicacion(
    estado: UbicacionUi,
    lugares: List<LugarConDistancia>,
    seleccionado: Int?,
    masCercano: Int? = null, // Commit 8: id del lugar más cercano; se dibuja en verde
    siguiendo: Boolean = false, // Mejora 1: true mientras el seguimiento en vivo está activo
    ruta: List<Posicion> = emptyList(), // Mejora 3: camino a pie por las calles hasta el destino
    usarPuntoAzul: Boolean = false, // Mejora 3: punto azul nativo y botón de mi ubicación (solo con permiso)
    contentPadding: PaddingValues = PaddingValues(), // deja libres los bordes ocupados por el buscador y el panel
    onMiUbicacion: () -> Unit = {}, // se llama al pulsar el botón de mi ubicación del mapa
    modifier: Modifier = Modifier
) {
    val camara = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(-12.0464, -77.0428), 12f)
    }

    // Destino elegido (si hay uno): se usa para dibujar la línea y para encuadrar la cámara.
    val destino = lugares.firstOrNull { it.lugar.id == seleccionado }?.lugar

    // Commit 4: cuando llega la ubicación (estado Lista), la cámara viaja a tu posición con zoom 16.
    // Mejora 1: si estás siguiendo tu camino hacia un destino, la cámara encuadra los dos puntos.
    LaunchedEffect(estado) {
        if (estado is UbicacionUi.Lista) {
            val pos = LatLng(estado.posicion.latitud, estado.posicion.longitud)
            if (siguiendo && destino != null) {
                camara.animate(encuadrar(pos, LatLng(destino.latitud, destino.longitud)))
            } else {
                camara.animate(CameraUpdateFactory.newLatLngZoom(pos, 16f))
            }
        }
    }

    // Commit 7: al seleccionar un lugar (desde la lista), la cámara viaja hasta él con zoom 17.
    LaunchedEffect(seleccionado) {
        lugares.firstOrNull { it.lugar.id == seleccionado }?.let {
            val lugar = LatLng(it.lugar.latitud, it.lugar.longitud)
            if (siguiendo && estado is UbicacionUi.Lista) {
                camara.animate(encuadrar(LatLng(estado.posicion.latitud, estado.posicion.longitud), lugar))
            } else {
                camara.animate(CameraUpdateFactory.newLatLngZoom(lugar, 17f))
            }
        }
    }

    // Mejora 3 (punto azul nativo): el mapa dibuja tu ubicación en vivo como el punto azul de Google Maps.
    // Solo se activa si el permiso fue concedido; sin permiso, activarlo cerraría la app.
    GoogleMap(
        modifier = modifier,
        cameraPositionState = camara,
        properties = MapProperties(isMyLocationEnabled = usarPuntoAzul),
        uiSettings = MapUiSettings(
            myLocationButtonEnabled = usarPuntoAzul,
            zoomControlsEnabled = false,
            mapToolbarEnabled = false
        ),
        contentPadding = contentPadding,
        onMyLocationButtonClick = {
            onMiUbicacion()
            false // false = además se mueve la cámara a tu ubicación, como hace el mapa normalmente
        }
    ) {
        if (estado is UbicacionUi.Lista && destino != null) {
            if (ruta.size >= 2) {
                // Mejora 3: el camino a pie sigue las calles y veredas, no atraviesa los edificios.
                Polyline(
                    points = ruta.map { LatLng(it.latitud, it.longitud) },
                    color = Color(0xFF1B2E57),
                    width = 10f,
                    jointType = JointType.ROUND,
                    startCap = RoundCap(),
                    endCap = RoundCap()
                )
            } else {
                // Mejora 1: mientras llega la ruta (o si no hay internet) se muestra una línea recta punteada.
                Polyline(
                    points = listOf(
                        LatLng(estado.posicion.latitud, estado.posicion.longitud),
                        LatLng(destino.latitud, destino.longitud)
                    ),
                    color = Color(0xFF1B2E57),
                    width = 6f,
                    pattern = listOf(Dash(24f), Gap(16f))
                )
            }
        }

        // Commit 4: marcador rojo "Estás aquí", solo cuando ya se conoce la ubicación.
        // Mejora 3: si se usa el punto azul nativo, el marcador rojo sobra y se quita.
        if (estado is UbicacionUi.Lista && !usarPuntoAzul) {
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
