package com.flores.saludplus.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flores.saludplus.data.model.Especialidad
import com.flores.saludplus.data.repository.Repositorio
import com.flores.saludplus.navigation.Rutas
import com.flores.saludplus.ui.components.BarraInferior
import com.flores.saludplus.ui.components.ImagenEspecialidad
import com.flores.saludplus.ui.components.TarjetaAccion
import com.flores.saludplus.ui.theme.AzulClaro
import com.flores.saludplus.ui.theme.AzulOscuro
import com.flores.saludplus.ui.theme.AzulPrimario
import com.flores.saludplus.ui.theme.BordeSuave
import com.flores.saludplus.ui.theme.Morado
import com.flores.saludplus.ui.theme.MoradoClaro
import com.flores.saludplus.ui.theme.Naranja
import com.flores.saludplus.ui.theme.NaranjaClaro
import com.flores.saludplus.ui.theme.RojoAviso
import com.flores.saludplus.ui.theme.TextoPrincipal
import com.flores.saludplus.ui.theme.TextoSecundario
import com.flores.saludplus.ui.theme.VerdeClaro
import com.flores.saludplus.ui.theme.VerdeDisponible

// Relaciones:
// - La llama AppNavigation en Rutas.HOME
// - Usa TarjetaAccion y BarraInferior (Componentes.kt) y Repositorio (usuarioActual, especialidadesDestacadas)
// - Fase 2: usa ImagenEspecialidad (ImagenPorNombre.kt): imagen nombreRecurso(nombre) o círculo pastel
// - Navega a Especialidades, Médicos, Notificaciones y a Mis citas, Perfil y Resultados

// Commit 4: pantalla de Inicio con saludo, tarjetas y especialidades destacadas
// Fase 2: medidas tomadas del diseño de referencia (pantalla de 411 x 913 dp)
@Composable
fun HomeScreen(
    onAgendar: () -> Unit,
    onEspecialidad: (Int) -> Unit,
    onNotificaciones: () -> Unit,
    onNavegar: (String) -> Unit
) {
    // Primer nombre del usuario con sesión iniciada
    val nombre = Repositorio.usuarioActual?.nombre?.substringBefore(" ") ?: ""

    Scaffold(
        // Fase 2: fondo blanco
        containerColor = Color.White,
        // Commit 5: barra inferior de navegación (Fase 2: 90dp de alto)
        bottomBar = { BarraInferior(Rutas.HOME, onNavegar) }
    ) { padding ->
        // Fase 2: margen horizontal de 16dp
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            // Fase 2: fila superior: menú decorativo (30dp) a la izquierda y campana (32dp) a la derecha
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Filled.Menu, contentDescription = null, tint = TextoPrincipal, modifier = Modifier.size(30.dp))
                Spacer(Modifier.weight(1f))
                CampanaConAviso(onClick = onNotificaciones)
            }

            // Fase 2: saludo 40sp negrita azul marino y subtítulo 22sp gris
            // Fase 2: el nombre siempre en una sola línea (Ellipsis si es muy largo)
            Text(
                "¡Hola, $nombre!",
                fontSize = 40.sp,
                lineHeight = 46.sp,
                fontWeight = FontWeight.Bold,
                color = AzulOscuro,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text("¿Qué deseas hacer hoy?", fontSize = 22.sp, color = TextoSecundario)
            Spacer(Modifier.height(20.dp))

            // Fase 2: cuadrícula 2x2 de tarjetas de 155dp con 14dp de separación
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                // Fila 1: Agendar cita y Mis citas
                Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    TarjetaAccion("Agendar cita", Icons.Filled.CalendarMonth, AzulClaro, AzulPrimario, onAgendar, Modifier.weight(1f))
                    TarjetaAccion("Mis citas", Icons.Filled.EventAvailable, VerdeClaro, VerdeDisponible, { onNavegar(Rutas.MIS_CITAS) }, Modifier.weight(1f))
                }
                // Fila 2: Mis datos y Resultados
                Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    TarjetaAccion("Mis datos", Icons.Filled.Person, MoradoClaro, Morado, { onNavegar(Rutas.PERFIL) }, Modifier.weight(1f))
                    TarjetaAccion("Resultados", Icons.Filled.Description, NaranjaClaro, Naranja, { onNavegar(Rutas.RESULTADOS) }, Modifier.weight(1f))
                }
            }
            Spacer(Modifier.height(20.dp))

            // Fase 2: fila "Especialidades destacadas" (22sp negrita) y "Ver todas" (22sp SemiBold azul)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "Especialidades destacadas",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextoPrincipal,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    "Ver todas",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AzulPrimario,
                    modifier = Modifier.clickable(onClick = onAgendar).padding(start = 8.dp)
                )
            }
            Spacer(Modifier.height(12.dp))

            // Fase 2: especialidades destacadas con 12dp de separación
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(Repositorio.especialidadesDestacadas()) { especialidad ->
                    EspecialidadDestacada(especialidad, onClick = { onEspecialidad(especialidad.id) })
                }
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

// Fase 2: dibuja la campana de notificaciones (32dp) con un punto rojo de 10dp arriba a la derecha
@Composable
private fun CampanaConAviso(onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Box {
            Icon(
                Icons.Outlined.Notifications,
                contentDescription = "Notificaciones",
                tint = TextoPrincipal,
                modifier = Modifier.size(32.dp)
            )
            // Fase 2: punto rojo (badge) con borde blanco
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 2.dp, end = 3.dp)
                    .size(10.dp)
                    .border(1.5.dp, Color.White, CircleShape)
                    .background(RojoAviso, CircleShape)
            )
        }
    }
}

// Tarjeta pequeña de una especialidad para el LazyRow
// Fase 2: tarjeta blanca de 120x160dp, esquinas 18dp, borde 1dp (#E3E8F0), sin sombra,
// con imagen circular de 72dp y el nombre en 19sp negrita (hasta 2 líneas)
@Composable
private fun EspecialidadDestacada(especialidad: Especialidad, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .size(width = 120.dp, height = 160.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White)
            .border(1.dp, BordeSuave, RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 6.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically)
    ) {
        // Fase 2: imagen circular de 72dp (o círculo pastel con el ícono si no hay imagen)
        ImagenEspecialidad(especialidad, 72.dp)
        Text(
            especialidad.nombre,
            fontSize = 19.sp,
            lineHeight = 22.sp,
            fontWeight = FontWeight.Bold,
            color = TextoPrincipal,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
