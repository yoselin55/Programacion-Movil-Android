package com.flores.murocampus.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.flores.murocampus.viewmodel.PostsViewModel
import com.flores.murocampus.viewmodel.UiState

// Commit 7: la pantalla solo dibuja el estado; nunca llama a la API.
@Composable
fun PantallaLista(vm: PostsViewModel, modifier: Modifier = Modifier) {
    val estado by vm.lista.collectAsStateWithLifecycle()

    when (val e = estado) {
        is UiState.Cargando -> Box(
            modifier = modifier.fillMaxSize().padding(16.dp),
            contentAlignment = Alignment.Center
        ) { Cargando() }

        is UiState.Error -> Box(
            modifier = modifier.fillMaxSize().padding(16.dp),
            contentAlignment = Alignment.Center
        ) { ErrorConReintento(mensaje = e.mensaje, onReintentar = { vm.cargar() }) }

        is UiState.Exito -> LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(e.datos, key = { it.id }) { post -> TarjetaPost(post) }
        }
    }
}
