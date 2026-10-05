package com.flores.saludplus.navigation

// Relaciones:
// - Lo usan AppNavigation (definir destinos) y las pantallas con barra inferior (Rutas.HOME, MIS_CITAS...)
// - No llama a ningún otro archivo

// Nombres de las rutas y constructores de las que llevan parámetros
object Rutas {
    const val SPLASH = "splash"
    const val REGISTRO = "registro"
    const val LOGIN = "login"
    const val TERMINOS = "terminos"
    const val HOME = "home"
    const val ESPECIALIDADES = "especialidades"
    const val MIS_CITAS = "mis_citas"
    const val RESULTADOS = "resultados"
    const val PERFIL = "perfil"
    const val NOTIFICACIONES = "notificaciones"

    const val MEDICOS = "medicos/{especialidadId}"
    const val FECHA_HORA = "fecha_hora/{medicoId}"
    const val CONFIRMAR = "confirmar/{medicoId}/{fecha}/{hora}"
    const val CITA_EXITOSA = "cita_exitosa/{citaId}"
    const val DETALLE_CITA = "detalle_cita/{citaId}"

    fun medicos(especialidadId: Int) = "medicos/$especialidadId"
    fun fechaHora(medicoId: Int) = "fecha_hora/$medicoId"
    fun confirmar(medicoId: Int, fecha: String, hora: String) = "confirmar/$medicoId/$fecha/$hora"
    fun citaExitosa(citaId: Int) = "cita_exitosa/$citaId"
    fun detalleCita(citaId: Int) = "detalle_cita/$citaId"
}
