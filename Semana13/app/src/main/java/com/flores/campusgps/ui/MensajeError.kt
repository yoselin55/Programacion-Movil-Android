package com.flores.campusgps.ui

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.flores.campusgps.model.TipoError
import com.flores.campusgps.model.UbicacionUi

// Commit 9: recuadro de error con el texto y, debajo, el botón que corresponde según el tipo de error.
@Composable
fun MensajeError(
    error: UbicacionUi.Error,
    onReintentar: () -> Unit,
    modifier: Modifier = Modifier
) {
    val contexto = LocalContext.current

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(error.mensaje, color = MaterialTheme.colorScheme.onErrorContainer)

            when (error.tipo) {
                // Permiso denegado: lleva a los ajustes de la app para activarlo a mano.
                TipoError.SIN_PERMISO -> Button(
                    onClick = {
                        val intent = Intent(
                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.fromParts("package", contexto.packageName, null)
                        )
                        contexto.startActivity(intent)
                    },
                    modifier = Modifier.padding(top = 8.dp)
                ) { Text("Abrir ajustes") }

                // Sin Play Services no tiene sentido reintentar: no hay botón.
                TipoError.SIN_PLAY_SERVICES -> Unit

                // GPS apagado u otro problema: se puede volver a intentar.
                TipoError.GPS_APAGADO, TipoError.OTRO -> Button(
                    onClick = onReintentar,
                    modifier = Modifier.padding(top = 8.dp)
                ) { Text("Reintentar") }
            }
        }
    }
}
