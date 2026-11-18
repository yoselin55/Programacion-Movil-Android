package com.flores.campusgps.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.flores.campusgps.ui.theme.Azul
import com.flores.campusgps.ui.theme.Fondo
import com.flores.campusgps.ui.theme.Tinta

// Mejora 3 (búsqueda): barra flotante estilo Google Maps. A la izquierda abre el menú, en el centro se busca
// por nombre o categoría y a la derecha está tu avatar, que lleva al perfil.
// Al hacer clic en ella, si todavía no se escribió nada, muestra las búsquedas recientes.
// Solo dibuja y avisa los eventos: no conoce el ViewModel.
@Composable
fun BarraBusqueda(
    consulta: String,
    onCambio: (String) -> Unit,
    onBuscar: () -> Unit,
    busquedasRecientes: List<String>,
    onElegirBusqueda: (String) -> Unit,
    inicial: String,
    onMenu: () -> Unit,
    onPerfil: () -> Unit,
    modifier: Modifier = Modifier
) {
    var enfocada by remember { mutableStateOf(false) }
    val foco = LocalFocusManager.current

    Column(modifier = modifier) {
        // La sombra y la forma de píldora las da la Surface; el campo de texto va sin borde.
        Surface(
            shape = RoundedCornerShape(32.dp),
            color = Fondo,
            shadowElevation = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            TextField(
                value = consulta,
                onValueChange = onCambio,
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { enfocada = it.isFocused },
                placeholder = { Text("Buscar lugar o categoría") },
                leadingIcon = {
                    IconButton(onClick = onMenu) {
                        Icon(Icons.Filled.Menu, contentDescription = "Abrir menú", tint = Tinta)
                    }
                },
                trailingIcon = {
                    if (consulta.isNotEmpty()) {
                        IconButton(onClick = { onCambio("") }) {
                            Icon(Icons.Filled.Clear, contentDescription = "Borrar búsqueda")
                        }
                    } else {
                        // Avatar con tu inicial: lleva al perfil.
                        Box(
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Azul)
                                .clickable(onClick = onPerfil),
                            contentAlignment = Alignment.Center
                        ) {
                            if (inicial.isNotBlank()) {
                                Text(inicial, fontWeight = FontWeight.Bold, color = Tinta)
                            } else {
                                Icon(Icons.Filled.Person, contentDescription = "Perfil", tint = Tinta, modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                },
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = Tinta
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = {
                    onBuscar() // guarda la búsqueda en el historial
                    foco.clearFocus()
                })
            )
        }

        // Búsquedas recientes: solo cuando la barra está activa y vacía.
        if (enfocada && consulta.isEmpty() && busquedasRecientes.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Fondo),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Text(
                    "Búsquedas recientes",
                    modifier = Modifier.padding(start = 16.dp, top = 12.dp, end = 16.dp, bottom = 4.dp),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold
                )
                busquedasRecientes.forEach { texto ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onElegirBusqueda(texto)
                                foco.clearFocus()
                            }
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.History, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.width(12.dp))
                        Text(texto)
                    }
                }
            }
        }
    }
}
