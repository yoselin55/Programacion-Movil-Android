package com.flores.saludplus.navigation

// Relaciones:
// - Lo usan AppNavigation (definir destinos), el menú lateral (MenuLateral.kt) y las pantallas con barra inferior
// - Fase 3: MIS_CITAS es la "Agenda" del menú lateral
// - No llama a ningún otro archivo

// Nombres de las rutas y constructores de las que llevan parámetros
object Rutas {
    const val SPLASH = "splash"
    const val REGISTRO = "registro"
    const val LOGIN = "login"
    const val TERMINOS = "terminos"
    const val HOME = "home"
    const val SEDES = "sedes"
    const val DOCTORES = "doctores"
    const val MIS_CITAS = "mis_citas"
    const val RESULTADOS = "resultados"
    const val PERFIL = "perfil"
    const val NOTIFICACIONES = "notificaciones"

    // Fase 3: el agendamiento empieza en una sede: Sedes > Sede > Especialidades > Médicos > Fecha y hora
    const val SEDE_DETALLE = "sede/{sedeId}"
    const val ESPECIALIDADES = "especialidades/{sedeId}"
    const val MEDICOS = "medicos/{sedeId}/{especialidadId}"
    // Fase 3: directorio de doctores: Doctores > Especialidad > datos de cada doctor
    const val DOCTORES_ESPECIALIDAD = "doctores/{especialidadId}"
    const val FECHA_HORA = "fecha_hora/{medicoId}"
    const val CONFIRMAR = "confirmar/{medicoId}/{fecha}/{hora}"
    const val CITA_EXITOSA = "cita_exitosa/{citaId}"
    const val DETALLE_CITA = "detalle_cita/{citaId}"

    fun sedeDetalle(sedeId: Int) = "sede/$sedeId"
    fun especialidades(sedeId: Int) = "especialidades/$sedeId"
    fun medicos(sedeId: Int, especialidadId: Int) = "medicos/$sedeId/$especialidadId"
    fun doctoresEspecialidad(especialidadId: Int) = "doctores/$especialidadId"
    fun fechaHora(medicoId: Int) = "fecha_hora/$medicoId"
    fun confirmar(medicoId: Int, fecha: String, hora: String) = "confirmar/$medicoId/$fecha/$hora"
    fun citaExitosa(citaId: Int) = "cita_exitosa/$citaId"
    fun detalleCita(citaId: Int) = "detalle_cita/$citaId"
}
