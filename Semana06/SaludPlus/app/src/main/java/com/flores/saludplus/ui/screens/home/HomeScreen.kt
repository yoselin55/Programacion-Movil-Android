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
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.flores.saludplus.ui.components.ImagenPorNombre
import com.flores.saludplus.ui.theme.AzulClaro
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flores.saludplus.data.model.Especialidad
import com.flores.saludplus.data.repository.Repositorio
import com.flores.saludplus.navigation.Rutas
import com.flores.saludplus.ui.components.BarraInferior
import com.flores.saludplus.ui.components.BotonMenu
import com.flores.saludplus.ui.components.ImagenEspecialidad
import com.flores.saludplus.ui.theme.AzulOscuro
import com.flores.saludplus.ui.theme.AzulPrimario
import com.flores.saludplus.ui.theme.BordeSuave
import com.flores.saludplus.ui.theme.RojoAviso
import com.flores.saludplus.ui.theme.TextoPrincipal
import com.flores.saludplus.ui.theme.TextoSecundario

// Relaciones:
// - La llama AppNavigation en Rutas.HOME
// - Fase 3: banner con la foto de la clínica en lugar de las tarjetas de acceso
// - Usa BarraInferior y BotonMenu (Componentes.kt, MenuLateral.kt) y Repositorio (usuarioActual, especialidadesDestacadas)
// - Fase 2: usa ImagenEspecialidad (ImagenPorNombre.kt): imagen nombreRecurso(nombre) o círculo pastel
// - Navega a Doctores (especialidad destacada o "Ver todas"), Notificaciones y a Mis citas, Perfil y Resultados
// - Fase 3: el menú lateral ya no está aquí: AppNavigation lo comparte con todas las pantallas y este botón
//   lo abre con onMenu. "Agendar cita" salió del Inicio porque ahora se hace desde Sedes (menú lateral)

// Commit 4: pantalla de Inicio con saludo, tarjetas y especialidades destacadas
// Fase 2: medidas tomadas del diseño de referencia (pantalla de 411 x 913 dp)
@Composable
fun HomeScreen(
    onEspecialidad: (Int) -> Unit,
    onVerEspecialidades: () -> Unit,
    onNotificaciones: () -> Unit,
    onNavegar: (String) -> Unit,
    onMenu: () -> Unit
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
            // Fase 2: fila superior: menú (30dp, zona táctil de 48dp) a la izquierda y campana (32dp) a la derecha
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BotonMenu(onMenu)
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

            // Fase 3: banner con la foto de la clínica (res/drawable/banner_clinica) y el lema, en lugar de las
            // tarjetas Mis citas, Resultados y Mis datos (ya están en la barra inferior)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .clip(RoundedCornerShape(20.dp))
            ) {
                ImagenPorNombre(
                    nombre = "banner_clinica",
                    descripcion = "Clínica SaludPlus",
                    modifier = Modifier.fillMaxSize(),
                    respaldo = { Box(Modifier.fillMaxSize().background(AzulClaro)) }
                )
                // Degradado granate abajo para leer el texto
                Box(
                    modifier = Modifier.fillMaxSize().background(
                        Brush.verticalGradient(listOf(Color.Transparent, Color(0xCC3C0000)))
                    )
                )
                Column(modifier = Modifier.align(Alignment.BottomStart).padding(16.dp)) {
                    Text("Clínica SaludPlus", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text("Tu salud, nuestra prioridad", fontSize = 18.sp, color = Color.White)
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
                    modifier = Modifier.clickable(onClick = onVerEspecialidades).padding(start = 8.dp)
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
            fontSize = 18.sp,
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
