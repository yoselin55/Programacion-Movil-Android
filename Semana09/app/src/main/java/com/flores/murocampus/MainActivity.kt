package com.flores.murocampus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
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
                BackHandler(enabled = idSeleccionado != null) { idSeleccionado = null }
                BackHandler(enabled = creando) { creando = false }

                // IA commit 1: barra lateral rosa a la izquierda y contenido a la derecha.
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    BarraLateral(
                        destino = if (creando) Destino.Crear else Destino.Inicio,
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
                        }
                    )
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize()
                            .windowInsetsPadding(WindowInsets.systemBars)
                    ) {
                        val id = idSeleccionado
                        when {
                            creando -> Encabezado("Nueva publicación", "Cuéntale algo al campus")
                            id != null -> Encabezado("Detalle", "Publicación y comentarios")
                            else -> Encabezado("MuroCampus", "Tu muro del campus")
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            when {
                                creando -> PantallaCrear(vm = vm)
                                id != null -> PantallaDetalle(vm = vm, idPost = id)
                                else -> PantallaLista(
                                    vm = vm,
                                    onPostClick = { vm.abrir(it); idSeleccionado = it }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
