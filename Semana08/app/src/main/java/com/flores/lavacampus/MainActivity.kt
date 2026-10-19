package com.flores.lavacampus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.flores.lavacampus.model.Rol
import com.flores.lavacampus.ui.navigation.NavLava
import com.flores.lavacampus.ui.theme.LavaTheme
import com.flores.lavacampus.viewmodel.LavaViewModelFactory
import com.flores.lavacampus.viewmodel.SesionViewModel

class MainActivity : ComponentActivity() {

    private val sesionVm: SesionViewModel by viewModels {
        val app = application as LavaApp
        LavaViewModelFactory(app.repository, app.prefs)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val usuario by sesionVm.usuarioActual.collectAsState()
            val oscuro by sesionVm.modoOscuro.collectAsState()
            // El tema depende del perfil: turquesa (estudiante) o violeta (administrador)
            val rol = if (usuario?.rol == Rol.ADMIN.name) Rol.ADMIN else Rol.CLIENTE
            LavaTheme(rol = rol, darkTheme = oscuro) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavLava(sesionVm, (application as LavaApp).repository, (application as LavaApp).prefs)
                }
            }
        }
    }
}
