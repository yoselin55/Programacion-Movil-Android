package com.flores.mycar.data.perfil

import com.flores.mycar.domain.model.PerfilDatos
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.tasks.await

/** Datos personales de la persona, guardados en su documento de Firestore (usuarios/{uid}). */
interface PerfilRepository {
    suspend fun leer(uid: String): PerfilDatos
    suspend fun guardar(uid: String, datos: PerfilDatos): Result<Unit>
}

class FirebasePerfilRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) : PerfilRepository {

    override suspend fun leer(uid: String): PerfilDatos = try {
        val doc = db.collection("usuarios").document(uid).get().await()
        PerfilDatos(
            nombre = doc.getString("nombre").orEmpty(),
            telefono = doc.getString("telefono").orEmpty(),
            ciudad = doc.getString("ciudad").orEmpty()
        )
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        PerfilDatos()
    }

    override suspend fun guardar(uid: String, datos: PerfilDatos): Result<Unit> = try {
        // Solo se cambian estos tres campos; el rol no se puede tocar (lo impiden las reglas).
        db.collection("usuarios").document(uid).update(
            mapOf("nombre" to datos.nombre, "telefono" to datos.telefono, "ciudad" to datos.ciudad)
        ).await()
        Result.success(Unit)
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Result.failure(e)
    }
}
