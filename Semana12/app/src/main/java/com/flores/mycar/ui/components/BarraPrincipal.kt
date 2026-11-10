package com.flores.mycar.ui.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf

/** Lo que muestra la barra superior a la derecha: la campana con los avisos de pedidos. */
data class AccionesBarra(
    val avisos: Int,
    val onAvisos: () -> Unit
)

val LocalAccionesBarra = staticCompositionLocalOf<AccionesBarra?> { null }

/**
 * Barra superior de las pantallas principales: boton del menu lateral, titulo y, a la derecha,
 * la campana (cambios en tus pedidos, en tiempo real).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarraPrincipal(
    titulo: String,
    onMenu: () -> Unit,
    acciones: @Composable RowScope.() -> Unit = {}
) {
    val barra = LocalAccionesBarra.current
    TopAppBar(
        title = { Text(titulo) },
        navigationIcon = {
            IconButton(onClick = onMenu) {
                Icon(Icons.Filled.Menu, contentDescription = "Abrir menú")
            }
        },
        actions = {
            acciones()
            if (barra != null) {
                IconButton(onClick = barra.onAvisos) {
                    BadgedBox(badge = { if (barra.avisos > 0) Badge { Text(barra.avisos.toString()) } }) {
                        Icon(
                            if (barra.avisos > 0) Icons.Filled.NotificationsActive else Icons.Filled.Notifications,
                            contentDescription = "Avisos de pedidos"
                        )
                    }
                }
            }
        }
    )
}
