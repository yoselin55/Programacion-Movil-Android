package com.flores.firabase

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.flores.firabase.ui.theme.FirabaseTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Pantalla completa; el color de los íconos de la barra lo fija cada pantalla
        enableEdgeToEdge()
        setContent {
            FirabaseTheme {
                Surface(Modifier.fillMaxSize()) {
                    AuthApp()
                }
            }
        }
    }
}
