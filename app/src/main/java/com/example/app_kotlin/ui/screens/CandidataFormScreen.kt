package com.example.app_kotlin.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.app_kotlin.model.Candidata
import com.example.app_kotlin.ui.components.CampoValidado
import com.example.app_kotlin.ui.components.GlassCard
import com.example.app_kotlin.ui.components.TituloSeccion
import com.example.app_kotlin.ui.components.sacudir
import com.example.app_kotlin.viewmodel.CandidataFormUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CandidataFormScreen(
    state: CandidataFormUiState,
    onNombreChange: (String) -> Unit,
    onCorreoChange: (String) -> Unit,
    onTelefonoChange: (String) -> Unit,
    onAreaChange: (String) -> Unit,
    onAnnosExpChange: (String) -> Unit,
    onResumenExpChange: (String) -> Unit,
    onLinkedinUrlChange: (String) -> Unit,
    onArchivoPdfSeleccionado: (Uri?) -> Unit,
    onGuardarClick: () -> Unit,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    var areaMenuExpanded by remember { mutableStateOf(false) }

    val pdfLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        onArchivoPdfSeleccionado(uri)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registro de Postulación", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
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
            TituloSeccion(
                titulo = "Antecedentes de la Postulante",
                subtitulo = "Información básica y contacto verificado"
            )

            CampoValidado(
                campo = state.nombre,
                onValueChange = onNombreChange,
                label = "Nombre completo (Nombre y Apellido)",
                icono = Icons.Default.Person,
                placeholder = "Ej: Valentina Silva M."
            )

            CampoValidado(
                campo = state.correo,
                onValueChange = onCorreoChange,
                label = "Correo electrónico ficticio",
                icono = Icons.Default.Email,
                placeholder = "ejemplo: valentina@ejemplo.cl",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            CampoValidado(
                campo = state.telefono,
                onValueChange = onTelefonoChange,
                label = "Teléfono chileno de contacto",
                icono = Icons.Default.Phone,
                placeholder = "+56 9 8765 4321",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )

            TituloSeccion(
                titulo = "Perfil Profesional y Ciberseguridad",
                subtitulo = "Especialidad en el programa WIM 100"
            )

            ExposedDropdownMenuBox(
                expanded = areaMenuExpanded,
                onExpandedChange = { areaMenuExpanded = !areaMenuExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = state.area.valor,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Área de especialización WIM") },
                    leadingIcon = { Icon(Icons.Default.BusinessCenter, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = areaMenuExpanded) },
                    isError = state.area.error != null,
                    supportingText = state.area.error?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = areaMenuExpanded,
                    onDismissRequest = { areaMenuExpanded = false }
                ) {
                    Candidata.AREAS.forEach { areaOpcion ->
                        DropdownMenuItem(
                            text = { Text(areaOpcion) },
                            onClick = {
                                onAreaChange(areaOpcion)
                                areaMenuExpanded = false
                            }
                        )
                    }
                }
            }

            CampoValidado(
                campo = state.annosExp,
                onValueChange = onAnnosExpChange,
                label = "Años de experiencia (0 a 50)",
                icono = Icons.Default.Badge,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            CampoValidado(
                campo = state.resumenExp,
                onValueChange = onResumenExpChange,
                label = "Resumen de experiencia técnica",
                placeholder = "Describa proyectos relevantes, certificaciones o trayectoria...",
                esMultilinea = true,
                maxCaracteres = 500
            )

            CampoValidado(
                campo = state.linkedinUrl,
                onValueChange = onLinkedinUrlChange,
                label = "Enlace de perfil LinkedIn (opcional)",
                icono = Icons.Default.Link,
                placeholder = "https://www.linkedin.com/in/perfil"
            )

            TituloSeccion(
                titulo = "Documento Adjunto (CV PDF)",
                subtitulo = "Requisito obligatorio para evaluación"
            )

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.PictureAsPdf,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = state.infoArchivoPdf?.nombre ?: "Sin archivo PDF adjunto",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            if (state.infoArchivoPdf != null) {
                                Text(
                                    text = "PDF cargado correctamente",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = { pdfLauncher.launch("application/pdf") },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (state.infoArchivoPdf == null) "Adjuntar Currículum (PDF)" else "Reemplazar PDF")
                    }

                    state.errorCvPdf?.let { err ->
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = err,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onGuardarClick,
                enabled = !state.guardando,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .sacudir(state.activadoSacudida)
            ) {
                Icon(Icons.Default.Save, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (state.guardando) "Guardando..." else "GUARDAR POSTULACIÓN",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}