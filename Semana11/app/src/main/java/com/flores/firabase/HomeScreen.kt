package com.flores.firabase

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.flores.firabase.ui.theme.PaletaCursos
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.math.abs

@Composable
fun HomeScreen(onLogout: () -> Unit) {
    IconosBarraEstado(oscuros = false)   // la cabecera es roja: íconos claros

    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val usuario = auth.currentUser
    val uid = usuario?.uid.orEmpty()
    val correo = usuario?.email.orEmpty()
    val nombre = (usuario?.displayName ?: "").ifBlank { correo.substringBefore("@") }
        .ifBlank { "Estudiante" }
        .replaceFirstChar { it.uppercase() }

    val cursos = remember { mutableStateListOf<Curso>() }
    val snackbar = remember { SnackbarHostState() }
    var mensaje by remember { mutableStateOf<String?>(null) }
    var dialogo by remember { mutableStateOf(false) }
    var editando by remember { mutableStateOf<Curso?>(null) }
    var porBorrar by remember { mutableStateOf<Curso?>(null) }

    LaunchedEffect(mensaje) {
        mensaje?.let {
            snackbar.showSnackbar(it)
            mensaje = null
        }
    }

    // READ: solo los cursos del usuario autenticado, en tiempo real
    DisposableEffect(uid) {
        val reg = db.collection("cursos")
            .whereEqualTo("uid", uid)
            .addSnapshotListener { snap, e ->
                if (e != null) {
                    mensaje = e.localizedMessage
                    return@addSnapshotListener
                }
                cursos.clear()
                snap?.documents?.forEach { d ->
                    cursos.add(
                        Curso(
                            id = d.id,
                            uid = d.getString("uid").orEmpty(),
                            nombre = d.getString("nombre").orEmpty(),
                            docente = d.getString("docente").orEmpty(),
                            creditos = (d.getLong("creditos") ?: 0L).toInt()
                        )
                    )
                }
                cursos.sortBy { it.nombre.lowercase() }
            }
        onDispose { reg.remove() }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbar) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { editando = null; dialogo = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(painterResource(R.drawable.ic_add), contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Nuevo curso", fontWeight = FontWeight.SemiBold)
            }
        }
    ) { inner ->
        LazyColumn(
            Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = inner.calculateBottomPadding() + 96.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                CabeceraHome(
                    nombre = nombre,
                    correo = correo,
                    totalCursos = cursos.size,
                    totalCreditos = cursos.sumOf { it.creditos },
                    onSalir = {
                        auth.signOut()
                        onLogout()
                    }
                )
            }

            item {
                Text(
                    "Mis cursos",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(start = 20.dp, top = 8.dp)
                )
            }

            if (cursos.isEmpty()) {
                item { EstadoVacio() }
            }

            items(cursos, key = { it.id }) { curso ->
                TarjetaCurso(
                    curso = curso,
                    onEditar = { editando = curso; dialogo = true },
                    onBorrar = { porBorrar = curso }
                )
            }

            item { PieAutora() }
        }
    }

    if (dialogo) {
        CursoDialog(
            inicial = editando,
            onCancelar = { dialogo = false },
            onGuardar = { nombreCurso, docente, creditos ->
                val datos = hashMapOf(
                    "uid" to uid,
                    "nombre" to nombreCurso,
                    "docente" to docente,
                    "creditos" to creditos
                )
                val actual = editando
                val tarea = if (actual == null) {
                    db.collection("cursos").add(datos)                      // CREATE
                } else {
                    db.collection("cursos").document(actual.id).set(datos)  // UPDATE
                }
                tarea.addOnFailureListener { mensaje = it.localizedMessage }
                dialogo = false
            }
        )
    }

    porBorrar?.let { curso ->
        AlertDialog(
            onDismissRequest = { porBorrar = null },
            shape = RoundedCornerShape(24.dp),
            title = { Text("Eliminar curso") },
            text = { Text("¿Seguro que deseas eliminar \"${curso.nombre}\"? Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = {
                    // DELETE
                    db.collection("cursos").document(curso.id).delete()
                        .addOnFailureListener { mensaje = it.localizedMessage }
                    porBorrar = null
                }) { Text("Eliminar", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = { TextButton(onClick = { porBorrar = null }) { Text("Cancelar") } }
        )
    }
}

@Composable
private fun CabeceraHome(
    nombre: String,
    correo: String,
    totalCursos: Int,
    totalCreditos: Int,
    onSalir: () -> Unit
) {
    EncabezadoDegradado {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 12.dp, top = 12.dp, bottom = 24.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        nombre.first().uppercase(),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge
                    )
                }
                Spacer(Modifier.width(14.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        "Bienvenida/o,",
                        color = Color.White.copy(alpha = 0.8f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        nombre,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        correo,
                        color = Color.White.copy(alpha = 0.8f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                IconButton(onClick = onSalir) {
                    Icon(
                        painterResource(R.drawable.ic_logout),
                        contentDescription = "Cerrar sesión",
                        tint = Color.White
                    )
                }
            }

            Spacer(Modifier.height(20.dp))
            Row(
                Modifier.padding(end = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Estadistica("Cursos", totalCursos.toString(), Modifier.weight(1f))
                Estadistica("Créditos", totalCreditos.toString(), Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun Estadistica(titulo: String, valor: String, modifier: Modifier = Modifier) {
    Column(
        modifier
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White.copy(alpha = 0.16f))
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            valor,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            titulo,
            color = Color.White.copy(alpha = 0.85f),
            style = MaterialTheme.typography.labelLarge
        )
    }
}

@Composable
private fun EstadoVacio() {
    Card(
        Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Aún no tienes cursos",
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "Toca \"Nuevo curso\" para registrar el primero.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun TarjetaCurso(curso: Curso, onEditar: () -> Unit, onBorrar: () -> Unit) {
    val color = PaletaCursos[abs(curso.nombre.hashCode()) % PaletaCursos.size]
    Card(
        Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(color),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        curso.nombre.take(1).uppercase(),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge
                    )
                }
                Spacer(Modifier.width(14.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        curso.nombre,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        "Docente: ${curso.docente}",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(50)
                ) {
                    Text(
                        "${curso.creditos} cr.",
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = onEditar) {
                    Icon(
                        painterResource(R.drawable.ic_edit),
                        contentDescription = "Editar",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(onClick = onBorrar) {
                    Icon(
                        painterResource(R.drawable.ic_delete),
                        contentDescription = "Borrar",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun CursoDialog(
    inicial: Curso?,
    onCancelar: () -> Unit,
    onGuardar: (String, String, Int) -> Unit
) {
    var nombre by remember { mutableStateOf(inicial?.nombre.orEmpty()) }
    var docente by remember { mutableStateOf(inicial?.docente.orEmpty()) }
    var creditos by remember { mutableStateOf(inicial?.creditos?.toString().orEmpty()) }
    val valido = nombre.isNotBlank() && docente.isNotBlank() && (creditos.toIntOrNull() ?: 0) in 1..12

    AlertDialog(
        onDismissRequest = onCancelar,
        shape = RoundedCornerShape(24.dp),
        title = { Text(if (inicial == null) "Nuevo curso" else "Editar curso", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                CampoTexto(nombre, { nombre = it }, "Nombre del curso")
                CampoTexto(docente, { docente = it }, "Docente")
                CampoTexto(
                    creditos, { creditos = it.filter(Char::isDigit).take(2) },
                    "Créditos (1 a 12)",
                    tipo = KeyboardType.Number,
                    imeAction = ImeAction.Done
                )
            }
        },
        confirmButton = {
            TextButton(
                enabled = valido,
                onClick = { onGuardar(nombre.trim(), docente.trim(), creditos.toInt()) }
            ) { Text("Guardar", fontWeight = FontWeight.SemiBold) }
        },
        dismissButton = { TextButton(onClick = onCancelar) { Text("Cancelar") } }
    )
}
