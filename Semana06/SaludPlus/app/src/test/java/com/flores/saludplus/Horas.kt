package com.flores.saludplus

import java.time.LocalDateTime

// Relaciones:
// - La usan las pruebas que agendan citas (HorariosReservadosTest, CancelarCitaTest, CrucesDeCitasTest, HorariosDeMedicoTest)

// Fase 3: "ahora" fijo (jueves 1 oct 2026, 08:00) para que las fechas de prueba (5 al 9 de octubre) siempre
// sean futuras y las pruebas no dependan del día en que se ejecuten
val AHORA_PRUEBA: LocalDateTime = LocalDateTime.of(2026, 10, 1, 8, 0)
