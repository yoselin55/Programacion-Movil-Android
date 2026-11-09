package com.flores.mycar

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.flores.mycar.ui.MycarViewModelFactory
import com.flores.mycar.ui.navigation.AppNav
import com.flores.mycar.ui.theme.MycarTheme

class MainActivity : ComponentActivity() {

    // Si la persona niega el permiso, la app funciona igual pero sin avisos.
    private val pedirPermisoNotificaciones =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        pedirPermisoSiHaceFalta()
        val factory = MycarViewModelFactory((application as MycarApp).container)
        setContent {
            MycarTheme {
                AppNav(factory)
            }
        }
    }

    /** En Android 13 o superior las notificaciones necesitan permiso en tiempo de ejecucion. */
    private fun pedirPermisoSiHaceFalta() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            pedirPermisoNotificaciones.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}
