package com.flores.mycar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.flores.mycar.ui.MycarViewModelFactory
import com.flores.mycar.ui.lista.ListaScreen
import com.flores.mycar.ui.lista.ListaViewModel
import com.flores.mycar.ui.theme.MycarTheme

class MainActivity : ComponentActivity() {

    private val listaViewModel: ListaViewModel by viewModels {
        MycarViewModelFactory((application as MycarApp).container)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MycarTheme {
                ListaScreen(viewModel = listaViewModel)
            }
        }
    }
}
