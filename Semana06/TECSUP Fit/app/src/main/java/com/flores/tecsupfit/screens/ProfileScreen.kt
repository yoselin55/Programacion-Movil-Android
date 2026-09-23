package com.flores.tecsupfit.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flores.tecsupfit.model.User

@Composable
fun ProfileScreen(
    user: User,
    reservedCount: Int,   // reservas "Confirmada"
    completedCount: Int,  // reservas "Completada"
    routinesCompletedCount: Int,
    onSaveName: (String) -> Unit,
    onLogout: () -> Unit
) {
    val primary = Color(0xFF0F6A52)
    var showEditDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TecsupTopBar(title = "Mi perfil") }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(14.dp))

            Box(
                modifier = Modifier
                    .size(90.dp)
                    .background(Color(0xFFD4F3E6), shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                // Iniciales del usuario logueado
                Text(user.initials, fontSize = 26.sp, fontWeight = FontWeight.Bold, color = primary)
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(user.fullName, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text(user.email, color = Color.Gray, fontSize = 13.sp)
            Text("Plan Premium", color = Color.Gray, fontSize = 13.sp)

            Spacer(modifier = Modifier.height(30.dp))

            // Estadísticas calculadas con los datos reales
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(reservedCount.toString(), "Reservadas", modifier = Modifier.weight(1f))
                StatCard(completedCount.toString(), "Completadas", modifier = Modifier.weight(1f))
                StatCard(routinesCompletedCount.toString(), "Rutinas", modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(30.dp))

            OutlinedButton(
                onClick = { showEditDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Edit, contentDescription = null, tint = primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Editar perfil", color = primary)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { showLogoutDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFDE2E1)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, tint = Color(0xFFB3261E))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Cerrar sesión", color = Color(0xFFB3261E))
            }
        }
    }

    if (showEditDialog) {
        EditNameDialog(
            currentName = user.fullName,
            onDismiss = { showEditDialog = false },
            onSave = { newName ->
                showEditDialog = false
                onSaveName(newName)
            }
        )
    }

    // Confirmación antes de cerrar sesión
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Cerrar sesión", fontWeight = FontWeight.Bold) },
            text = { Text("¿Seguro que quieres cerrar sesión?") },
            confirmButton = {
                TextButton(onClick = {
                    showLogoutDialog = false
                    onLogout()
                }) {
                    Text("Cerrar sesión", color = Color(0xFFB3261E), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancelar", color = Color.Gray)
                }
            },
            shape = RoundedCornerShape(12.dp)
        )
    }
}

// Diálogo para cambiar el nombre, con validación
@Composable
private fun EditNameDialog(
    currentName: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var name by remember { mutableStateOf(currentName) }

    // Validación: no vacío y al menos 3 letras
    val nameError = when {
        name.isBlank() -> "El nombre no puede estar vacío"
        name.count { it.isLetter() } < 3 -> "Debe tener al menos 3 letras"
        else -> null
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar perfil", fontWeight = FontWeight.Bold) },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre completo") },
                singleLine = true,
                isError = nameError != null,
                supportingText = { nameError?.let { Text(it) } },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onSave(name.trim()) },
                enabled = nameError == null
            ) {
                Text("Guardar", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = Color.Gray)
            }
        },
        shape = RoundedCornerShape(12.dp)
    )
}

@Composable
fun StatCard(number: String, label: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF2F2F2))
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(number, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text(label, color = Color.Gray, fontSize = 12.sp)
        }
    }
}
