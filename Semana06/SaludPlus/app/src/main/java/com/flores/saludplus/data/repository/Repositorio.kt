package com.flores.saludplus.data.repository

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Female
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.WbSunny
import com.flores.saludplus.data.model.Cita
import com.flores.saludplus.data.model.Especialidad
import com.flores.saludplus.data.model.Medico
import com.flores.saludplus.data.model.Sede
import com.flores.saludplus.data.model.Usuario
import com.flores.saludplus.util.Fechas
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeParseException

// Relaciones:
// - Usa los modelos Usuario, Especialidad, Medico y Cita (data/model)
// - Lo llamarán las pantallas de ui/screens para leer y guardar datos
// - Es un object único: sus colecciones se comparten en toda la app
// - Fase 2: citaDelUsuarioEn lo usan FechaHoraScreen y ConfirmarCitaScreen para evitar cruces de citas

// Datos en memoria (sin base de datos): se pierden al cerrar la app
object Repositorio {

    // ---- Colecciones ----
    val usuarios = mutableListOf<Usuario>()
    val citas = mutableListOf<Cita>()
    var usuarioActual: Usuario? = null

    val especialidades = listOf(
        Especialidad(1, "Medicina General", "Atención integral", Icons.Filled.Person),
        Especialidad(2, "Pediatría", "Niños y adolescentes", Icons.Filled.ChildCare),
        Especialidad(3, "Ginecología", "Salud de la mujer", Icons.Filled.Female),
        Especialidad(4, "Cardiología", "Corazón y presión sanguínea", Icons.Filled.Favorite),
        Especialidad(5, "Dermatología", "Piel, cabello y uñas", Icons.Filled.WbSunny),
        Especialidad(6, "Traumatología", "Huesos y articulaciones", Icons.Filled.Healing),
        Especialidad(7, "Oftalmología", "Salud visual", Icons.Filled.Visibility)
    )

    // Fase 3: sedes de la clínica
    val sedes = listOf(
        Sede(1, "Santa Anita", "Av. Los Ficus 450, Santa Anita", "(01) 715-2200"),
        Sede(2, "Ate", "Av. Nicolás Ayllón 3120, Ate", "(01) 715-2300"),
        Sede(3, "La Molina", "Av. La Molina 1850, La Molina", "(01) 715-2400"),
        Sede(4, "San Isidro", "Av. Conquistadores 640, San Isidro", "(01) 715-2500")
    )

    // Fase 3: duración de cada consulta (y separación entre horarios) en minutos
    private const val MINUTOS_CONSULTA = 30

    // Fase 3: horas de inicio cada 30 minutos desde "desde" hasta "hasta" (la hora final no se incluye)
    private fun horas(desde: String, hasta: String): List<String> {
        val lista = mutableListOf<String>()
        var hora = LocalTime.parse(desde)
        val fin = LocalTime.parse(hasta)
        while (hora.isBefore(fin)) {
            lista.add(hora.toString())
            hora = hora.plusMinutes(MINUTOS_CONSULTA.toLong())
        }
        return lista
    }

    // Fase 3: turnos de atención (4 horas): mañana de 08:00 a 12:00 y tarde de 15:00 a 19:00
    private val manana = horas("08:00", "12:00")
    private val tarde = horas("15:00", "19:00")

    // Fase 3: arma el horario de un médico: cada día indicado con las mismas horas
    private fun agenda(dias: List<DayOfWeek>, horas: List<String>): Map<DayOfWeek, List<String>> =
        dias.associateWith { horas }

    private val lunMieVie = listOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY)
    private val marJue = listOf(DayOfWeek.TUESDAY, DayOfWeek.THURSDAY)
    private val lunMarJue = listOf(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.THURSDAY)
    private val marMieVie = listOf(DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY)
    private val lunAVie = listOf(
        DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY
    )

    // Fase 3: cada médico atiende en una sola sede y solo en sus días y horas
    // (id, nombre, especialidad, sede, código, teléfono, calificación, reseñas, horario)
    val medicos = listOf(
        Medico(1, "Dra. Ana Torres", 3, 1, "12345", "(01) 715-3101", 4.9, 120, agenda(lunAVie, manana)),
        Medico(2, "Dra. Claudia Rojas", 3, 2, "12346", "(01) 715-3102", 4.8, 86, agenda(marJue, tarde)),
        Medico(3, "Dr. Luis Ramírez", 3, 3, "12347", "(01) 715-3103", 4.7, 98, agenda(lunMieVie, tarde)),
        Medico(4, "Dra. Mariana Soto", 3, 4, "12348", "(01) 715-3104", 4.6, 76, agenda(marMieVie, manana)),
        Medico(5, "Dr. Carlos Mendoza", 1, 1, "22110", "(01) 715-3105", 4.8, 140, agenda(lunAVie, tarde)),
        Medico(6, "Dra. Lucía Vargas", 1, 2, "22111", "(01) 715-3106", 4.5, 64, agenda(lunMieVie, manana)),
        Medico(7, "Dra. Sofía Paredes", 2, 1, "33120", "(01) 715-3107", 4.9, 110, agenda(lunMarJue, manana)),
        Medico(8, "Dr. Jorge Salas", 2, 3, "33121", "(01) 715-3108", 4.6, 58, agenda(marJue, manana)),
        Medico(9, "Dr. Ricardo Núñez", 4, 4, "44130", "(01) 715-3109", 4.9, 132, agenda(lunMieVie, tarde)),
        Medico(10, "Dra. Patricia León", 5, 3, "55140", "(01) 715-3110", 4.7, 91, agenda(marMieVie, tarde)),
        Medico(11, "Dr. Andrés Quispe", 6, 2, "66150", "(01) 715-3111", 4.6, 73, agenda(lunMarJue, tarde)),
        Medico(12, "Dra. Elena Campos", 7, 1, "77160", "(01) 715-3112", 4.8, 85, agenda(lunAVie, manana)),
        Medico(13, "Dr. Miguel Herrera", 1, 3, "22112", "(01) 715-3113", 4.4, 52, agenda(marJue, manana)),
        Medico(14, "Dra. Valeria Cruz", 1, 4, "22113", "(01) 715-3114", 4.7, 69, agenda(lunMieVie, tarde)),
        Medico(15, "Dra. Camila Ortega", 2, 2, "33122", "(01) 715-3115", 4.8, 77, agenda(marMieVie, tarde)),
        Medico(16, "Dr. Fernando Díaz", 2, 4, "33123", "(01) 715-3116", 4.5, 49, agenda(lunAVie, tarde)),
        Medico(17, "Dra. Rosa Medina", 4, 1, "44131", "(01) 715-3117", 4.8, 101, agenda(marJue, tarde)),
        Medico(18, "Dr. Héctor Rivas", 4, 2, "44132", "(01) 715-3118", 4.6, 66, agenda(lunMarJue, manana)),
        Medico(19, "Dra. Natalia Silva", 5, 4, "55141", "(01) 715-3119", 4.6, 55, agenda(lunMieVie, manana)),
        Medico(20, "Dr. Óscar Benítez", 6, 1, "66151", "(01) 715-3120", 4.7, 88, agenda(marMieVie, manana)),
        Medico(21, "Dr. Daniel Flores", 7, 3, "77161", "(01) 715-3121", 4.5, 61, agenda(lunMieVie, tarde))
    )

    // ---- Usuarios ----
    // Registra al usuario; devuelve false si el teléfono ya existe (any) y si no lo agrega (add)
    fun registrarUsuario(nombre: String, telefono: String, correo: String, contrasena: String): Boolean {
        if (usuarios.any { it.telefono == telefono }) return false
        val nuevo = Usuario(usuarios.size + 1, nombre, telefono, correo, contrasena)
        usuarios.add(nuevo)
        usuarioActual = nuevo // queda con la sesión iniciada
        return true
    }

    // Busca con find el usuario que coincida; si existe lo deja como usuarioActual
    fun iniciarSesion(telefono: String, contrasena: String): Boolean {
        val encontrado = usuarios.find { it.telefono == telefono && it.contrasena == contrasena }
        usuarioActual = encontrado
        return encontrado != null
    }

    // Cierra la sesión actual
    fun cerrarSesion() {
        usuarioActual = null
    }

    // ---- Especialidades ----
    // Filtra con filter + contains (sin importar mayúsculas) por nombre; con texto vacío devuelve todas
    fun buscarEspecialidades(texto: String): List<Especialidad> =
        especialidades.filter { it.nombre.contains(texto.trim(), ignoreCase = true) }

    // Toma con take las 3 primeras especialidades para el LazyRow de Inicio
    fun especialidadesDestacadas(): List<Especialidad> = especialidades.take(3)

    // Busca con find la especialidad por id (null si no existe)
    fun obtenerEspecialidad(id: Int): Especialidad? = especialidades.find { it.id == id }

    // ---- Sedes ----
    // Busca con find la sede por id (null si no existe)
    fun obtenerSede(id: Int): Sede? = sedes.find { it.id == id }

    // Fase 3: especialidades que tienen al menos un médico en la sede (filter + any)
    fun especialidadesDeSede(sedeId: Int): List<Especialidad> =
        especialidades.filter { esp -> medicos.any { it.sedeId == sedeId && it.especialidadId == esp.id } }

    // ---- Médicos ----
    // Busca con find el médico por id (null si no existe)
    fun obtenerMedico(id: Int): Medico? = medicos.find { it.id == id }

    // Médicos de una especialidad (filter), del mejor al peor calificado (sortedByDescending)
    // Fase 3: sedeId opcional; si se indica, solo los médicos de esa sede
    fun medicosPorEspecialidad(especialidadId: Int, sedeId: Int? = null): List<Medico> =
        medicos.filter { it.especialidadId == especialidadId && (sedeId == null || it.sedeId == sedeId) }
            .sortedByDescending { it.calificacion }

    // Igual que medicosPorEspecialidad pero también filtra por texto en el nombre del médico
    fun buscarMedicos(especialidadId: Int, texto: String, sedeId: Int? = null): List<Medico> =
        medicosPorEspecialidad(especialidadId, sedeId).filter { it.nombre.contains(texto.trim(), ignoreCase = true) }

    // Fase 3: resumen de los días y horas de atención, por ejemplo "Lun, Mié y Vie · 08:00 a 12:00"
    fun resumenHorario(medico: Medico): String {
        val dias = medico.horario.filterValues { it.isNotEmpty() }.keys.sortedBy { it.value }
        if (dias.isEmpty()) return "Sin horarios de atención"
        val nombres = dias.map { Fechas.nombreCorto(it) }
        // Fase 3: varios días seguidos se resumen como "Lun a Vie"
        val seguidos = dias.size >= 3 && dias.last().value - dias.first().value == dias.size - 1
        val textoDias = when {
            nombres.size == 1 -> nombres.first()
            seguidos -> "${nombres.first()} a ${nombres.last()}"
            else -> nombres.dropLast(1).joinToString(", ") + " y " + nombres.last()
        }
        val horas = medico.horasDe(dias.first())
        val rango = Fechas.rangoHora(horas.first()).substringBefore(" a ") + " a " +
            Fechas.rangoHora(horas.last()).substringAfter(" a ")
        return "$textoDias · $rango"
    }

    // Fase 3: etiqueta de disponibilidad del médico: "Disponible hoy", "Disponible mañana" o
    // "Próx. Lun 12 oct"; busca hasta 60 días hacia adelante y, si no hay, "Sin horarios"
    fun etiquetaDisponibilidad(medicoId: Int, ahora: LocalDateTime = LocalDateTime.now()): String {
        val hoy = ahora.toLocalDate()
        for (dias in 0L..60L) {
            val fecha = hoy.plusDays(dias)
            if (horariosDisponibles(medicoId, fecha.toString(), ahora).isEmpty()) continue
            return when (dias) {
                0L -> "Disponible hoy"
                1L -> "Disponible mañana"
                else -> "Próx. ${Fechas.textoDiaMes(fecha)}"
            }
        }
        return "Sin horarios"
    }

    // ---- Citas ----
    // Fase 3: horas libres de un médico en una fecha ISO. Parte de las horas en que el médico atiende
    // ese día de la semana y quita las ya reservadas (filter + map) y las que ya pasaron si la fecha es hoy.
    // Devuelve vacía si el médico no existe, la fecha no es válida, es un día pasado o no atiende ese día.
    fun horariosDisponibles(
        medicoId: Int,
        fecha: String,
        ahora: LocalDateTime = LocalDateTime.now()
    ): List<String> {
        val medico = obtenerMedico(medicoId) ?: return emptyList()
        val dia = try {
            LocalDate.parse(fecha)
        } catch (e: DateTimeParseException) {
            return emptyList()
        }
        if (dia.isBefore(ahora.toLocalDate())) return emptyList()
        val reservadas = citas.filter { it.medicoId == medicoId && it.fecha == fecha }.map { it.hora }
        return medico.horasDe(dia.dayOfWeek).filter { hora ->
            hora !in reservadas && (dia.isAfter(ahora.toLocalDate()) || LocalTime.parse(hora).isAfter(ahora.toLocalTime()))
        }
    }

    // Crea la cita si hay sesión y la hora está entre los horarios disponibles del médico (día de atención,
    // no reservada y no pasada); si no, devuelve null
    // Fase 2: también devuelve null si el paciente ya tiene otra cita a esa fecha y hora (citaDelUsuarioEn)
    fun agendarCita(
        medicoId: Int,
        fecha: String,
        hora: String,
        motivo: String,
        ahora: LocalDateTime = LocalDateTime.now()
    ): Cita? {
        val usuario = usuarioActual ?: return null
        if (hora !in horariosDisponibles(medicoId, fecha, ahora)) return null
        if (citaDelUsuarioEn(fecha, hora) != null) return null
        val nueva = Cita((citas.maxOfOrNull { it.id } ?: 0) + 1, usuario.id, medicoId, fecha, hora, motivo)
        citas.add(nueva)
        return nueva
    }

    // Citas del usuario en sesión (filter), de la más próxima a la más lejana (sortedWith)
    fun citasDelUsuario(): List<Cita> =
        citas.filter { it.usuarioId == usuarioActual?.id }
            .sortedWith(compareBy({ it.fecha }, { it.hora }))

    // Fase 2: busca con find, entre las citas del usuario en sesión, la que tenga exactamente esa fecha
    // y hora (con cualquier médico); devuelve null si no hay ninguna
    fun citaDelUsuarioEn(fecha: String, hora: String): Cita? =
        citasDelUsuario().find { it.fecha == fecha && it.hora == hora }

    // Busca con find la cita por id (null si no existe)
    fun obtenerCita(id: Int): Cita? = citas.find { it.id == id }

    // Fase 2: elimina con removeIf la cita con ese id; devuelve true si se eliminó y false si no existía.
    // Al quitarla, su hora vuelve a aparecer en horariosDisponibles
    fun cancelarCita(id: Int): Boolean = citas.removeIf { it.id == id }
}
