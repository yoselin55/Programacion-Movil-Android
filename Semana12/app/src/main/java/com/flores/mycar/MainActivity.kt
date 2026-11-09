package com.flores.mycar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.flores.mycar.ui.MycarViewModelFactory
import com.flores.mycar.ui.navigation.AppNav
import com.flores.mycar.ui.theme.MycarTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val factory = MycarViewModelFactory((application as MycarApp).container)
        setContent {
            MycarTheme {
                AppNav(factory)
            }
        }
    }
}
