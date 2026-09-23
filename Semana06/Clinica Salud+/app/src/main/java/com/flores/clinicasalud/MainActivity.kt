package com.flores.clinicasalud

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.flores.clinicasalud.navigation.MainAppNavigation
import com.flores.clinicasalud.ui.theme.ClinicaSaludTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ClinicaSaludTheme {
                MainAppNavigation() // <--- Asegúrate de que llame a esta función
            }
        }
    }
}