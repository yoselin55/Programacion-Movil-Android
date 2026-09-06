package com.flores.registrodenotas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
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
import java.util.Locale
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

    Scaffold(
        topBar = {
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
        }
    ) { paddingValores ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValores)
                .background(fondoDegradado)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "Notas del ciclo",
                fontSize = 17.sp,
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

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Redondear promedio final", fontSize = 13.sp, color = Color.Black)
                Switch(
                    checked = redondear,
                    onCheckedChange = {
                        redondear = it
                        if (calculado) {
                            ejecutarCalculo(
                                notaFundamentos, notaPoo, notaMoviles, notaBd, redondear,
                                onResultado = { promPond, promFinTxt, obs, bgCol, txtCol ->
                                    promedioPonderado = promPond
                                    promedioFinalTexto = promFinTxt
                                    observacion = obs
                                    colorChipFondo = bgCol
                                    colorChipTexto = txtCol
                                }
                            )
                        }
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = colorMoradoPrincipal
                    )
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(
                    checked = confirmado,
                    onCheckedChange = {
                        confirmado = it
                        if (!it) calculado = false
                    },
                    colors = CheckboxDefaults.colors(
                        checkedColor = colorMoradoPrincipal
                    )
                )
                Text("Confirmo que las notas son correctas", fontSize = 13.sp, color = Color.Black)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        ejecutarCalculo(
                            notaFundamentos, notaPoo, notaMoviles, notaBd, redondear,
                            onResultado = { promPond, promFinTxt, obs, bgCol, txtCol ->
                                promedioPonderado = promPond
                                promedioFinalTexto = promFinTxt
                                observacion = obs
                                colorChipFondo = bgCol
                                colorChipTexto = txtCol
                                calculado = true
                            }
                        )
                    },
                    enabled = confirmado,
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp),
                    shape = RoundedCornerShape(22.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorMoradoPrincipal,
                        disabledContainerColor = Color(0xFFCCC2DC)
                    )
                ) {
                    Text("CALCULAR", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }

                OutlinedButton(
                    onClick = {
                        notaFundamentos = 0f
                        notaPoo = 0f
                        notaMoviles = 0f
                        notaBd = 0f
                        redondear = false
                        confirmado = false
                        calculado = false
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp),
                    shape = RoundedCornerShape(22.dp),
                    border = BorderStroke(1.dp, colorMoradoPrincipal),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = colorMoradoPrincipal
                    )
                ) {
                    Text("LIMPIAR", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }

            if (!calculado) {
                Text(
                    text = "Asigna las notas y confirma para calcular",
                    color = Color.Gray,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 2.dp)
                )
            } else {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE0D0F0)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Aporte por curso:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Color.Black
                        )
                        Text(
                            text = "• Fundamentos: ${notaFundamentos.toInt()} × 20% = ${String.format(Locale.US, "%.2f", notaFundamentos * 0.20f)}",
                            fontSize = 11.sp,
                            color = Color(0xFF49454F)
                        )
                        Text(
                            text = "• POO: ${notaPoo.toInt()} × 25% = ${String.format(Locale.US, "%.2f", notaPoo * 0.25f)}",
                            fontSize = 11.sp,
                            color = Color(0xFF49454F)
                        )
                        Text(
                            text = "• Móviles: ${notaMoviles.toInt()} × 30% = ${String.format(Locale.US, "%.2f", notaMoviles * 0.30f)}",
                            fontSize = 11.sp,
                            color = Color(0xFF49454F)
                        )
                        Text(
                            text = "• Base de Datos: ${notaBd.toInt()} × 25% = ${String.format(Locale.US, "%.2f", notaBd * 0.25f)}",
                            fontSize = 11.sp,
                            color = Color(0xFF49454F)
                        )

                        HorizontalDivider(modifier = Modifier.padding(vertical = 2.dp), color = Color(0xFFE0D0F0))

                        Row {
                            Text("Promedio ponderado:  ", color = Color(0xFF49454F), fontSize = 13.sp)
                            Text(
                                text = String.format(Locale.US, "%.2f", promedioPonderado),
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }

                        Column {
                            Row {
                                Text(
                                    text = "Promedio final:  ",
                                    fontWeight = FontWeight.Bold,
                                    color = colorMoradoPrincipal,
                                    fontSize = 16.sp
                                )
                                Text(
                                    text = promedioFinalTexto,
                                    fontWeight = FontWeight.Bold,
                                    color = colorMoradoPrincipal,
                                    fontSize = 16.sp
                                )
                            }
                            if (redondear) {
                                Text(
                                    text = "(redondeado)",
                                    fontSize = 10.sp,
                                    color = Color.Gray
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        Surface(
                            color = colorChipFondo,
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(
                                text = observacion,
                                color = colorChipTexto,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Text(
                    text = "✓ Promedio calculado correctamente",
                    color = Color(0xFF2E7D32),
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                )
            }

            Text(
                text = "Desarrollado por: Yoselin Fabiola Flores",
                fontSize = 11.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
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

private fun ejecutarCalculo(
    n1: Float, n2: Float, n3: Float, n4: Float,
    redondear: Boolean,
    onResultado: (Double, String, String, Color, Color) -> Unit
) {
    val ponderado = (n1 * 0.20) + (n2 * 0.25) + (n3 * 0.30) + (n4 * 0.25)
    val promFinalNum: Double
    val promFinalTxt: String

    if (redondear) {
        val redondeadoInt = ponderado.roundToInt()
        promFinalNum = redondeadoInt.toDouble()
        promFinalTxt = "$redondeadoInt"
    } else {
        promFinalNum = ponderado
        promFinalTxt = String.format(Locale.US, "%.2f", ponderado)
    }

    val (obs, bgCol, txtCol) = when {
        promFinalNum >= 17.0 -> Triple("EXCELENTE", Color(0xFF1B5E20), Color.White)
        promFinalNum >= 13.0 -> Triple("APROBADO", Color(0xFFD7E8DE), Color(0xFF2E7D32))
        promFinalNum >= 10.0 -> Triple("EN RECUPERACIÓN", Color(0xFFFFF3C4), Color(0xFFB78103))
        else -> Triple("DESAPROBADO", Color(0xFFFFEBEE), Color(0xFFC62828))
    }

    onResultado(ponderado, promFinalTxt, obs, bgCol, txtCol)
}