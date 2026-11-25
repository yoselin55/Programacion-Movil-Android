package com.flores.murocampus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.flores.murocampus.data.PostRepositoryRemote
import com.flores.murocampus.data.remote.RetrofitClient
import com.flores.murocampus.ui.BarraLateral
import com.flores.murocampus.ui.Destino
import com.flores.murocampus.ui.Encabezado
import com.flores.murocampus.ui.PantallaCrear
import com.flores.murocampus.ui.PantallaDetalle
import com.flores.murocampus.ui.PantallaLista
import com.flores.murocampus.ui.theme.MuroCampusTheme
import com.flores.murocampus.viewmodel.PostsViewModel
import com.flores.murocampus.viewmodel.PostsViewModelFactory

class MainActivity : ComponentActivity() {

    private val vm: PostsViewModel by viewModels {
        PostsViewModelFactory(PostRepositoryRemote(RetrofitClient.api))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MuroCampusTheme {
                // null = se ve la lista; con un id se ve el detalle.
                var idSeleccionado by rememberSaveable { mutableStateOf<Int?>(null) }
                // true = se ve el formulario de crear publicacion.
                var creando by rememberSaveable { mutableStateOf(false) }
                // IA commit 4: true = menu lateral abierto (icono y nombre).
                var menuAbierto by rememberSaveable { mutableStateOf(false) }

                BackHandler(enabled = idSeleccionado != null) { idSeleccionado = null }
                BackHandler(enabled = creando) { creando = false }
                BackHandler(enabled = menuAbierto) { menuAbierto = false }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    // Contenido: deja 68 dp a la izquierda para la barra cerrada.
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(start = 68.dp)
                            .windowInsetsPadding(WindowInsets.systemBars)
                    ) {
                        val id = idSeleccionado
                        when {
                            creando -> Encabezado(
                                "Nueva publicación",
                                "Cuéntale algo al campus",
                                onVolver = { creando = false }
                            )
                            id != null -> Encabezado(
                                "Detalle",
                                "Publicación y comentarios",
                                onVolver = { idSeleccionado = null }
                            )
                            else -> Encabezado("MuroCampus", "Tu muro del campus")
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            when {
                                creando -> PantallaCrear(vm = vm)
                                id != null -> PantallaDetalle(
                                    vm = vm,
                                    idPost = id,
                                    onBorrado = { idSeleccionado = null }
                                )
                                else -> PantallaLista(
                                    vm = vm,
                                    onPostClick = { vm.abrir(it); idSeleccionado = it }
                                )
                            }
                        }
                    }

                    // Fondo oscuro: al tocarlo, el menu se cierra.
                    if (menuAbierto) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.30f))
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) { menuAbierto = false }
                        )
                    }

                    BarraLateral(
                        destino = if (creando) Destino.Crear else Destino.Inicio,
                        expandida = menuAbierto,
                        onAlternar = { menuAbierto = !menuAbierto },
                        onDestino = { destino ->
                            when (destino) {
                                Destino.Inicio -> {
                                    creando = false
                                    idSeleccionado = null
                                }
                                Destino.Crear -> {
                                    vm.reiniciarCrear()
                                    idSeleccionado = null
                                    creando = true
                                }
                            }
                            menuAbierto = false
                        },
                        modifier = Modifier.align(Alignment.CenterStart)
                    )
                }
            }
        }
    }
}
