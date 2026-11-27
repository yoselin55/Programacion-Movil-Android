package com.flores.avisoscampus.notificacion

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.flores.avisoscampus.MainActivity
import com.flores.avisoscampus.R
import com.flores.avisoscampus.model.Aviso

object Notificador {

    const val CANAL = "avisos"
    const val CANAL_URGENTE = "urgentes"

    fun crearCanal(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = context.getSystemService(NotificationManager::class.java)
            // Canal normal para los avisos de todos los días
            manager.createNotificationChannel(
                NotificationChannel(CANAL, "Avisos", NotificationManager.IMPORTANCE_DEFAULT)
            )
            // Canal urgente: IMPORTANCE_HIGH hace que la notificación sea emergente
            manager.createNotificationChannel(
                NotificationChannel(CANAL_URGENTE, "Urgentes", NotificationManager.IMPORTANCE_HIGH)
            )
        }
    }

    fun tienePermiso(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    }

    // Texto que se muestra cuando la notificación está expandida
    private fun textoLargo(aviso: Aviso): String =
        "${aviso.texto}\nOrigen: ${aviso.origen.texto} · ${aviso.hora}"

    @SuppressLint("MissingPermission")
    fun mostrar(context: Context, aviso: Aviso): Boolean {
        if (!tienePermiso(context)) return false

        // Al tocar la notificación se abre la app
        val intent = Intent(context, MainActivity::class.java)
        val pendiente = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        // El aviso urgente va por su canal y con prioridad alta (Android 7 o menos)
        val canal = if (aviso.urgente) CANAL_URGENTE else CANAL
        val prioridad = if (aviso.urgente) {
            NotificationCompat.PRIORITY_HIGH
        } else {
            NotificationCompat.PRIORITY_DEFAULT
        }

        val notificacion = NotificationCompat.Builder(context, canal)
            .setSmallIcon(R.drawable.ic_notificacion)
            .setContentTitle(aviso.titulo)
            .setContentText(aviso.texto)
            // Al expandir la notificación se ve el texto completo y el detalle
            .setStyle(NotificationCompat.BigTextStyle().bigText(textoLargo(aviso)))
            .setPriority(prioridad)
            .setAutoCancel(true)
            .setContentIntent(pendiente)
            .build()

        // El id del aviso hace que cada notificación sea distinta
        NotificationManagerCompat.from(context).notify(aviso.id, notificacion)
        return true
    }
}
