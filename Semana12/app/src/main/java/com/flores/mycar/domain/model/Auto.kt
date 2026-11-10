package com.flores.mycar.domain.model

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/** Estados de un pedido: desde que la persona compra hasta que se entrega el auto. */
enum class EstadoAuto(val clave: String, val etiqueta: String) {
    DISPONIBLE("disponible", "Disponible"),
    PAGO_REALIZADO("pago_realizado", "En proceso"),
    PAGADO("pagado", "Pagado"),
    ENTREGADO("entregado", "Comprado"),
    NO_RECOGIDO("no_recogido", "No recogido");

    companion object {
        fun desde(clave: String?): EstadoAuto = entries.firstOrNull { it.clave == clave } ?: DISPONIBLE
    }
}

/**
 * Un auto en venta. Cada marca de la API de NHTSA es un auto del catalogo (id = id de la marca).
 * Cuando la persona compra, se guarda un pedido con la misma forma (id = id del pedido en Firestore).
 */
data class Auto(
    val id: String = "",
    val marca: String,
    val modelo: String = "",
    val precio: Double,
    val imagenUrl: String = "",
    val color: String = "",
    val estado: EstadoAuto = EstadoAuto.DISPONIBLE,
    val compradorUid: String? = null,
    val compradorCorreo: String? = null,
    val precioOriginal: Double = 0.0,   // precio sin descuento (0 si no hay promocion)
    val descuento: Int = 0,             // % de descuento vigente
    val direccion: String = "",         // direccion de la casa si pidio envio a domicilio
    val recojo: String = ""   // fecha (dd/MM/yyyy) en que la persona va a recoger su auto; vacia si no
) {
    val nombre: String get() = "$marca $modelo".trim()
}

/** Una marca de la API con sus autos (modelos). "cargada" es false mientras no se piden sus modelos. */
data class MarcaVenta(val id: Int, val nombre: String, val autos: List<Auto>, val cargada: Boolean)

enum class EstadoCita(val clave: String, val etiqueta: String) {
    PENDIENTE("pendiente", "Pendiente"),
    CONFIRMADA("confirmada", "Confirmada");

    companion object {
        fun desde(clave: String?): EstadoCita = entries.firstOrNull { it.clave == clave } ?: PENDIENTE
    }
}

/** Cita para ver un auto en persona (coleccion "citas"). */
data class Cita(
    val id: String = "",
    val autoId: String,
    val autoNombre: String,
    val uid: String,
    val correo: String,
    val fecha: String,
    val hora: String,
    val estado: EstadoCita = EstadoCita.PENDIENTE,
    val nombre: String = "",
    val nota: String = ""
)

/** Identifica un horario de cita (dia y hora). Dos citas no pueden tener la misma clave. */
fun claveHorario(fecha: String, hora: String): String = fecha.trim().replace('/', '-') + "_" + hora.trim().replace(":", "")

/** Una persona registrada (la ve el administrador). */
data class UsuarioResumen(val uid: String, val nombre: String, val correo: String, val rol: String, val ciudad: String)

/** true si la fecha "dd/MM/yyyy" ya paso (antes de hoy). */
fun fechaPasada(fecha: String): Boolean = try {
    val dia = SimpleDateFormat("dd/MM/yyyy", Locale.US).apply { isLenient = false }.parse(fecha)
    val hoy = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
    }.time
    dia != null && dia.before(hoy)
} catch (e: Exception) {
    false
}

/** Promocion del administrador: un descuento por mes en todas las marcas o en una. */
data class Promocion(
    val id: String = "",
    val titulo: String,
    val descripcion: String = "",
    val descuento: Int,
    val mes: String,
    val marca: String = "",       // vacio = todas las marcas
    val activa: Boolean = true
)
