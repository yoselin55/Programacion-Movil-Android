package com.flores.tecsupfit.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// TopAppBar común de la app con el color principal.
// Si se pasa onBackClick se muestra la flecha para volver.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TecsupTopBar(
    title: String,
    subtitle: String? = null,
    onBackClick: (() -> Unit)? = null
) {
    TopAppBar(
        title = {
            Column {
                Text(title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                subtitle?.let {
                    Text(it, fontSize = 13.sp, color = Color.White.copy(alpha = 0.8f))
                }
            }
        },
        navigationIcon = {
            if (onBackClick != null) {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(0xFF0F6A52),
            titleContentColor = Color.White,
            navigationIconContentColor = Color.White
        )
    )
}
