package com.flores.mycar.data.auth

import com.flores.mycar.domain.model.Rol
import com.flores.mycar.domain.model.Sesion
import android.util.Log
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
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
        Log.w(ETIQUETA, "Inicio de sesion rechazado: ${e.errorCode} - ${e.message}")
        // Un correo mal escrito no es lo mismo que una contraseña incorrecta.
        Result.failure(if (e.errorCode == "ERROR_INVALID_EMAIL") CorreoInvalidoException() else CredencialesInvalidasException())
    } catch (e: FirebaseAuthInvalidUserException) {
        Log.w(ETIQUETA, "Inicio de sesion, usuario: ${e.errorCode} - ${e.message}")
        Result.failure(CredencialesInvalidasException())
    } catch (e: FirebaseNetworkException) {
        Log.w(ETIQUETA, "Inicio de sesion sin red: ${e.message}")
        Result.failure(IOException("Sin conexion", e))
    } catch (e: Exception) {
        Log.w(ETIQUETA, "Inicio de sesion fallo: ${e.javaClass.simpleName} - ${e.message}")
        Result.failure(e)
    }

    override suspend fun registrar(nombre: String, correo: String, clave: String): Result<Sesion> = try {
        val uid = auth.createUserWithEmailAndPassword(correo, clave).await().user!!.uid
        // El rol siempre es "usuario". Las reglas de Firestore impiden que alguien se cree como admin.
        // Si guardar el documento falla, la cuenta igual queda como usuario (es el rol por defecto).
        try {
            db.collection("usuarios").document(uid).set(mapOf("rol" to "usuario", "nombre" to nombre, "correo" to correo)).await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            // no es grave: se leera como usuario sin nombre
        }
        Result.success(Sesion(uid, correo, Rol.USUARIO, nombre))
    } catch (e: CancellationException) {
        throw e
    } catch (e: FirebaseAuthUserCollisionException) {
        Result.failure(CorreoEnUsoException())
    } catch (e: FirebaseAuthWeakPasswordException) {
        Result.failure(ClaveDebilException())
    } catch (e: FirebaseAuthInvalidCredentialsException) {
        Result.failure(CorreoInvalidoException())
    } catch (e: FirebaseNetworkException) {
        Result.failure(IOException("Sin conexion", e))
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun recuperarClave(correo: String): Result<Unit> = try {
        auth.sendPasswordResetEmail(correo).await()
        Result.success(Unit)
    } catch (e: CancellationException) {
        throw e
    } catch (e: FirebaseAuthInvalidCredentialsException) {
        Result.failure(CorreoInvalidoException())
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
        return Sesion(uid, correo, rol, doc.getString("nombre").orEmpty())
    }

    private companion object {
        const val ETIQUETA = "MycarAuth"
    }
}
