package com.flores.saludplus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.flores.saludplus.navigation.AppNavigation
import com.flores.saludplus.ui.theme.SaludplusTheme

// Relaciones:
// - Es el punto de entrada: lo abre AndroidManifest.xml al iniciar la app
// - Llama a SaludplusTheme (ui/theme/Theme.kt) para aplicar el tema
// - Llama a AppNavigation (navigation/AppNavigation.kt) que muestra las pantallas

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SaludplusTheme {
                AppNavigation()
            }
        }
    }
}
