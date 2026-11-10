package com.flores.mycar.ui.components

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/** Horas en las que se atiende (cada hora, de 9:00 a 17:00). */
val HORAS_CITA = (9..17).map { "%02d:00".format(it) }

/** Los proximos dias de atencion (de lunes a sabado): fecha "dd/MM/yyyy" y su etiqueta, ej. "Lun 10 Nov". */
fun proximosDias(cantidad: Int = 12): List<Pair<String, String>> {
    val formatoFecha = SimpleDateFormat("dd/MM/yyyy", Locale.US)
    val formatoEtiqueta = SimpleDateFormat("EEE d MMM", Locale.forLanguageTag("es-PE"))
    val dia = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 1) }
    val lista = mutableListOf<Pair<String, String>>()
    while (lista.size < cantidad) {
        if (dia.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
            lista += formatoFecha.format(dia.time) to formatoEtiqueta.format(dia.time).replaceFirstChar { it.uppercase() }
        }
        dia.add(Calendar.DAY_OF_YEAR, 1)
    }
    return lista
}
