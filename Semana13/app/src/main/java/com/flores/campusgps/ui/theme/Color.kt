package com.flores.campusgps.ui.theme

import androidx.compose.ui.graphics.Color

// Mejora 3: paleta de CampusGPS. Solo dos colores de interfaz (los de la referencia)...
val Fondo = Color(0xFFF5FEFF)   // #F5FEFF: fondo de las pantallas
val Azul = Color(0xFFAAC0E1)    // #AAC0E1: barra superior, botones y tarjetas seleccionadas

// ...y una "tinta" azul marino tomada del logo, solo para texto e íconos (contraste legible).
val Tinta = Color(0xFF1B2E57)
val TintaSuave = Color(0xFF4F5F86) // texto secundario

// Tonos derivados mezclando Azul con Fondo (no son colores nuevos).
val AzulClaro = Color(0xFFC8D9ED)  // 60% de Azul
val AzulSuave = Color(0xFFDBE8F4)  // 35% de Azul: tarjetas y chips
val AzulTenue = Color(0xFFEAF2F9)  // 18% de Azul: paneles
