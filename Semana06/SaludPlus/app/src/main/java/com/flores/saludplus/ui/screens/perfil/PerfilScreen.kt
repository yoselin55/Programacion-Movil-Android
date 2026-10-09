package com.flores.saludplus.ui.screens.perfil

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flores.saludplus.data.repository.Repositorio
import com.flores.saludplus.navigation.Rutas
import com.flores.saludplus.ui.components.BarraInferior
import com.flores.saludplus.ui.components.BotonContornoRojo
import com.flores.saludplus.ui.components.FilaDetalle
import com.flores.saludplus.ui.theme.AzulClaro
import com.flores.saludplus.ui.theme.AzulOscuro
import com.flores.saludplus.ui.theme.AzulPrimario
import com.flores.saludplus.ui.theme.BordeSuave
import com.flores.saludplus.ui.theme.Divisor
import com.flores.saludplus.ui.theme.TextoSecundario
import com.flores.saludplus.util.iniciales

// Relaciones:
// - Fase 2: usa iniciales (util/Textos.kt) para el avatar
// - La llama AppNavigation en Rutas.PERFIL
// - Usa BarraInferior y FilaDetalle (Componentes.kt); Fase 2: también BotonContornoRojo
// - Llama a Repositorio.usuarioActual, citasDelUsuario y cerrarSesion
// - Al cerrar sesión vuelve al Splash (onCerrarSesion)

// Commit 9: datos de la sesión actual y botón para cerrar sesión
// Fase 2: avatar con iniciales de 120dp, tarjeta de datos con bordes y botón rojo de 64dp
@Composable
fun PerfilScreen(onNavegar: (String) -> Unit, onCerrarSesion: () -> Unit) {
    val usuario = Repositorio.usuarioActual
    val totalCitas = Repositorio.citasDelUsuario().size

    Scaffold(
        containerColor = Color.White,
        bottomBar = { BarraInferior(Rutas.PERFIL, onNavegar) }
    ) { padding ->
        // Fase 2: contenido desplazable con 20dp de margen horizontal
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Fase 2: título 34sp negrita azul marino a la izquierda
            Text(
                "Mis datos",
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                color = AzulOscuro,
                modifier = Modifier.fillMaxWidth().padding(top = 24.dp)
            )
            Spacer(Modifier.height(24.dp))

            // Fase 2: avatar circular de 120dp con las iniciales en 44sp negrita azul
            Box(
                modifier = Modifier.size(120.dp).background(AzulClaro, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(iniciales(usuario?.nombre ?: ""), fontSize = 44.sp, fontWeight = FontWeight.Bold, color = AzulPrimario)
            }
            Spacer(Modifier.height(16.dp))
            // Fase 2: nombre 28sp negrita azul marino y teléfono 20sp gris, centrados
            Text(
                usuario?.nombre ?: "",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = AzulOscuro,
                textAlign = TextAlign.Center
            )
            Text(usuario?.telefono ?: "", fontSize = 20.sp, color = TextoSecundario)
            Spacer(Modifier.height(24.dp))

            // Fase 2: tarjeta blanca de datos (borde 1dp, esquinas 20dp, padding 16dp) con líneas de 1dp
            val datos = listOf(
                Triple(Icons.Filled.Phone, "Teléfono", usuario?.telefono ?: ""),
                Triple(Icons.Filled.Email, "Correo", usuario?.correo?.ifBlank { "No registrado" } ?: "No registrado"),
                Triple(Icons.Filled.Event, "Citas agendadas", totalCitas.toString())
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(20.dp))
                    .border(1.dp, BordeSuave, RoundedCornerShape(20.dp))
                    .padding(16.dp)
            ) {
                datos.forEachIndexed { indice, (icono, titulo, valor) ->
                    // Fase 2: línea divisoria entre filas (no antes de la primera)
                    if (indice > 0) HorizontalDivider(thickness = 1.dp, color = Divisor)
                    Column(modifier = Modifier.padding(vertical = 12.dp)) {
                        FilaDetalle(icono, titulo, valor, tamanoTitulo = 18.sp, tamanoValor = 22.sp)
                    }
                }
            }
            Spacer(Modifier.height(28.dp))

            // Fase 2: cierra la sesión en el Repositorio y vuelve al Splash
            BotonContornoRojo(
                "Cerrar sesión",
                onClick = {
                    Repositorio.cerrarSesion()
                    onCerrarSesion()
                },
                icono = Icons.AutoMirrored.Filled.Logout
            )
            Spacer(Modifier.height(24.dp))
        }
    }
}
