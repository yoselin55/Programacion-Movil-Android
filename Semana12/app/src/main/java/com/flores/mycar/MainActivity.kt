package com.flores.mycar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.flores.mycar.ui.lista.ListaScreen
import com.flores.mycar.ui.theme.MycarTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MycarTheme {
                ListaScreen()
            }
        }
    }
}
