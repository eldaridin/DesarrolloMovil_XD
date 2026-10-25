package com.example.app_kotlin.nativo

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.app_kotlin.R

interface Notificador {
    fun notificarEvaluacionRegistrada(nombreCandidata: String, puntaje: Double)
}

class NotificadorAndroid(private val context: Context) : Notificador {

    companion object {
        const val CANAL_ID = "wim_evaluaciones_channel"
    }

    init {
        crearCanalNotificacion()
    }

    private fun crearCanalNotificacion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nombre = context.getString(R.string.canal_evaluaciones_nombre)
            val descripcion = context.getString(R.string.canal_evaluaciones_desc)
            val importancia = NotificationManager.IMPORTANCE_DEFAULT
            val canal = NotificationChannel(CANAL_ID, nombre, importancia).apply {
                description = descripcion
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(canal)
        }
    }

    override fun notificarEvaluacionRegistrada(nombreCandidata: String, puntaje: Double) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
        }

        val notification = NotificationCompat.Builder(context, CANAL_ID)
            .setSmallIcon(R.drawable.ic_notificacion)
            .setContentTitle("Nueva Evaluación Registrada - WIM 100")
            .setContentText("Evaluación guardada para $nombreCandidata (Puntaje: ${puntaje.toInt()}/100)")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify((System.currentTimeMillis() % 10000).toInt(), notification)
    }
}