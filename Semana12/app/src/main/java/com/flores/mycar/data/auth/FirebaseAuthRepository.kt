package com.flores.mycar.data.auth

import com.flores.mycar.domain.model.Rol
import com.flores.mycar.domain.model.Sesion
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.firestore.FirebaseFirestore
import java.io.IOException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.tasks.await

/**
 * Autenticacion con Firebase Auth (correo y contraseña).
 * El rol NO se decide en la app: se lee de Firestore, en usuarios/{uid}.rol.
 */
class FirebaseAuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) : AuthRepository {

    override suspend fun iniciarSesion(correo: String, clave: String): Result<Sesion> = try {
        val uid = auth.signInWithEmailAndPassword(correo, clave).await().user!!.uid
        try {
            Result.success(leerSesion(uid, correo))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            auth.signOut()   // si no se puede leer el rol, no se deja la sesion a medias
            throw e
        }
    } catch (e: CancellationException) {
        throw e
    } catch (e: FirebaseAuthInvalidCredentialsException) {
        Result.failure(CredencialesInvalidasException())
    } catch (e: FirebaseAuthInvalidUserException) {
        Result.failure(CredencialesInvalidasException())
    } catch (e: FirebaseNetworkException) {
        Result.failure(IOException("Sin conexion", e))
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun sesionActual(): Sesion? {
        val usuario = auth.currentUser ?: return null
        return try {
            leerSesion(usuario.uid, usuario.email ?: "")
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            null   // sin poder confirmar el rol, se pide iniciar sesion de nuevo
        }
    }

    override fun cerrarSesion() = auth.signOut()

    private suspend fun leerSesion(uid: String, correo: String): Sesion {
        val doc = db.collection("usuarios").document(uid).get().await()
        val rol = if (doc.getString("rol") == "admin") Rol.ADMIN else Rol.USUARIO
        return Sesion(uid, correo, rol)
    }
}
