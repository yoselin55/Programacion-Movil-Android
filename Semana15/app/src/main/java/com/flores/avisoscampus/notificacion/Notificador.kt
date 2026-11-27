package com.flores.avisoscampus.notificacion

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.flores.avisoscampus.model.Aviso

object Notificador {

    const val CANAL = "avisos"

    fun crearCanal(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val canal = NotificationChannel(
                CANAL,
                "Avisos",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(canal)
        }
    }

    // TODO Paso 2: fun tienePermiso(context: Context): Boolean

    fun mostrar(context: Context, aviso: Aviso): Boolean {
        // TODO Paso 3: si no tienePermiso(context) -> return false
        // TODO Paso 3: construir la notificacion con NotificationCompat.Builder
        // TODO Paso 3: NotificationManagerCompat.from(context).notify(aviso.id, ...)
        // TODO Paso 4: PendingIntent para abrir MainActivity al tocar
        return true
    }
}
