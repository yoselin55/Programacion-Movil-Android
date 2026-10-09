package com.flores.saludplus.data.model

// Relaciones:
// - Lo usa Repositorio (colección sedes)
// - Lo muestran SedesScreen, SedeDetalleScreen y las tarjetas de médico y de confirmación de cita

// Fase 3: sede de la clínica donde atienden los médicos
data class Sede(
    val id: Int,
    val nombre: String,
    val direccion: String,
    val telefono: String
)
