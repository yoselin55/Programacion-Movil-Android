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
import com.flores.saludplus.data.model.Usuario

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

    val medicos = listOf(
        Medico(1, "Dra. Ana Torres", 3, "12345", 4.9, 120, "Disponible hoy"),
        Medico(2, "Dra. Claudia Rojas", 3, "12346", 4.8, 86, "Disponible mañana"),
        Medico(3, "Dr. Luis Ramírez", 3, "12347", 4.7, 98, "Disponible hoy"),
        Medico(4, "Dra. Mariana Soto", 3, "12348", 4.6, 76, "Disponible esta semana"),
        Medico(5, "Dr. Carlos Mendoza", 1, "22110", 4.8, 140, "Disponible hoy"),
        Medico(6, "Dra. Lucía Vargas", 1, "22111", 4.5, 64, "Disponible mañana"),
        Medico(7, "Dra. Sofía Paredes", 2, "33120", 4.9, 110, "Disponible hoy"),
        Medico(8, "Dr. Jorge Salas", 2, "33121", 4.6, 58, "Disponible esta semana"),
        Medico(9, "Dr. Ricardo Núñez", 4, "44130", 4.9, 132, "Disponible mañana"),
        Medico(10, "Dra. Patricia León", 5, "55140", 4.7, 91, "Disponible hoy"),
        Medico(11, "Dr. Andrés Quispe", 6, "66150", 4.6, 73, "Disponible esta semana"),
        Medico(12, "Dra. Elena Campos", 7, "77160", 4.8, 85, "Disponible hoy")
    )

    // Horarios base de atención cada 30 minutos
    val horariosBase = listOf(
        "08:00", "08:30", "09:00", "09:30", "10:00", "10:30",
        "11:00", "11:30", "12:00"
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

    // ---- Médicos ----
    // Busca con find el médico por id (null si no existe)
    fun obtenerMedico(id: Int): Medico? = medicos.find { it.id == id }

    // Médicos de una especialidad (filter), del mejor al peor calificado (sortedByDescending)
    fun medicosPorEspecialidad(especialidadId: Int): List<Medico> =
        medicos.filter { it.especialidadId == especialidadId }.sortedByDescending { it.calificacion }

    // Igual que medicosPorEspecialidad pero también filtra por texto en el nombre del médico
    fun buscarMedicos(especialidadId: Int, texto: String): List<Medico> =
        medicosPorEspecialidad(especialidadId).filter { it.nombre.contains(texto.trim(), ignoreCase = true) }

    // ---- Citas ----
    // Horas ya reservadas del médico ese día (filter + map); se quitan de los horarios base
    fun horariosDisponibles(medicoId: Int, fecha: String): List<String> {
        val reservadas = citas.filter { it.medicoId == medicoId && it.fecha == fecha }.map { it.hora }
        return horariosBase.filter { it !in reservadas }
    }

    // Crea la cita si hay sesión y el horario sigue libre (any); si no, devuelve null
    // Fase 2: también devuelve null si el paciente ya tiene otra cita a esa fecha y hora (citaDelUsuarioEn)
    fun agendarCita(medicoId: Int, fecha: String, hora: String, motivo: String): Cita? {
        val usuario = usuarioActual ?: return null
        if (citas.any { it.medicoId == medicoId && it.fecha == fecha && it.hora == hora }) return null
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
