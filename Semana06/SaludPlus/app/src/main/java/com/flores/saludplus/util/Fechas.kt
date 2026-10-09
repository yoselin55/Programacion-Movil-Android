package com.flores.saludplus.util

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

// Relaciones:
// - Lo usará FechaHoraScreen (ui/screens/agendamiento) para armar el calendario dinámico
// - Lo usan ConfirmarCitaScreen, CitaExitosaScreen y TarjetaCita para mostrar fechas ISO en español
// - Fase 2: lo usan DetalleCitaScreen, NotificacionesScreen y ResultadosScreen para las fechas
// - Fase 2: rangoHora lo usan ConfirmarCitaScreen, CitaExitosaScreen y DetalleCitaScreen
// - Lo prueba FechasTest (app/src/test)
// - Depende solo de java.time (LocalDate y LocalTime; requiere minSdk 26)

// Fase 2: utilidades de fechas en español, sin depender del Locale del dispositivo
object Fechas {

    // Fase 2: nombres propios en español (índice 0 = lunes / enero)
    private val diasCortos = listOf("Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom")
    private val diasLargos = listOf(
        "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"
    )
    private val meses = listOf(
        "enero", "febrero", "marzo", "abril", "mayo", "junio",
        "julio", "agosto", "setiembre", "octubre", "noviembre", "diciembre"
    )
    private val mesesCortos = listOf(
        "ene", "feb", "mar", "abr", "may", "jun",
        "jul", "ago", "set", "oct", "nov", "dic"
    )

    // Fase 2: indica si la fecha cae de lunes a viernes.
    // Devuelve true si es día hábil.
    fun esHabil(fecha: LocalDate): Boolean =
        fecha.dayOfWeek != DayOfWeek.SATURDAY && fecha.dayOfWeek != DayOfWeek.SUNDAY

    // Fase 2: devuelve la misma fecha si es hábil; si cae en fin de semana,
    // devuelve el lunes siguiente.
    fun proximoHabil(desde: LocalDate): LocalDate {
        var fecha = desde
        // Avanza día por día mientras sea sábado o domingo
        while (!esHabil(fecha)) {
            fecha = fecha.plusDays(1)
        }
        return fecha
    }

    // Fase 2: devuelve "cantidad" días hábiles seguidos, empezando en proximoHabil(desde).
    fun diasHabiles(desde: LocalDate, cantidad: Int = 5): List<LocalDate> {
        val resultado = mutableListOf<LocalDate>()
        var fecha = proximoHabil(desde)
        while (resultado.size < cantidad) {
            // Solo agrega días hábiles; los sábados y domingos se saltan
            if (esHabil(fecha)) resultado.add(fecha)
            fecha = fecha.plusDays(1)
        }
        return resultado
    }

    // Fase 2: devuelve los 5 días hábiles de la semana indicada.
    // desplazamiento 0 = desde hoy; cada unidad suma 7 días.
    fun semana(hoy: LocalDate, desplazamiento: Int): List<LocalDate> =
        // diasHabiles empieza en proximoHabil, así que la primera fecha siempre es hábil
        diasHabiles(hoy.plusWeeks(desplazamiento.toLong()))

    // Fase 2: devuelve el nombre corto del día, por ejemplo "Mié".
    fun diaCorto(fecha: LocalDate): String = nombreCorto(fecha.dayOfWeek)

    // Fase 3: devuelve el nombre corto de un día de la semana, por ejemplo "Lun".
    fun nombreCorto(dia: DayOfWeek): String =
        // value va de 1 (lunes) a 7 (domingo)
        diasCortos[dia.value - 1]

    // Fase 3: devuelve el día en plural y minúscula para los avisos, por ejemplo "martes".
    fun nombreDiaPlural(dia: DayOfWeek): String {
        val nombre = diasLargos[dia.value - 1].lowercase()
        // lunes a viernes ya terminan en "s"; sábado y domingo llevan "s" al final
        return if (nombre.endsWith("s")) nombre else nombre + "s"
    }

    // Fase 3: devuelve el día y el mes abreviados sin año, por ejemplo "Lun 12 oct".
    fun textoDiaMes(fecha: LocalDate): String =
        "${diaCorto(fecha)} ${fecha.dayOfMonth} ${mesesCortos[fecha.monthValue - 1]}"

    // Fase 2: devuelve el mes con mayúscula inicial y el año, por ejemplo "Octubre 2026".
    fun mesYAnio(fecha: LocalDate): String =
        "${meses[fecha.monthValue - 1].replaceFirstChar { it.uppercase() }} ${fecha.year}"

    // Fase 2: devuelve la fecha completa, por ejemplo "Miércoles 16 de setiembre 2026".
    fun textoLargo(fecha: LocalDate): String =
        "${diasLargos[fecha.dayOfWeek.value - 1]} ${fecha.dayOfMonth} de " +
            "${meses[fecha.monthValue - 1]} ${fecha.year}"

    // Fase 2: devuelve la fecha abreviada, por ejemplo "Mié 16 set 2026".
    fun textoCorto(fecha: LocalDate): String =
        "${diaCorto(fecha)} ${fecha.dayOfMonth} ${mesesCortos[fecha.monthValue - 1]} ${fecha.year}"

    // Fase 2: convierte una fecha ISO ("2026-10-06") a texto largo ("Martes 6 de octubre 2026").
    // Si el texto no es una fecha válida, devuelve el mismo texto sin lanzar excepción.
    fun textoLargoDesdeIso(iso: String): String = desdeIso(iso, ::textoLargo)

    // Fase 2: convierte una fecha ISO ("2026-10-06") a texto corto ("Mar 6 oct 2026").
    // Si el texto no es una fecha válida, devuelve el mismo texto sin lanzar excepción.
    fun textoCortoDesdeIso(iso: String): String = desdeIso(iso, ::textoCorto)

    // Fase 2: duración de cada consulta en minutos (los horarios base van cada 30 minutos)
    private const val MINUTOS_CONSULTA = 30L

    // Fase 2: devuelve el rango de la consulta desde la hora indicada, por ejemplo "09:30" -> "09:30 a 10:00".
    // Si la hora no es válida, devuelve el mismo texto sin lanzar excepción.
    fun rangoHora(hora: String): String =
        try {
            val inicio = LocalTime.parse(hora, formatoHora)
            "${inicio.format(formatoHora)} a ${inicio.plusMinutes(MINUTOS_CONSULTA).format(formatoHora)}"
        } catch (e: DateTimeParseException) {
            hora
        }

    // Fase 2: formato de hora de 24 horas con dos dígitos ("09:30")
    private val formatoHora = DateTimeFormatter.ofPattern("HH:mm")

    // Fase 2: aplica el formato a la fecha ISO; devuelve el texto original si no se puede convertir
    private fun desdeIso(iso: String, formato: (LocalDate) -> String): String =
        try {
            formato(LocalDate.parse(iso))
        } catch (e: DateTimeParseException) {
            iso
        }
}
