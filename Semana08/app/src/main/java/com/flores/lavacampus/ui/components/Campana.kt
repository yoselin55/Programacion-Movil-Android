package com.flores.lavacampus.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Barra de arriba del Panel: el boton del menu lateral, y el titulo con una frase corta
@Composable
fun BarraSuperior(
    frase: String,
    onMenu: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        BotonMenu(onMenu)
        Column(Modifier.weight(1f).padding(start = 12.dp)) {
            Text("LavaCampus", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text(frase, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f))
        }
    }
}
