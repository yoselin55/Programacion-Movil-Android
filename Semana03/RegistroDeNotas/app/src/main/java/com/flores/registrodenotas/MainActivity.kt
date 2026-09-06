package com.flores.registrodenotas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                RegistroNotasScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroNotasScreen() {
    var notaFundamentos by remember { mutableFloatStateOf(0f) }
    var notaPoo by remember { mutableFloatStateOf(0f) }
    var notaMoviles by remember { mutableFloatStateOf(0f) }
    var notaBd by remember { mutableFloatStateOf(0f) }

    var redondear by remember { mutableStateOf(false) }
    var confirmado by remember { mutableStateOf(false) }
    var calculado by remember { mutableStateOf(false) }

    var promedioPonderado by remember { mutableDoubleStateOf(0.0) }
    var promedioFinalTexto by remember { mutableStateOf("") }
    var observacion by remember { mutableStateOf("") }
    var colorChipFondo by remember { mutableStateOf(Color.Unspecified) }
    var colorChipTexto by remember { mutableStateOf(Color.Unspecified) }

    val fondoDegradado = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFEADDFF),
            Color(0xFFF6F2FA)
        )
    )

    val colorMoradoPrincipal = Color(0xFF5B419D)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(fondoDegradado)
            .verticalScroll(rememberScrollState())
    ) {
        TopAppBar(
            title = {
                Text(
                    text = "Registro de Notas",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = colorMoradoPrincipal
            )
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Notas del ciclo",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                text = "Desliza para asignar cada nota (0 a 20)",
                fontSize = 12.sp,
                color = Color.Gray
            )

            CursoSliderItem("Fundamentos de Programación", "20%", notaFundamentos) {
                notaFundamentos = it
                calculado = false
            }
            CursoSliderItem("Programación Orientada a Objetos", "25%", notaPoo) {
                notaPoo = it
                calculado = false
            }
            CursoSliderItem("Programación en Móviles", "30%", notaMoviles) {
                notaMoviles = it
                calculado = false
            }
            CursoSliderItem("Base de Datos", "25%", notaBd) {
                notaBd = it
                calculado = false
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Desarrollado por: Yoselin Fabiola Flores",
                fontSize = 12.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 16.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CursoSliderItem(
    nombre: String,
    peso: String,
    nota: Float,
    onNotaChange: (Float) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row {
                Text(text = nombre, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.Black)
                Text(text = " ($peso)", color = Color(0xFF6750A4), fontSize = 12.sp)
            }

            // Semáforo: fondo verde si es >= 13, rojo si es menor
            val colorFondoBadge = if (nota >= 13f) Color(0xFFD7E8DE) else Color(0xFFFFEBEE)
            val colorTextoBadge = if (nota >= 13f) Color(0xFF2E7D32) else Color(0xFFC62828)

            Surface(
                color = colorFondoBadge,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "${nota.toInt()}",
                    fontWeight = FontWeight.Bold,
                    color = colorTextoBadge,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }

        Slider(
            value = nota,
            onValueChange = { onNotaChange(it.roundToInt().toFloat()) },
            valueRange = 0f..20f,
            thumb = {
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .background(Color(0xFF5B419D), CircleShape)
                )
            },
            colors = SliderDefaults.colors(
                activeTrackColor = Color(0xFF5B419D),
                inactiveTrackColor = Color(0xFFE8E0F0)
            )
        )
    }
}