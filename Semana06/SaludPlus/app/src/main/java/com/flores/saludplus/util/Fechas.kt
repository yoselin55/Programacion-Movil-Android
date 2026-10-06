package com.flores.saludplus.util

import java.time.DayOfWeek
import java.time.LocalDate

// Relaciones:
// - Lo usará FechaHoraScreen (ui/screens/agendamiento) para armar el calendario dinámico
// - Lo prueba FechasTest (app/src/test)
// - Depende solo de java.time.LocalDate (requiere minSdk 26)

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
    fun diaCorto(fecha: LocalDate): String =
        // dayOfWeek.value va de 1 (lunes) a 7 (domingo)
        diasCortos[fecha.dayOfWeek.value - 1]

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
}
