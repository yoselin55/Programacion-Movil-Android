package com.flores.murocampus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import com.flores.murocampus.data.PostRepositoryRemote
import com.flores.murocampus.data.remote.RetrofitClient
import com.flores.murocampus.ui.PantallaCrear
import com.flores.murocampus.ui.PantallaDetalle
import com.flores.murocampus.ui.PantallaLista
import com.flores.murocampus.ui.theme.MuroCampusTheme
import com.flores.murocampus.viewmodel.PostsViewModel
import com.flores.murocampus.viewmodel.PostsViewModelFactory

class MainActivity : ComponentActivity() {

    // Commit 7: se arma Repository -> ViewModel con la fabrica; se borra la prueba temporal.
    private val vm: PostsViewModel by viewModels {
        PostsViewModelFactory(PostRepositoryRemote(RetrofitClient.api))
    }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MuroCampusTheme {
                // Commit 8: null = se ve la lista; con un id se ve el detalle.
                var idSeleccionado by rememberSaveable { mutableStateOf<Int?>(null) }
                BackHandler(enabled = idSeleccionado != null) { idSeleccionado = null }
                // Commit 10: true = se ve el formulario de crear publicacion.
                var creando by rememberSaveable { mutableStateOf(false) }
                BackHandler(enabled = creando) { creando = false }
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    floatingActionButton = {
                        if (!creando && idSeleccionado == null) {
                            FloatingActionButton(onClick = { vm.reiniciarCrear(); creando = true }) {
                                Text("+", fontSize = 24.sp)
                            }
                        }
                    },
                    topBar = {
                        TopAppBar(
                            title = { Text("MuroCampus") },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                titleContentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                    }
                ) { innerPadding ->
                    val id = idSeleccionado
                    if (creando) {
                        PantallaCrear(vm = vm, modifier = Modifier.padding(innerPadding))
                    } else if (id == null) {
                        PantallaLista(
                            vm = vm,
                            onPostClick = { vm.abrir(it); idSeleccionado = it },
                            modifier = Modifier.padding(innerPadding)
                        )
                    } else {
                        PantallaDetalle(vm = vm, idPost = id, modifier = Modifier.padding(innerPadding))
                    }
                }
            }
        }
    }
}
