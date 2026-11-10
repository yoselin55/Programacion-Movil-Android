package com.flores.mycar.data.autos

import com.flores.mycar.domain.model.Auto
import com.flores.mycar.domain.model.Cita
import com.flores.mycar.domain.model.EstadoAuto
import com.flores.mycar.domain.model.EstadoCita
import com.flores.mycar.domain.model.Promocion
import com.flores.mycar.domain.model.UsuarioResumen
import com.flores.mycar.domain.model.claveHorario
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/** Pedidos y citas guardados en Cloud Firestore. Los cambios llegan en tiempo real. */
class FirebaseAutoRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    private val pedidosRef get() = db.collection("pedidos")
    private val citasRef get() = db.collection("citas")
    private val horariosRef get() = db.collection("horarios")
    private val desactivadosRef get() = db.collection("desactivados")

    fun pedidos(uid: String?): Flow<List<Auto>> = callbackFlow {
        val consulta: Query = if (uid == null) pedidosRef else pedidosRef.whereEqualTo("compradorUid", uid)
        val registro = consulta.addSnapshotListener { instantanea, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            val lista = instantanea?.documents.orEmpty()
                .sortedByDescending { it.getLong("creadoEn") ?: 0L }
                .map { it.aPedido() }
            trySend(lista)
        }
        awaitClose { registro.remove() }
    }

    /** Ids de los horarios ya tomados. Cualquier persona con sesion puede verlos (no tienen datos personales). */
    fun horariosOcupados(): Flow<Set<String>> = callbackFlow {
        val registro = horariosRef.addSnapshotListener { instantanea, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            trySend(instantanea?.documents.orEmpty().map { it.id }.toSet())
        }
        awaitClose { registro.remove() }
    }

    fun citas(uid: String?): Flow<List<Cita>> = callbackFlow {
        val consulta: Query = if (uid == null) citasRef else citasRef.whereEqualTo("uid", uid)
        val registro = consulta.addSnapshotListener { instantanea, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            val lista = instantanea?.documents.orEmpty()
                .sortedByDescending { it.getLong("creadoEn") ?: 0L }
                .map { it.aCita() }
            trySend(lista)
        }
        awaitClose { registro.remove() }
    }

    /** Marcas y autos que el administrador desactivo (ids como "marca_448" o "auto_448-m3"). */
    fun desactivados(): Flow<Set<String>> = callbackFlow {
        val registro = desactivadosRef.addSnapshotListener { instantanea, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            trySend(instantanea?.documents.orEmpty().map { it.id }.toSet())
        }
        awaitClose { registro.remove() }
    }

    suspend fun cambiarActivo(clave: String, nombre: String, activo: Boolean): Result<Unit> = ejecutar {
        if (activo) desactivadosRef.document(clave).delete().await()
        else desactivadosRef.document(clave).set(mapOf("nombre" to nombre, "creadoEn" to System.currentTimeMillis())).await()
    }

    /** Promociones del administrador (todas, activas o no). */
    fun promociones(): Flow<List<Promocion>> = callbackFlow {
        val registro = db.collection("promociones").addSnapshotListener { instantanea, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            trySend(
                instantanea?.documents.orEmpty()
                    .sortedByDescending { it.getLong("creadoEn") ?: 0L }
                    .map {
                        Promocion(
                            id = it.id,
                            titulo = it.getString("titulo").orEmpty(),
                            descripcion = it.getString("descripcion").orEmpty(),
                            descuento = (it.getLong("descuento") ?: 0L).toInt(),
                            mes = it.getString("mes").orEmpty(),
                            marca = it.getString("marca").orEmpty(),
                            activa = it.getBoolean("activa") ?: false
                        )
                    }
            )
        }
        awaitClose { registro.remove() }
    }

    suspend fun guardarPromocion(p: Promocion): Result<Unit> = ejecutar {
        db.collection("promociones").add(
            mapOf(
                "titulo" to p.titulo, "descripcion" to p.descripcion, "descuento" to p.descuento,
                "mes" to p.mes, "marca" to p.marca, "activa" to p.activa, "creadoEn" to System.currentTimeMillis()
            )
        ).await()
    }

    suspend fun cambiarPromocion(id: String, activa: Boolean): Result<Unit> = ejecutar {
        db.collection("promociones").document(id).update("activa", activa).await()
    }

    suspend fun eliminarPromocion(id: String): Result<Unit> = ejecutar {
        db.collection("promociones").document(id).delete().await()
    }

    /** Todas las personas registradas (solo lo puede pedir el administrador). */
    fun usuarios(): Flow<List<UsuarioResumen>> = callbackFlow {
        val registro = db.collection("usuarios").addSnapshotListener { instantanea, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            trySend(
                instantanea?.documents.orEmpty().map {
                    UsuarioResumen(
                        uid = it.id,
                        nombre = it.getString("nombre").orEmpty(),
                        correo = it.getString("correo").orEmpty(),
                        rol = it.getString("rol").orEmpty(),
                        ciudad = it.getString("ciudad").orEmpty()
                    )
                }.sortedBy { it.nombre.lowercase() }
            )
        }
        awaitClose { registro.remove() }
    }

    suspend fun comprar(auto: Auto, color: String, uid: String, correo: String, recojo: String, direccion: String): Result<Unit> = ejecutar {
        pedidosRef.add(
            mapOf(
                "catalogoId" to auto.id,
                "marca" to auto.marca,
                "modelo" to auto.modelo,
                "precio" to auto.precio,
                "imagenUrl" to auto.imagenUrl,
                "color" to color,
                "estado" to EstadoAuto.PAGO_REALIZADO.clave,
                "compradorUid" to uid,
                "compradorCorreo" to correo,
                "recojoFecha" to recojo,
                "direccion" to direccion,
                "creadoEn" to System.currentTimeMillis()
            )
        ).await()
    }

    suspend fun cambiarEstado(pedidoId: String, estado: EstadoAuto): Result<Unit> = ejecutar {
        pedidosRef.document(pedidoId).update("estado", estado.clave).await()
    }

    /** Guarda la cita y reserva su horario en un solo paso: si el horario ya esta tomado, no se guarda nada. */
    suspend fun agendarCita(cita: Cita): Result<Unit> = ejecutar {
        val lote = db.batch()
        lote.set(
            citasRef.document(),
            mapOf(
                "autoId" to cita.autoId,
                "autoNombre" to cita.autoNombre,
                "uid" to cita.uid,
                "correo" to cita.correo,
                "nombre" to cita.nombre,
                "nota" to cita.nota,
                "fecha" to cita.fecha,
                "hora" to cita.hora,
                "estado" to EstadoCita.PENDIENTE.clave,
                "creadoEn" to System.currentTimeMillis()
            )
        )
        lote.set(
            horariosRef.document(claveHorario(cita.fecha, cita.hora)),
            mapOf("uid" to cita.uid, "creadoEn" to System.currentTimeMillis())
        )
        lote.commit().await()
    }

    suspend fun confirmarCita(id: String): Result<Unit> = ejecutar {
        citasRef.document(id).update("estado", EstadoCita.CONFIRMADA.clave).await()
    }

    private suspend fun ejecutar(bloque: suspend () -> Unit): Result<Unit> = try {
        bloque()
        Result.success(Unit)
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Result.failure(e)
    }

    private fun DocumentSnapshot.aPedido() = Auto(
        id = id,
        marca = getString("marca").orEmpty(),
        modelo = getString("modelo").orEmpty(),
        precio = getDouble("precio") ?: 0.0,
        imagenUrl = getString("imagenUrl").orEmpty(),
        color = getString("color").orEmpty(),
        estado = EstadoAuto.desde(getString("estado")),
        compradorUid = getString("compradorUid"),
        compradorCorreo = getString("compradorCorreo"),
        recojo = getString("recojoFecha").orEmpty(),
        direccion = getString("direccion").orEmpty()
    )

    private fun DocumentSnapshot.aCita() = Cita(
        id = id,
        autoId = getString("autoId").orEmpty(),
        autoNombre = getString("autoNombre").orEmpty(),
        uid = getString("uid").orEmpty(),
        correo = getString("correo").orEmpty(),
        fecha = getString("fecha").orEmpty(),
        hora = getString("hora").orEmpty(),
        estado = EstadoCita.desde(getString("estado")),
        nombre = getString("nombre").orEmpty(),
        nota = getString("nota").orEmpty()
    )
}
