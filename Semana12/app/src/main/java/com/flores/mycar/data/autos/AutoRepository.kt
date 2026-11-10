package com.flores.mycar.data.autos

import com.flores.mycar.domain.model.Auto
import com.flores.mycar.domain.model.Cita
import com.flores.mycar.domain.model.EstadoAuto
import com.flores.mycar.domain.model.FichaAuto
import com.flores.mycar.domain.model.MarcaVenta
import com.flores.mycar.domain.model.Promocion
import com.flores.mycar.domain.model.UsuarioResumen
import kotlinx.coroutines.flow.Flow

/**
 * Contrato de los autos en venta (las marcas de NHTSA), los pedidos y las citas.
 * El catalogo sale de Room; los pedidos y las citas viven en Firestore.
 */
interface AutoRepository {
    /** Catalogo: una marca = un auto en venta, con precio y, cuando se encuentra, su foto. */
    val autos: Flow<List<Auto>>

    /** Catalogo por marcas: cada marca de la API con sus autos (modelos) ya cargados. */
    val catalogo: Flow<List<MarcaVenta>>

    /** Pide a la API los autos (modelos) de una marca; si ya los tiene, no hace nada. */
    suspend fun cargarAutos(idMarca: Int)

    /** Se asegura de tener cargadas las marcas de esos autos (carrito, favoritos, detalle). */
    suspend fun asegurar(ids: Collection<String>)

    /** Catalogo completo, tambien con lo que el administrador desactivo (solo para el panel del administrador). */
    val catalogoCompleto: Flow<List<MarcaVenta>>

    /** Marcas y autos desactivados (ids "marca_448" o "auto_448-m3"). */
    val desactivados: Flow<Set<String>>

    /** El administrador activa o desactiva una marca o un auto; lo desactivado deja de verse. */
    suspend fun cambiarActivo(clave: String, nombre: String, activo: Boolean): Result<Unit>

    /** Promociones (descuentos por mes) creadas por el administrador. */
    val promociones: Flow<List<Promocion>>

    suspend fun guardarPromocion(promocion: Promocion): Result<Unit>
    suspend fun cambiarPromocion(id: String, activa: Boolean): Result<Unit>
    suspend fun eliminarPromocion(id: String): Result<Unit>

    /** Personas registradas (solo para el administrador). */
    fun usuarios(): Flow<List<UsuarioResumen>>

    /** Horarios de cita que ya estan tomados (por cualquier persona). */
    val horariosOcupados: Flow<Set<String>>

    /** Pedidos de una persona; con uid = null devuelve todos (solo lo puede pedir el admin). */
    fun pedidos(uid: String?): Flow<List<Auto>>

    /** Citas de una persona; con uid = null devuelve todas (solo lo puede pedir el admin). */
    fun citas(uid: String?): Flow<List<Cita>>

    /** Fotos propias de la marca y su descripcion (Wikipedia, a partir de uno de sus modelos). */
    suspend fun ficha(auto: Auto): FichaAuto?

    /** Modelos de la marca (segundo endpoint de NHTSA). */
    suspend fun modelos(auto: Auto): List<String>

    /** Compra (simulada, sin cobro): queda un pedido "en proceso". */
    suspend fun comprar(auto: Auto, color: String, uid: String, correo: String, recojo: String = "", direccion: String = ""): Result<Unit>

    suspend fun cambiarEstado(pedidoId: String, estado: EstadoAuto): Result<Unit>
    suspend fun agendarCita(cita: Cita): Result<Unit>
    suspend fun confirmarCita(id: String): Result<Unit>
}
