package com.example.app_kotlin.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.unit.dp
import com.example.app_kotlin.model.EstadoCandidatura
import com.example.app_kotlin.model.Rol
import com.example.app_kotlin.model.Sesion
import com.example.app_kotlin.nativo.IntentsHelper
import com.example.app_kotlin.ui.components.AvatarIniciales
import com.example.app_kotlin.ui.components.EstadoChip
import com.example.app_kotlin.ui.components.GlassCard
import com.example.app_kotlin.ui.components.PuntajeCircular
import com.example.app_kotlin.ui.components.TituloSeccion
import com.example.app_kotlin.ui.components.VisorCvDialog
import com.example.app_kotlin.viewmodel.DetalleCandidataUiState
import com.example.app_kotlin.viewmodel.EvaluacionConDetalle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleCandidataScreen(
    state: DetalleCandidataUiState,
    onEvaluarClick: () -> Unit,
    onResolverClick: (EstadoCandidatura) -> Unit,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    var mostrarVisorPdf by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Expediente de Candidata", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        state.candidata?.let { c ->
                            IntentsHelper.compartirTexto(
                                context,
                                "Expediente WIM 100 - ${c.nombre}",
                                "Candidata: ${c.nombre}\nÁrea: ${c.area}\nEstado: ${c.estado.titulo}"
                            )
                        }
                    }) {
                        Icon(Icons.Default.Share, contentDescription = "Compartir expediente")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { innerPadding ->
        if (state.cargando || state.candidata == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            val candidata = state.candidata

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            AvatarIniciales(nombre = candidata.nombre, size = 60.dp)
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = candidata.nombre,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Text(
                                    text = candidata.area,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                EstadoChip(estado = candidata.estado)
                            }
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

                        Text("📧 Correo: ${candidata.correo}", style = MaterialTheme.typography.bodyMedium)
                        Text("📞 Teléfono: ${candidata.telefono}", style = MaterialTheme.typography.bodyMedium)
                        Text("💼 Experiencia: ${candidata.annosExp} años", style = MaterialTheme.typography.bodyMedium)
                        Text("👤 Registrado por: ${candidata.registrador}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

                        if (candidata.linkedinUrl.isNotBlank()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedButton(
                                onClick = { IntentsHelper.abrirEnlaceWeb(context, candidata.linkedinUrl) },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Ver Perfil en LinkedIn")
                            }
                        }

                        if (!candidata.cvUri.isNull_or_blank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = { mostrarVisorPdf = true },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.PictureAsPdf, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Visualizar CV en PDF")
                            }
                        }
                    }
                }

                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        TituloSeccion(titulo = "Resumen de Trayectoria")
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = candidata.resumenExp,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                TituloSeccion(
                    titulo = "Evaluaciones de la Matriz (${state.evaluaciones.size})",
                    subtitulo = "Evaluaciones parciales de las jurados asignadas"
                )

                if (state.evaluaciones.isEmpty()) {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Aún no se han registrado evaluaciones para esta postulante.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                } else {
                    state.evaluaciones.forEach { evConDetalle ->
                        EvaluacionExpandibleCard(evConDetalle = evConDetalle)
                    }
                }

                if (!candidata.estado.estaResuelta) {
                    if (Sesion.rolActivo == Rol.EVALUADORA) {
                        Button(
                            onClick = onEvaluarClick,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                        ) {
                            Icon(Icons.Default.RateReview, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("INGRESAR EVALUACIÓN", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        }
                    } else if (Sesion.rolActivo == Rol.DIRECCION) {
                        GlassCard(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                TituloSeccion(
                                    titulo = "Resolución de Dirección",
                                    subtitulo = "Seleccione el veredicto final para esta postulación WIM 100"
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Button(
                                        onClick = { onResolverClick(EstadoCandidatura.SELECCIONADA) },
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = com.example.app_kotlin.ui.theme.SuccessEmerald
                                        ),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Seleccionar", fontWeight = FontWeight.Bold)
                                    }

                                    Button(
                                        onClick = { onResolverClick(EstadoCandidatura.NO_SELECCIONADA) },
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = com.example.app_kotlin.ui.theme.DangerRose
                                        ),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(Icons.Default.Cancel, contentDescription = null)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Rechazar", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (mostrarVisorPdf && state.candidata?.cvUri != null) {
        VisorCvDialog(
            pathArchivoPdf = state.candidata.cvUri,
            nombreArchivo = state.candidata.cvNombre ?: "CV_Candidata.pdf",
            onDismiss = { mostrarVisorPdf = false }
        )
    }
}

@Composable
fun EvaluacionExpandibleCard(evConDetalle: EvaluacionConDetalle) {
    var expandido by remember { mutableStateOf(false) }

    GlassCard(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                PuntajeCircular(
                    puntaje = evConDetalle.evaluacion.puntajeTotal,
                    size = 64.dp,
                    grosor = 6.dp
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = evConDetalle.evaluacion.evaluadoraNombre,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Veredicto:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = evConDetalle.evaluacion.observacionGeneral,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                IconButton(onClick = { expandido = !expandido }) {
                    Icon(
                        imageVector = if (expandido) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = "Expandir detalle"
                    )
                }
            }

            if (expandido) {
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                Text(
                    text = "Detalle de puntuación:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(8.dp))

                evConDetalle.detalles.forEach { d ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = d.nombreCriterio,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "${d.puntaje}/5",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    if (d.observacion.isNotBlank()) {
                        Text(
                            text = "└ Justificación: ${d.observacion}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

private fun String?.isNull_or_blank(): Boolean = this == null || this.trim().isEmpty()