package com.flores.lavacampus.model

import androidx.compose.ui.graphics.Color

// Formato que reciben GraficoDona y GraficoBarras
data class DatoGrafico(
    val etiqueta: String,
    val valor: Float,
    val color: Color
)
