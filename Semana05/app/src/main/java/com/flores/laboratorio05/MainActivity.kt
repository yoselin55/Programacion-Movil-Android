package com.flores.laboratorio05

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.flores.laboratorio05.navigation.AppNavigation
import com.flores.laboratorio05.ui.theme.PortalAcademicoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // setContent define la interfaz visual principal de la aplicación en Jetpack Compose
        setContent {
            PortalAcademicoTheme {
                // Invocamos a AppNavigation, que se encarga de determinar
                // qué pantalla mostrar según la ruta activa (inicia en LoginScreen)
                AppNavigation()
            }
        }
    }
}
