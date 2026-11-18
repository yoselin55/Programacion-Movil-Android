package com.flores.campusgps.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.flores.campusgps.R
import com.flores.campusgps.ui.theme.Azul

// Mejora 3: pantalla de bienvenida con el logo, un saludo y un botón para comenzar.
// nombreGuardado es null mientras se lee el archivo de preferencias.
@Composable
fun PantallaBienvenida(
    nombreGuardado: String?,
    onComenzar: (String) -> Unit
) {
    var texto by rememberSaveable(nombreGuardado) { mutableStateOf(nombreGuardado ?: "") }
    val yaTieneNombre = !nombreGuardado.isNullOrBlank()

    // Animación de entrada del logo: aparece con fundido y crece un poco.
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Decoración: dos círculos suaves del color de la marca, detrás del contenido.
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(Azul.copy(alpha = 0.35f), radius = size.width * 0.55f, center = Offset(size.width, 0f))
            drawCircle(Azul.copy(alpha = 0.25f), radius = size.width * 0.45f, center = Offset(0f, size.height))
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(700)) + scaleIn(tween(700), initialScale = 0.8f)
            ) {
                Image(
                    painter = painterResource(R.drawable.logo_campusgps),
                    contentDescription = "Logo de CampusGPS",
                    modifier = Modifier.size(180.dp)
                )
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text = if (yaTieneNombre) "Hola de nuevo, $nombreGuardado" else "Bienvenido a CampusGPS",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Espero que sea de tu agrado. Estamos aquí para ayudarte a llegar a tu destino dentro del campus.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(24.dp))

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Ventaja(Icons.Filled.MyLocation, "Mira dónde estás en el mapa")
                Ventaja(Icons.Filled.Place, "Encuentra el lugar más cercano")
                Ventaja(Icons.Filled.DirectionsWalk, "Sigue tu camino en vivo")
            }

            Spacer(Modifier.height(28.dp))

            // Solo se pide el nombre la primera vez.
            if (nombreGuardado != null && !yaTieneNombre) {
                OutlinedTextField(
                    value = texto,
                    onValueChange = { texto = it },
                    label = { Text("¿Cómo te llamas? (opcional)") },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(16.dp))
            }

            Button(
                onClick = { onComenzar(texto.trim()) },
                enabled = nombreGuardado != null,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text("Comenzar", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

// Una línea con un ícono dentro de un círculo azul y un texto corto.
@Composable
private fun Ventaja(icono: ImageVector, texto: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Azul),
            contentAlignment = Alignment.Center
        ) {
            Icon(icono, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary)
        }
        Spacer(Modifier.width(12.dp))
        Text(texto, style = MaterialTheme.typography.bodyLarge)
    }
}
