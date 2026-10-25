package com.example.app_kotlin.nativo

import android.Manifest
import android.content.Context
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext

@Composable
fun SolicitudPermisoNotificaciones() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return

    val context = LocalContext.current
    var mostrarDialogo by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ ->
        mostrarDialogo = false
    }

    LaunchedEffect(Unit) {
        val prefs = context.getSharedPreferences("wim_prefs", Context.MODE_PRIVATE)
        val yaSolicitado = prefs.getBoolean("permiso_notif_solicitado", false)
        if (!yaSolicitado) {
            mostrarDialogo = true
            prefs.edit().putBoolean("permiso_notif_solicitado", true).apply()
        }
    }

    if (mostrarDialogo) {
        AlertDialog(
            onDismissRequest = { mostrarDialogo = false },
            icon = { Icon(Icons.Default.NotificationsActive, contentDescription = null) },
            title = { Text("Notificaciones WIM 100") },
            text = { Text("Le enviaremos alertas locales cuando se registren nuevas evaluaciones y se consolide el ranking de postulantes.") },
            confirmButton = {
                TextButton(onClick = {
                    mostrarDialogo = false
                    launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }) {
                    Text("Permitir")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogo = false }) {
                    Text("Ahora no")
                }
            }
        )
    }
}