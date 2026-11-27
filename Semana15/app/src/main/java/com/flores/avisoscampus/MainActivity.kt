package com.flores.avisoscampus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.flores.avisoscampus.data.AvisosRepositoryMemoria
import com.flores.avisoscampus.ui.PantallaAvisos
import com.flores.avisoscampus.ui.theme.AvisosCampusTheme
import com.flores.avisoscampus.viewmodel.AvisosViewModel
import com.flores.avisoscampus.viewmodel.AvisosViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AvisosCampusTheme {
                val vm: AvisosViewModel = viewModel(
                    factory = AvisosViewModelFactory(AvisosRepositoryMemoria())
                )
                PantallaAvisos(vm)
            }
        }
    }
}
