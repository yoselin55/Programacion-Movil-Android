package com.flores.murocampus

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.flores.murocampus.data.remote.RetrofitClient
import com.flores.murocampus.ui.theme.MuroCampusTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Commit 5: prueba temporal, se borra antes del commit.
        lifecycleScope.launch {
            val posts = RetrofitClient.api.listar()
            Log.d("MuroCampus", "Llegaron ${posts.size} publicaciones")
        }
        setContent {
            MuroCampusTheme {
                PantallaBase()
            }
        }
    }
}

// Commit 1: importa el proyecto base; barra superior MuroCampus y contenido vacío.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaBase() {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
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
        Box(Modifier.fillMaxSize().padding(innerPadding))
    }
}
