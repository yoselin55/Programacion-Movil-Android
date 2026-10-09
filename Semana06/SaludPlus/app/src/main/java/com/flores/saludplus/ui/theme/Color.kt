package com.flores.saludplus.ui.theme

import androidx.compose.ui.graphics.Color

// Relaciones:
// - Lo usa Theme.kt para armar el esquema de colores
// - Lo usan Componentes.kt y las pantallas de ui/screens (Fase 2: colores del diseño de referencia)

// Paleta de SaludPlus (Fase 3): #3C0000 y #670010 (granates oscuros), #960018 (rojo principal),
// #E8C39E (arena) y #F5E1CE (crema). Los nombres "Azul*" se conservan para no tocar todas las pantallas:
// AzulPrimario es ahora el rojo #960018, AzulClaro la crema de fondos y AzulOscuro el granate #670010 de títulos;
// TextoPrincipal usa #3C0000 y los bordes la arena #E8C39E.
// RojoAviso pasó a naranja para que los errores se distingan del rojo principal.
val AzulPrimario = Color(0xFF960018)
val AzulClaro = Color(0xFFF5E1CE)
val AzulOscuro = Color(0xFF670010)
val FondoApp = Color(0xFFFFFAF5)
val TextoPrincipal = Color(0xFF3C0000)
val TextoSecundario = Color(0xFF7A6560)
val VerdeDisponible = Color(0xFF2E9E5B)
val VerdeClaro = Color(0xFFDDF5E7)
val Estrella = Color(0xFFF5A623)

// Commit 4: colores de las tarjetas de acción de Inicio
val MoradoClaro = Color(0xFFEDE4FB)
val Morado = Color(0xFF7B4FD6)
val NaranjaClaro = Color(0xFFFDEBD3)
val Naranja = Color(0xFFF28C1E)

// Fase 2: colores del diseño de referencia (bordes finos, fondo celeste del Splash y aviso rojo)
val BordeSuave = Color(0xFFE8C39E)
val CelesteSplash = Color(0xFFF5E1CE)
val RojoAviso = Color(0xFFF57C00)

// Fase 2: fondo gris muy claro del área de escritura y gris marcado del aviso de términos
val FondoCampo = Color(0xFFFBF3EA)
val GrisMarcado = Color(0xFF5A3E3E)

// Fase 2: pares pastel/fuerte extra para los respaldos de las especialidades
val RosaClaro = Color(0xFFFCE4EC)
val Rosa = Color(0xFFD81B60)
val RojoClaro = Color(0xFFFDE0E0)
val Rojo = Color(0xFFE53935)
val CelesteClaro = Color(0xFFE0F2FE)
val Celeste = Color(0xFF0288D1)

// Fase 2: borde de los campos del Registro/Login, fondo del buscador y divisor de la lista
val BordeCampo = Color(0xFFE8C39E)
val FondoBusqueda = Color(0xFFF8EBDD)
val Divisor = Color(0xFFEBD5C1)

// Fase 2: línea separadora más marcada para los datos de Confirmar cita
val LineaSeparadora = Color(0xFFD9B99A)
