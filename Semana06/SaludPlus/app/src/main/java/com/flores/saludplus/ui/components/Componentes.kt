package com.flores.saludplus.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flores.saludplus.data.model.Cita
import com.flores.saludplus.data.model.Especialidad
import com.flores.saludplus.data.model.Medico
import com.flores.saludplus.navigation.Rutas
import com.flores.saludplus.ui.theme.AzulClaro
import com.flores.saludplus.ui.theme.AzulOscuro
import com.flores.saludplus.ui.theme.BordeCampo
import com.flores.saludplus.ui.theme.FondoBusqueda
import com.flores.saludplus.ui.theme.AzulPrimario
import com.flores.saludplus.ui.theme.BordeSuave
import com.flores.saludplus.ui.theme.Estrella
import com.flores.saludplus.ui.theme.TextoPrincipal
import com.flores.saludplus.ui.theme.TextoSecundario
import com.flores.saludplus.ui.theme.VerdeClaro
import com.flores.saludplus.ui.theme.VerdeDisponible
import com.flores.saludplus.util.Fechas

// Relaciones:
// - Los usan las pantallas de ui/screens (Splash, Registro, Login, Home y las 3 de la barra inferior)
// - TarjetaCita usa util/Fechas.kt (Fase 2) para mostrar la fecha en español
// - Fase 2: usan los colores BordeSuave, BordeCampo, FondoBusqueda y TextoSecundario de ui/theme/Color.kt
// - Fase 2: EspecialidadesScreen usa CampoBusqueda e ItemEspecialidad como lista plana
// - Fase 2: ItemEspecialidad y TarjetaMedico usan ImagenEspecialidad y FotoMedico (ImagenPorNombre.kt)

// Botón azul de ancho completo (Comenzar, Registrarme, Ingresar...)
// Fase 2: alto, radio y tamaño de texto configurables (por defecto 56dp, 14dp y 16sp)
@Composable
fun BotonPrincipal(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    alto: Dp = 56.dp,
    radio: Dp = 14.dp,
    tamanoTexto: TextUnit = 16.sp
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(radio),
        colors = ButtonDefaults.buttonColors(containerColor = AzulPrimario, contentColor = Color.White),
        modifier = modifier.fillMaxWidth().height(alto)
    ) {
        Text(texto, fontWeight = FontWeight.SemiBold, fontSize = tamanoTexto)
    }
}

// Campo de texto con ícono, etiqueta y mensaje de error (nombre, teléfono, correo, contraseña)
// Fase 2: delega en CampoFila (fila de 80dp con recuadro de ícono pegado al campo)
@Composable
fun CampoTexto(
    valor: String,
    onCambio: (String) -> Unit,
    etiqueta: String,
    icono: ImageVector,
    modifier: Modifier = Modifier,
    error: String? = null,
    teclado: KeyboardType = KeyboardType.Text,
    esContrasena: Boolean = false
) {
    CampoFila(valor, onCambio, etiqueta, icono, modifier, error, teclado, esContrasena)
}

// Fase 2: dibuja una fila de 80dp: a la izquierda el recuadro del ícono (78dp) y, pegada a su
// derecha sin espacio, una columna con la etiqueta gris arriba y la caja de escritura (54dp) abajo
@Composable
fun CampoFila(
    valor: String,
    onCambio: (String) -> Unit,
    etiqueta: String,
    icono: ImageVector,
    modifier: Modifier = Modifier,
    error: String? = null,
    teclado: KeyboardType = KeyboardType.Text,
    esContrasena: Boolean = false
) {
    // Fase 2: la caja de escritura se pinta con borde rojo cuando hay error
    val colorBorde = if (error != null) MaterialTheme.colorScheme.error else BordeCampo
    Column(modifier = modifier.fillMaxWidth()) {
        // Fase 2: fila de 80dp; todo alineado abajo para que la caja coincida con el borde inferior del recuadro
        Row(
            modifier = Modifier.fillMaxWidth().height(80.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            // Fase 2: recuadro de 78dp, esquinas 16dp, fondo blanco, borde 1dp #D8DEE9 e ícono azul de 34dp
            Box(
                modifier = Modifier
                    .size(78.dp)
                    .background(Color.White, RoundedCornerShape(16.dp))
                    .border(1.dp, BordeCampo, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icono, contentDescription = null, tint = AzulPrimario, modifier = Modifier.size(34.dp))
            }
            // Fase 2: columna pegada al recuadro: etiqueta 19sp arriba y caja de 54dp abajo
            Column(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    etiqueta,
                    fontSize = 19.sp,
                    lineHeight = 22.sp,
                    color = TextoSecundario,
                    modifier = Modifier.padding(start = 14.dp)
                )
                BasicTextField(
                    value = valor,
                    onValueChange = onCambio,
                    singleLine = true,
                    textStyle = TextStyle(fontSize = 22.sp, color = TextoPrincipal),
                    cursorBrush = SolidColor(AzulPrimario),
                    keyboardOptions = KeyboardOptions(keyboardType = teclado),
                    // La contraseña se muestra con puntos
                    visualTransformation = if (esContrasena) PasswordVisualTransformation() else VisualTransformation.None,
                    modifier = Modifier.fillMaxWidth(),
                    // Fase 2: caja blanca de 54dp, borde 1dp, esquinas 14dp y 14dp de padding horizontal
                    decorationBox = { campo ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp)
                                .background(Color.White, RoundedCornerShape(14.dp))
                                .border(1.dp, colorBorde, RoundedCornerShape(14.dp))
                                .padding(horizontal = 14.dp),
                            contentAlignment = Alignment.CenterStart
                        ) { campo() }
                    }
                )
            }
        }
        // Fase 2: mensaje de error bajo la fila, en rojo de 15sp
        if (error != null) {
            Text(
                error,
                color = MaterialTheme.colorScheme.error,
                fontSize = 15.sp,
                modifier = Modifier.padding(start = 8.dp, top = 6.dp)
            )
        }
    }
}

// Barra superior con título centrado y flecha para volver (pantallas internas)
// Fase 2: por defecto fondo blanco, flecha de 28dp y título de 26sp negrita azul marino;
// colorFondo y tamanoTitulo (con valor por defecto) permiten adaptarla si hace falta
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarraSuperior(
    titulo: String,
    onBack: () -> Unit,
    acciones: @Composable RowScope.() -> Unit = {},
    colorFondo: Color = Color.White,
    tamanoTitulo: TextUnit = 26.sp
) {
    CenterAlignedTopAppBar(
        // Fase 2: una sola línea para que los títulos largos ("Médicos de ...") no rompan la barra
        title = {
            Text(
                titulo,
                fontWeight = FontWeight.Bold,
                fontSize = tamanoTitulo,
                color = AzulOscuro,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", modifier = Modifier.size(28.dp))
            }
        },
        actions = acciones,
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = colorFondo)
    )
}

// Logo de la clínica: ícono médico sobre un cuadro azul claro
@Composable
fun LogoClinica(tamano: Int = 96) {
    Box(
        modifier = Modifier.size(tamano.dp).background(AzulClaro, RoundedCornerShape(24.dp)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            Icons.Filled.MedicalServices,
            contentDescription = "Logo SaludPlus",
            tint = AzulPrimario,
            modifier = Modifier.size((tamano * 0.6).dp)
        )
    }
}

// Commit 9: tarjeta de una cita (médico, especialidad, fecha y hora)
@Composable
fun TarjetaCita(cita: Cita, medico: String, especialidad: String, onClick: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(medico, fontWeight = FontWeight.Bold)
            Text(especialidad, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Event, contentDescription = null, tint = AzulPrimario, modifier = Modifier.size(18.dp))
                // Fase 2: fecha corta en español, por ejemplo "Mar 6 oct 2026"
                Text(Fechas.textoCortoDesdeIso(cita.fecha), style = MaterialTheme.typography.bodyMedium)
                Icon(Icons.Filled.Schedule, contentDescription = null, tint = AzulPrimario, modifier = Modifier.size(18.dp))
                Text(cita.hora, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

// Commit 8: fila con ícono, título y valor (datos de la cita)
// Fase 2: tamaños de título y valor opcionales (por defecto los del tema)
@Composable
fun FilaDetalle(
    icono: ImageVector,
    titulo: String,
    valor: String,
    tamanoTitulo: TextUnit = TextUnit.Unspecified,
    tamanoValor: TextUnit = TextUnit.Unspecified
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier.size(40.dp).background(AzulClaro, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icono, contentDescription = null, tint = AzulPrimario)
        }
        Column {
            Text(titulo, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = tamanoTitulo)
            Text(valor, fontWeight = FontWeight.SemiBold, fontSize = tamanoValor)
        }
    }
}

// Commit 7: botón seleccionable para días y horas; se pinta de azul al elegirlo
// Fase 2: alto y tamaño de texto configurables (por defecto 56dp y el estilo labelMedium)
@Composable
fun ChipSeleccion(
    texto: String,
    seleccionado: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    subtitulo: String? = null,
    alto: Dp = 56.dp,
    tamanoTexto: TextUnit = TextUnit.Unspecified
) {
    val colorTexto = if (seleccionado) Color.White else MaterialTheme.colorScheme.onSurface
    Column(
        modifier = modifier
            .height(alto)
            .background(if (seleccionado) AzulPrimario else AzulClaro, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(texto, color = colorTexto, style = MaterialTheme.typography.labelMedium, fontSize = tamanoTexto)
        if (subtitulo != null) {
            Text(subtitulo, color = colorTexto, fontWeight = FontWeight.Bold)
        }
    }
}

// Commit 6: campo de búsqueda en tiempo real (Especialidades y Médicos)
// Fase 2: caja de 52dp, esquinas 14dp, fondo celeste muy claro (#EEF3FA), sin borde,
// lupa gris a la izquierda y placeholder de 18sp gris
@Composable
fun CampoBusqueda(valor: String, onCambio: (String) -> Unit, placeholder: String, modifier: Modifier = Modifier) {
    BasicTextField(
        value = valor,
        onValueChange = onCambio,
        singleLine = true,
        textStyle = TextStyle(fontSize = 18.sp, color = TextoPrincipal),
        cursorBrush = SolidColor(AzulPrimario),
        modifier = modifier.fillMaxWidth(),
        decorationBox = { campo ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .background(FondoBusqueda, RoundedCornerShape(14.dp))
                    .padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(Icons.Filled.Search, contentDescription = null, tint = TextoSecundario)
                Box(modifier = Modifier.weight(1f)) {
                    // Placeholder visible solo mientras no hay texto
                    if (valor.isEmpty()) {
                        Text(placeholder, fontSize = 18.sp, color = TextoSecundario)
                    }
                    campo()
                }
            }
        }
    )
}

// Commit 6: fila de una especialidad (ícono, nombre, descripción y flecha)
// Fase 2: fila plana de 96dp (sin tarjeta ni sombra) con margen horizontal de 16dp:
// imagen circular de 88dp, 14dp de espacio, nombre y descripción, y chevron de 28dp
@Composable
fun ItemEspecialidad(especialidad: Especialidad, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(96.dp)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Fase 2: imagen por nombre (nombreRecurso) en círculo de 88dp, o círculo pastel con el ícono
        ImagenEspecialidad(especialidad, 88.dp)
        Spacer(Modifier.width(14.dp))
        // Fase 2: nombre 22sp negrita azul marino y descripción 18sp gris
        Column(modifier = Modifier.weight(1f)) {
            Text(especialidad.nombre, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = AzulOscuro)
            Text(especialidad.descripcion, fontSize = 18.sp, color = TextoSecundario)
        }
        // Fase 2: chevron oscuro de 28dp
        Icon(
            Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = TextoPrincipal,
            modifier = Modifier.size(28.dp)
        )
    }
}

// Commit 6: tarjeta de un médico (nombre, especialidad, calificación y disponibilidad)
@Composable
// Fase 2: modifier opcional (con valor por defecto) para que la lista le asigne el alto
fun TarjetaMedico(medico: Medico, especialidad: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier.fillMaxWidth().clickable(onClick = onClick)
    ) {
        // Fase 2: la fila llena todo el alto de la tarjeta
        Row(
            modifier = Modifier.fillMaxSize().padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Fase 2: foto por nombre (dra_ana_torres...) o silueta de respaldo, de 88dp
            FotoMedico(medico.nombre, 88.dp)
            // Fase 2: nombre 20sp, especialidad 16sp y calificación 16sp
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(medico.nombre, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Text(especialidad, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Star, contentDescription = null, tint = Estrella, modifier = Modifier.size(20.dp))
                    Text(" ${medico.calificacion} (${medico.resenas})", fontSize = 16.sp)
                }
            }
            // Fase 2: etiqueta de disponibilidad ("Disponible hoy") a la derecha, abajo de la tarjeta
            Box(
                modifier = Modifier
                    .align(Alignment.Bottom)
                    .background(VerdeClaro, RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(medico.disponibilidad, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = VerdeDisponible)
            }
        }
    }
}

// Commit 5: barra inferior con los 4 destinos principales; resalta la ruta actual
@Composable
fun BarraInferior(rutaActual: String, onNavegar: (String) -> Unit) {
    val destinos = listOf(
        Triple("Inicio", Icons.Filled.Home, Rutas.HOME),
        Triple("Citas", Icons.Filled.Event, Rutas.MIS_CITAS),
        Triple("Resultados", Icons.Filled.Description, Rutas.RESULTADOS),
        Triple("Perfil", Icons.Filled.Person, Rutas.PERFIL)
    )
    // Fase 2: fondo blanco con línea superior de 1dp; el padding de sistema va fuera de los 90dp
    Column(modifier = Modifier.background(Color.White).navigationBarsPadding()) {
        HorizontalDivider(thickness = 1.dp, color = BordeSuave)
        // Fase 2: barra de 90dp de alto, sin sombra
        NavigationBar(
            containerColor = Color.White,
            tonalElevation = 0.dp,
            windowInsets = WindowInsets(0),
            modifier = Modifier.height(90.dp)
        ) {
            destinos.forEach { (etiqueta, icono, ruta) ->
                val activo = ruta == rutaActual
                NavigationBarItem(
                    selected = activo,
                    onClick = { if (!activo) onNavegar(ruta) },
                    // Fase 2: íconos de 34dp
                    icon = { Icon(icono, contentDescription = etiqueta, modifier = Modifier.size(34.dp)) },
                    // Fase 2: etiquetas de 18sp; la activa en negrita
                    label = {
                        Text(
                            etiqueta,
                            fontSize = 18.sp,
                            maxLines = 1,
                            fontWeight = if (activo) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    // Fase 2: activo en azul sin píldora de fondo; los demás en gris
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = AzulPrimario,
                        selectedTextColor = AzulPrimario,
                        indicatorColor = Color.Transparent,
                        unselectedIconColor = TextoSecundario,
                        unselectedTextColor = TextoSecundario
                    )
                )
            }
        }
    }
}

// Commit 4: tarjeta cuadrada de color con ícono y texto (accesos de Inicio)
// Fase 2: 155dp de alto, esquinas 20dp, sin sombra, ícono de 60dp y texto de 22sp SemiBold
@Composable
fun TarjetaAccion(
    texto: String,
    icono: ImageVector,
    colorFondo: Color,
    colorIcono: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = colorFondo),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = modifier.height(155.dp).clickable(onClick = onClick)
    ) {
        // Fase 2: contenido centrado: ícono arriba y texto debajo, ambos del color fuerte
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icono, contentDescription = null, tint = colorIcono, modifier = Modifier.size(60.dp))
            Text(texto, color = colorIcono, fontSize = 22.sp, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center)
        }
    }
}

// Contenido temporal de cada pantalla pendiente, con botones para seguir el flujo.
// TODO: quitar su uso en cada pantalla al terminarla
@Composable
fun PantallaEnConstruccion(
    titulo: String,
    acciones: List<Pair<String, () -> Unit>> = emptyList()
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = titulo, style = MaterialTheme.typography.headlineSmall)
        Text(text = "Pantalla en construcción", style = MaterialTheme.typography.bodyMedium)
        acciones.forEach { (texto, accion) ->
            Button(onClick = accion) { Text(texto) }
        }
    }
}
