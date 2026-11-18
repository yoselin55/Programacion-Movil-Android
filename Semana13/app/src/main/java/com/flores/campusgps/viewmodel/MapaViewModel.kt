package com.flores.campusgps.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flores.campusgps.data.GpsApagadoException
import com.flores.campusgps.data.LugaresRepository
import com.flores.campusgps.data.PreferenciasRepository
import com.flores.campusgps.data.RutasRepository
import com.flores.campusgps.data.UbicacionRepository
import com.flores.campusgps.model.Lugar
import com.flores.campusgps.model.LugarConDistancia
import com.flores.campusgps.model.Posicion
import com.flores.campusgps.model.TipoError
import com.flores.campusgps.model.UbicacionUi
import com.flores.campusgps.util.contieneBusqueda
import com.flores.campusgps.util.distanciaMetros
import com.flores.campusgps.util.formatearDistancia
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// Commit 1: ViewModel con el estado de la ubicación; los TODO se completan en los commits 6 al 9.
class MapaViewModel(
    private val ubicacionRepo: UbicacionRepository,
    private val lugaresRepo: LugaresRepository,
    private val preferenciasRepo: PreferenciasRepository, // Mejora 2: guarda el último lugar visitado
    private val rutasRepo: RutasRepository // Mejora 3: calcula el camino a pie por las calles
) : ViewModel() {

    private val _estado = MutableStateFlow<UbicacionUi>(UbicacionUi.Inicial)
    val estado: StateFlow<UbicacionUi> = _estado.asStateFlow()

    private val _lugares = MutableStateFlow(
        lugaresRepo.obtenerLugares().map { LugarConDistancia(it, null) }
    )
    val lugares: StateFlow<List<LugarConDistancia>> = _lugares.asStateFlow()

    private val _seleccionado = MutableStateFlow<Int?>(null)
    val seleccionado: StateFlow<Int?> = _seleccionado.asStateFlow()

    // Mejora 1 (seguimiento en vivo): true mientras se están pidiendo actualizaciones de ubicación.
    private val _siguiendo = MutableStateFlow(false)
    val siguiendo: StateFlow<Boolean> = _siguiendo.asStateFlow()

    // Mejora 1: lugar al que acabas de llegar (a menos de 20 m); la pantalla muestra el aviso.
    private val _llegada = MutableStateFlow<Lugar?>(null)
    val llegada: StateFlow<Lugar?> = _llegada.asStateFlow()

    private var trabajoSeguimiento: Job? = null
    private var llegadaAvisadaId: Int? = null

    // Mejora 3: puntos del camino a pie hacia el destino; vacío si no hay destino o no se pudo calcular.
    private val _ruta = MutableStateFlow<List<Posicion>>(emptyList())
    val ruta: StateFlow<List<Posicion>> = _ruta.asStateFlow()

    private var trabajoRuta: Job? = null
    private var origenUltimaRuta: Posicion? = null
    private var destinoUltimaRuta: Int? = null

    init {
        // Mejora 2: al abrir la app se recupera el último lugar visitado y queda seleccionado.
        viewModelScope.launch {
            val id = preferenciasRepo.ultimoLugarId.first()
            if (id != null && _seleccionado.value == null) _seleccionado.value = id
        }
    }

    fun buscar() {
        // Commit 9: antes de buscar se consulta si hay Google Play Services (sin usar Context).
        if (!ubicacionRepo.playServicesDisponible()) {
            _estado.value = UbicacionUi.Error(
                "Este dispositivo no tiene Google Play Services",
                TipoError.SIN_PLAY_SERVICES
            )
            return
        }
        _estado.value = UbicacionUi.Buscando
        viewModelScope.launch {
            ubicacionRepo.obtenerPosicion()
                .onSuccess {
                    _estado.value = UbicacionUi.Lista(it)
                    actualizarDistancias(it) // Commit 6: solo se calcula cuando la ubicación llega con éxito
                    actualizarRuta(it) // Mejora 3: si ya hay un destino elegido, se calcula el camino
                }
                .onFailure { _estado.value = errorDe(it) }
        }
    }

    // Mejora 1: inicia o detiene el seguimiento en vivo (botón Iniciar/Detener).
    fun alternarSeguimiento() {
        if (_siguiendo.value) detenerSeguimiento() else iniciarSeguimiento()
    }

    private fun iniciarSeguimiento() {
        if (!ubicacionRepo.playServicesDisponible()) {
            _estado.value = UbicacionUi.Error(
                "Este dispositivo no tiene Google Play Services",
                TipoError.SIN_PLAY_SERVICES
            )
            return
        }
        _siguiendo.value = true
        llegadaAvisadaId = null
        trabajoSeguimiento = viewModelScope.launch {
            ubicacionRepo.seguirPosicion()
                .catch { // si falla (por ejemplo, sin permiso) se muestra el error y se detiene
                    _estado.value = errorDe(it)
                    _siguiendo.value = false
                }
                .collect { pos ->
                    // Cada posición nueva actualiza el estado, las distancias y el aviso de llegada.
                    _estado.value = UbicacionUi.Lista(pos)
                    actualizarDistancias(pos)
                    actualizarRuta(pos) // Mejora 3: la ruta se recalcula solo si te moviste lo suficiente
                    revisarLlegada()
                }
        }
    }

    fun detenerSeguimiento() {
        trabajoSeguimiento?.cancel() // al cancelar, el Repository deja de pedir ubicaciones
        trabajoSeguimiento = null
        _siguiendo.value = false
    }

    // Mejora 1: si el lugar elegido como destino está a menos de 20 m, avisa una sola vez.
    private fun revisarLlegada() {
        val id = _seleccionado.value ?: return
        val destino = _lugares.value.firstOrNull { it.lugar.id == id } ?: return
        val metros = destino.metros ?: return
        if (metros < 20f && llegadaAvisadaId != id) {
            llegadaAvisadaId = id
            _llegada.value = destino.lugar
        }
    }

    fun cerrarLlegada() {
        _llegada.value = null
    }

    fun permisoDenegado() {
        _estado.value = UbicacionUi.Error(
            "Sin permiso de ubicación no puedo mostrar dónde estás",
            TipoError.SIN_PERMISO
        )
    }

    // Commit 6: calcula la distancia a cada lugar, ordena de menor a mayor y publica la lista.
    // Se hace aquí en el ViewModel y no en un Composable, para respetar MVVM.
    private fun actualizarDistancias(pos: Posicion) {
        _lugares.value = lugaresRepo.obtenerLugares()
            .map { LugarConDistancia(it, distanciaMetros(pos, it)) }
            .sortedBy { it.metros }
        // Commit 6: para comprobar el orden en Logcat (filtrar por "CampusGPS").
        Log.d("CampusGPS", _lugares.value.joinToString { "${it.lugar.nombre}=${it.metros?.let(::formatearDistancia)}" })
    }

    // Commit 7: guarda el lugar seleccionado; el mapa y la lista reaccionan a este valor.
    fun seleccionar(id: Int?) {
        if (id != _seleccionado.value) llegadaAvisadaId = null // Mejora 1: un destino nuevo vuelve a avisar
        _seleccionado.value = id
        // Mejora 2: cada vez que se elige un lugar, se guarda como el último visitado.
        // Mejora 3: además entra al historial de recientes y, si había algo buscado, se guarda esa búsqueda.
        if (id != null) {
            viewModelScope.launch {
                preferenciasRepo.guardarUltimoLugar(id)
                preferenciasRepo.agregarReciente(id)
            }
            guardarBusqueda()
        }
        // Mejora 3: al elegir un destino nuevo (y conocer tu posición) se calcula el camino hasta él.
        val actual = _estado.value
        if (actual is UbicacionUi.Lista) actualizarRuta(actual.posicion) else if (id == null) actualizarRuta(null)
    }

    // Mejora 3: pide al Repository el camino a pie hasta el destino elegido.
    // Para no llamar a la red en cada actualización, solo se repite si cambió el destino
    // o si te moviste más de 25 m desde donde se calculó la última ruta.
    private fun actualizarRuta(origen: Posicion?) {
        val id = _seleccionado.value
        val destino = lugaresRepo.obtenerLugares().firstOrNull { it.id == id }
        if (origen == null || destino == null) {
            trabajoRuta?.cancel()
            _ruta.value = emptyList()
            origenUltimaRuta = null
            destinoUltimaRuta = null
            return
        }
        val ultimo = origenUltimaRuta
        val mismoDestino = destinoUltimaRuta == destino.id
        if (mismoDestino && ultimo != null && distanciaMetros(ultimo, origen) < 25f) return

        origenUltimaRuta = origen
        destinoUltimaRuta = destino.id
        if (!mismoDestino) _ruta.value = emptyList() // mientras llega la nueva, no se muestra la vieja
        trabajoRuta?.cancel()
        trabajoRuta = viewModelScope.launch {
            rutasRepo.obtenerRuta(origen, Posicion(destino.latitud, destino.longitud))
                .onSuccess { if (_seleccionado.value == destino.id) _ruta.value = it }
                .onFailure { _ruta.value = emptyList() } // sin ruta: el mapa dibuja una línea recta
        }
    }

    // Mejora 2 (filtrar por categoría): categorías disponibles (Estudio, Comida, Eventos, Servicios).
    val categorias: List<String> = lugaresRepo.obtenerLugares().map { it.categoria }.distinct()

    // Mejora 2: categoría elegida en los chips; null significa "Todos".
    private val _categoria = MutableStateFlow<String?>(null)
    val categoria: StateFlow<String?> = _categoria.asStateFlow()

    // Mejora 3 (búsqueda): texto que el usuario escribe en la barra de búsqueda.
    private val _consulta = MutableStateFlow("")
    val consulta: StateFlow<String> = _consulta.asStateFlow()

    fun cambiarConsulta(texto: String) {
        _consulta.value = texto
    }

    // Mejora 3: guarda la búsqueda actual en el historial (al pulsar Buscar o al elegir un resultado).
    fun guardarBusqueda() {
        val texto = _consulta.value.trim()
        if (texto.isNotEmpty()) viewModelScope.launch { preferenciasRepo.agregarBusqueda(texto) }
    }

    // Mejora 2 + Mejora 3: los lugares que se muestran en el mapa y en la lista.
    // Cumplen a la vez la categoría elegida y el texto buscado (en el nombre o en la categoría).
    val lugaresVisibles: StateFlow<List<LugarConDistancia>> =
        combine(_lugares, _categoria, _consulta) { lista, cat, texto ->
            lista.filter { item ->
                (cat == null || item.lugar.categoria == cat) &&
                    (texto.isBlank() ||
                        item.lugar.nombre.contieneBusqueda(texto) ||
                        item.lugar.categoria.contieneBusqueda(texto))
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), _lugares.value)

    // Mejora 3 (recientes): los últimos lugares que visitó el usuario, del más nuevo al más viejo.
    val recientes: StateFlow<List<Lugar>> = preferenciasRepo.recientes
        .map { ids -> ids.mapNotNull { id -> lugaresRepo.obtenerLugares().firstOrNull { it.id == id } } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Mejora 3: las últimas búsquedas guardadas.
    val busquedasRecientes: StateFlow<List<String>> = preferenciasRepo.busquedas
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Mejora 3 (perfil): borra los recientes y las búsquedas guardadas.
    fun borrarHistorial() {
        viewModelScope.launch { preferenciasRepo.borrarHistorial() }
    }

    // Mejora 2: cambia el filtro; si el lugar seleccionado queda oculto, se quita la selección.
    fun filtrarPor(cat: String?) {
        _categoria.value = cat
        val id = _seleccionado.value
        if (cat != null && id != null && _lugares.value.none { it.lugar.id == id && it.lugar.categoria == cat }) {
            seleccionar(null)
        }
    }

    // Commit 8: el lugar más cercano se deriva de la lista ordenada (el primero con distancia calculada).
    // Mejora 2: ahora se calcula sobre los lugares visibles (el más cercano de la categoría elegida).
    // Es null mientras todavía no hay ubicación.
    val masCercano: StateFlow<Lugar?> = lugaresVisibles
        .map { lista -> lista.firstOrNull { it.metros != null }?.lugar }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Commit 9: convierte cada fallo en un error con su mensaje y su tipo.
    private fun errorDe(e: Throwable): UbicacionUi.Error = when (e) {
        is SecurityException -> UbicacionUi.Error("Falta el permiso de ubicación", TipoError.SIN_PERMISO)
        is GpsApagadoException -> UbicacionUi.Error("Activa el GPS e intenta de nuevo", TipoError.GPS_APAGADO)
        else -> UbicacionUi.Error(e.message ?: "No se pudo obtener la ubicación", TipoError.OTRO)
    }
}
