package com.example.app_kotlin.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.example.app_kotlin.model.Sesion
import com.example.app_kotlin.nativo.BiometriaHelper
import com.example.app_kotlin.nativo.IntentsHelper
import com.example.app_kotlin.ui.components.AvatarIniciales
import com.example.app_kotlin.ui.components.GlassCard
import com.example.app_kotlin.ui.components.TituloSeccion

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(
    onCerrarSesionClick: () -> Unit
) {
    val context = LocalContext.current

    val tienePermisoNotif = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
    } else true

    val tieneBiometria = if (context is FragmentActivity) {
        BiometriaHelper.puedeAutenticar(context)
    } else false

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Perfil y Diagnóstico", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            GlassCard(
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AvatarIniciales(
                            nombre = if (Sesion.usuarioActivo.isNotBlank()) Sesion.usuarioActivo else "Usuario WIM",
                            size = 64.dp
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = if (Sesion.usuarioActivo.isNotBlank()) Sesion.usuarioActivo else "Sin Sesión",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Rol activo: ${Sesion.rolActivo.titulo}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

                    Text(
                        text = Sesion.rolActivo.descripcion,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            TituloSeccion(
                titulo = "Diagnóstico de Hardware y Permisos",
                subtitulo = "Sensores del dispositivo y autorizaciones en tiempo de ejecución"
            )

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Notifications, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Notificaciones Locales", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            Text(
                                text = if (tienePermisoNotif) "Permiso activo (POST_NOTIFICATIONS)" else "Permiso pendiente / denegado",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (tienePermisoNotif) com.example.app_kotlin.ui.theme.SuccessEmerald else com.example.app_kotlin.ui.theme.WarningAmber
                            )
                        }
                        Icon(
                            imageVector = if (tienePermisoNotif) Icons.Default.CheckCircle else Icons.Default.Warning,
                            contentDescription = null,
                            tint = if (tienePermisoNotif) com.example.app_kotlin.ui.theme.SuccessEmerald else com.example.app_kotlin.ui.theme.WarningAmber
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Fingerprint, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Sensor de Biometría / PIN", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            Text(
                                text = if (tieneBiometria) "Subelemento autenticador disponible" else "No configurado en el dispositivo",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (tieneBiometria) com.example.app_kotlin.ui.theme.SuccessEmerald else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Icon(
                            imageVector = if (tieneBiometria) Icons.Default.CheckCircle else Icons.Default.Warning,
                            contentDescription = null,
                            tint = if (tieneBiometria) com.example.app_kotlin.ui.theme.SuccessEmerald else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            OutlinedButton(
                onClick = { IntentsHelper.abrirAjustesAplicacion(context) },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Settings, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Abrir Configuración de la App en Android")
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onCerrarSesionClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = com.example.app_kotlin.ui.theme.DangerRose
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("CERRAR SESIÓN", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
        }
    }
}