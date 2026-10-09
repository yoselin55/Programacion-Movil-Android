package com.flores.tecsupstore

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val colorMorado = Color(0xFF5B1DA3)
private val colorFondoSuave = Color(0xFFF5F0FB)
private val colorTexto = Color(0xFF1D1B20)
private val colorTextoSecundario = Color(0xFF49454F)

data class Pedido(
    val codigo: String,
    val producto: String,
    val total: String,
    val estado: String
)

// Lista de productos reutilizada por "Inicio" y "Favoritos"
@Composable
fun ListaProductos(
    productos: List<Pair<String, String>>,
    favoritos: Set<String>,
    onToggleFavorito: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        itemsIndexed(productos) { index, (nombre, precio) ->
            TarjetaProducto(
                nombre = nombre,
                precio = precio,
                esFavorito = favoritos.contains(nombre),
                onToggleFavorito = { onToggleFavorito(nombre) },
                tieneBorde = (index == 0)
            )
        }
    }
}

@Composable
fun PantallaFavoritos(
    productos: List<Pair<String, String>>,
    favoritos: Set<String>,
    onToggleFavorito: (String) -> Unit
) {
    val productosFavoritos = productos.filter { favoritos.contains(it.first) }
    if (productosFavoritos.isEmpty()) {
        MensajeVacio(
            icono = Icons.Default.FavoriteBorder,
            titulo = "Aún no tienes favoritos",
            descripcion = "Usa el menú ⋮ de un producto en Inicio y elige \"Favoritos\"."
        )
    } else {
        ListaProductos(
            productos = productosFavoritos,
            favoritos = favoritos,
            onToggleFavorito = onToggleFavorito
        )
    }
}

@Composable
fun PantallaPedidos(pedidos: List<Pedido>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        items(pedidos) { pedido ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = colorFondoSuave)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(Color(0xFFE8DEF8), shape = RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalShipping,
                            contentDescription = "Pedido",
                            tint = colorMorado
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = pedido.producto,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = colorTexto
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${pedido.codigo} · S/ ${pedido.total}",
                            fontSize = 14.sp,
                            color = colorTextoSecundario
                        )
                    }
                    val entregado = pedido.estado == "Entregado"
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = if (entregado) Color(0xFFDFF5E1) else Color(0xFFFFF1D6)
                    ) {
                        Text(
                            text = pedido.estado,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (entregado) Color(0xFF1E6B2A) else Color(0xFF8A5A00),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PantallaPerfil(
    nombreUsuario: String,
    emailUsuario: String,
    cantidadFavoritos: Int,
    cantidadPedidos: Int
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(CircleShape)
                .background(Color(0xFFE8DEF8)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Foto de perfil",
                tint = colorMorado,
                modifier = Modifier.size(56.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = nombreUsuario,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = colorTexto,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            DatoResumen("Pedidos", cantidadPedidos.toString(), Modifier.weight(1f))
            Spacer(modifier = Modifier.width(12.dp))
            DatoResumen("Favoritos", cantidadFavoritos.toString(), Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(16.dp))

        FilaPerfil(Icons.Default.Email, "Correo", emailUsuario)
        FilaPerfil(Icons.Default.School, "Institución", "TECSUP")
    }
}

@Composable
private fun DatoResumen(titulo: String, valor: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colorFondoSuave)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = valor, fontWeight = FontWeight.Bold, fontSize = 22.sp, color = colorMorado)
            Text(text = titulo, fontSize = 13.sp, color = colorTextoSecundario)
        }
    }
}

@Composable
private fun FilaPerfil(icono: ImageVector, titulo: String, valor: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icono, contentDescription = titulo, tint = colorMorado)
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = titulo, fontSize = 12.sp, color = colorTextoSecundario)
            Text(text = valor, fontSize = 15.sp, color = colorTexto)
        }
    }
}

@Composable
fun PantallaSesionCerrada(onIniciarSesion: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Lock,
            contentDescription = "Sesión cerrada",
            tint = colorMorado,
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Sesión cerrada",
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            color = colorTexto
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Gracias por visitar TECSUP Store.",
            fontSize = 14.sp,
            color = colorTextoSecundario,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onIniciarSesion,
            colors = ButtonDefaults.buttonColors(containerColor = colorMorado)
        ) {
            Text("Iniciar sesión")
        }
    }
}

@Composable
private fun MensajeVacio(icono: ImageVector, titulo: String, descripcion: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icono,
            contentDescription = null,
            tint = colorMorado,
            modifier = Modifier.size(56.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(text = titulo, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = colorTexto)
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = descripcion,
            fontSize = 14.sp,
            color = colorTextoSecundario,
            textAlign = TextAlign.Center
        )
    }
}
