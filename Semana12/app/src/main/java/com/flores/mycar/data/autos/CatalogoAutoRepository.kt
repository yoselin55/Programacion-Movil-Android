package com.flores.mycar.data.autos

import com.flores.mycar.data.fichas.FichaAutoRepository
import com.flores.mycar.data.repository.ItemRepository
import com.flores.mycar.domain.model.Auto
import com.flores.mycar.domain.model.Cita
import com.flores.mycar.domain.model.EstadoAuto
import com.flores.mycar.domain.model.FichaAuto
import com.flores.mycar.domain.model.MarcaVenta
import com.flores.mycar.domain.model.Promocion
import java.util.concurrent.ConcurrentHashMap
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit

/**
 * Catalogo de autos en venta con los dos servicios de la API de NHTSA:
 * - las marcas (primer endpoint, guardadas en Room, fuente unica de verdad);
 * - los autos de cada marca, o sea sus modelos (segundo servicio, por año), que se piden cuando la marca se muestra.
 * Solo estan en venta las marcas de MarcasEnVenta (10 principales, hasta 8 autos cada una).
 * Los autos que tienen foto propia (FotosLocales) salen primero en su marca.
 * El id de un auto es "idMarca-n" (con foto propia) o "idMarca-mN" (modelo de la API).
 * Los pedidos y las citas van a Firestore.
 */
class CatalogoAutoRepository(
    private val items: ItemRepository,
    private val fichas: FichaAutoRepository,
    private val remoto: FirebaseAutoRepository
) : AutoRepository {

    private val modelosPorMarca = MutableStateFlow<Map<Int, List<String>>>(emptyMap())
    private val descripciones = ConcurrentHashMap<String, FichaAuto>()
    private val limite = Semaphore(3)   // pocas consultas a la vez, para no saturar la red

    override val desactivados: Flow<Set<String>> = remoto.desactivados().catch { emit(emptySet()) }

    override val catalogoCompleto: Flow<List<MarcaVenta>> = combine(items.items, modelosPorMarca) { marcas, mapa ->
        marcas.filter { MarcasEnVenta.enVenta(it.titulo) }.sortedBy { MarcasEnVenta.orden(it.titulo) }.map { marca ->
            val propios = FotosLocales.autosDe(marca.titulo).mapIndexed { n, local ->
                Auto(
                    id = "${marca.id}-$n",
                    marca = marca.titulo,
                    modelo = local.modelo,
                    precio = precioDeReferencia(marca.id * 10 + n),
                    imagenUrl = local.url
                )
            }
            val modelos = mapa[marca.id]
            // Modelos de la API; se omiten los que ya estan como auto con foto propia (ej. "911" y "911 GT3").
            val delApi = modelos.orEmpty().take(60).mapIndexedNotNull { k, modelo ->
                if (propios.any { normal(it.modelo).contains(normal(modelo)) }) null
                else Auto(
                    id = "${marca.id}-m$k",
                    marca = marca.titulo,
                    modelo = modelo,
                    precio = precioDeReferencia(marca.id * 10 + 100 + k)
                )
            }
            MarcaVenta(marca.id, marca.titulo, (propios + delApi).take(maxOf(MarcasEnVenta.MAX_AUTOS, propios.size)), cargada = modelos != null)
        }
    }

    /** Lo que ve la gente: sin las marcas ni los autos que el administrador desactivo. */
    override val promociones: Flow<List<Promocion>> = remoto.promociones().catch { emit(emptyList()) }

    override val catalogo: Flow<List<MarcaVenta>> = combine(catalogoCompleto, desactivados, promociones) { marcas, apagados, promos ->
        val activas = promos.filter { it.activa }
        marcas.filter { "marca_${it.id}" !in apagados }.map { marca ->
            val descuento = activas.filter { it.marca.isBlank() || it.marca.equals(marca.nombre, ignoreCase = true) }
                .maxOfOrNull { it.descuento } ?: 0
            marca.copy(autos = marca.autos.filter { "auto_${it.id}" !in apagados }.map { conDescuento(it, descuento) })
        }
    }

    override val autos: Flow<List<Auto>> = catalogo.map { marcas -> marcas.flatMap { it.autos } }

    override suspend fun cambiarActivo(clave: String, nombre: String, activo: Boolean) = remoto.cambiarActivo(clave, nombre, activo)

    override fun usuarios() = remoto.usuarios()

    override suspend fun guardarPromocion(promocion: Promocion) = remoto.guardarPromocion(promocion)
    override suspend fun cambiarPromocion(id: String, activa: Boolean) = remoto.cambiarPromocion(id, activa)
    override suspend fun eliminarPromocion(id: String) = remoto.eliminarPromocion(id)

    override val horariosOcupados: Flow<Set<String>> = remoto.horariosOcupados()

    override fun pedidos(uid: String?) = remoto.pedidos(uid)
    override fun citas(uid: String?) = remoto.citas(uid)

    override suspend fun cargarAutos(idMarca: Int) {
        if (modelosPorMarca.value.containsKey(idMarca)) return
        limite.withPermit {
            if (modelosPorMarca.value.containsKey(idMarca)) return@withPermit
            val marca = items.items.first().firstOrNull { it.id == idMarca }?.titulo
            if (marca == null || !MarcasEnVenta.esPrincipal(marca)) {
                // Marcas que solo tienen autos con foto propia: no se piden modelos a la API.
                modelosPorMarca.update { it + (idMarca to emptyList()) }
                return@withPermit
            }
            // Autos de la marca del año elegido; si la API no trae ninguno, se usan sus modelos en general.
            // Si falla (sin internet) no se guarda nada y se reintenta la proxima vez.
            val delAnio = items.modelosDelAnio(idMarca, MarcasEnVenta.ANIO)
            val lista = delAnio.getOrNull()?.takeIf { it.isNotEmpty() }
                ?: if (delAnio.isSuccess) nombresDeAutos(items.obtenerDetalle(idMarca).getOrNull()?.modelos.orEmpty()) else null
            if (lista != null) modelosPorMarca.update { it + (idMarca to lista) }
        }
    }

    override suspend fun asegurar(ids: Collection<String>) {
        ids.mapNotNull { it.substringBefore('-').toIntOrNull() }.distinct().forEach { cargarAutos(it) }
    }

    /** Modelos que NHTSA registra para la marca del auto (segundo endpoint). */
    override suspend fun modelos(auto: Auto): List<String> {
        val idMarca = auto.id.substringBefore('-').toIntOrNull() ?: return emptyList()
        cargarAutos(idMarca)
        return modelosPorMarca.value[idMarca].orEmpty()
    }

    /** La foto es la del auto; la descripcion se toma de Wikipedia (primero el modelo, luego la marca). */
    override suspend fun ficha(auto: Auto): FichaAuto {
        val texto = descripcionDe(auto)
        return FichaAuto(
            descripcion = texto?.descripcion,
            imagenes = (listOf(auto.imagenUrl) + FotosLocales.extrasDe(auto.marca, auto.modelo)).filter { it.isNotBlank() },
            fuente = texto?.fuente ?: "Mycar",
            modelo = auto.modelo
        )
    }

    private suspend fun descripcionDe(auto: Auto): FichaAuto? {
        descripciones[auto.id]?.let { return it }
        return limite.withPermit {
            val ficha = listOf(auto.modelo, "").distinct()
                .firstNotNullOfOrNull { modelo -> fichas.ficha(auto.marca, modelo)?.takeIf { !it.descripcion.isNullOrBlank() } }
            ficha?.also { descripciones[auto.id] = it }
        }
    }

    override suspend fun comprar(auto: Auto, color: String, uid: String, correo: String, recojo: String, direccion: String) =
        remoto.comprar(auto, color, uid, correo, recojo, direccion)

    override suspend fun cambiarEstado(pedidoId: String, estado: EstadoAuto) = remoto.cambiarEstado(pedidoId, estado)
    override suspend fun agendarCita(cita: Cita) = remoto.agendarCita(cita)
    override suspend fun confirmarCita(id: String) = remoto.confirmarCita(id)

    /** Baja el precio segun el descuento (redondeado a decenas) y recuerda el precio original. */
    private fun conDescuento(auto: Auto, descuento: Int): Auto =
        if (descuento <= 0) auto
        else auto.copy(precioOriginal = auto.precio, precio = Math.round(auto.precio * (100 - descuento) / 1000.0) * 10.0, descuento = descuento)

    /** Para comparar nombres sin espacios ni guiones ("R 18 Classic" = "R18 Classic"). */
    private fun normal(s: String) = s.lowercase().filter { it.isLetterOrDigit() }

    companion object {
        /** Precio de referencia (simulado): siempre el mismo para el mismo auto. De S/ 60,000 a S/ 355,000. */
        fun precioDeReferencia(semilla: Int): Double = 60_000.0 + ((semilla.toLong() * 7919L) % 60L) * 5_000.0
    }
}
