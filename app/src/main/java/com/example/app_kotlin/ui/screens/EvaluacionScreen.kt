package com.example.app_kotlin.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.app_kotlin.model.Criterio
import com.example.app_kotlin.ui.components.BannerInfo
import com.example.app_kotlin.ui.components.GlassCard
import com.example.app_kotlin.ui.components.PuntajeCircular
import com.example.app_kotlin.ui.components.TituloSeccion
import com.example.app_kotlin.ui.components.sacudir
import com.example.app_kotlin.viewmodel.EvaluacionUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EvaluacionScreen(
    state: EvaluacionUiState,
    onPuntajeChange: (Long, Int) -> Unit,
    onObservacionCriterioChange: (Long, String) -> Unit,
    onObservacionGeneralChange: (String) -> Unit,
    onGuardarClick: () -> Unit,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Matriz WIM 100", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { innerPadding ->
        if (state.candidata == null) {
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
                    Row(
                        modifier = Modifier.padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "EVALUANDO POSTULANTE:",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = candidata.nombre,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = candidata.area,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        PuntajeCircular(
                            puntaje = state.puntajeTotalCalculado,
                            size = 76.dp,
                            grosor = 7.dp
                        )
                    }
                }

                BannerInfo(
                    mensaje = "Asigne una puntuación de 1 a 5 para cada criterio. Las puntuaciones 1 o 2 requieren justificación obligatoria."
                )

                state.errorGeneral?.let { err ->
                    Text(
                        text = err,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                state.criterios.forEach { criterio ->
                    ItemCriterioEvaluacion(
                        criterio = criterio,
                        puntajeActual = state.mapaPuntajes[criterio.id] ?: 3,
                        observacionActual = state.mapaObservaciones[criterio.id] ?: "",
                        errorObservacion = state.mapaErroresObservacion[criterio.id],
                        onPuntajeSelect = { p -> onPuntajeChange(criterio.id, p) },
                        onObservacionChange = { obs -> onObservacionCriterioChange(criterio.id, obs) }
                    )
                }

                TituloSeccion(
                    titulo = "Veredicto Cualitativo General",
                    subtitulo = "Mínimo 20 caracteres (obligatorio)"
                )

                OutlinedTextField(
                    value = state.observacionGeneral,
                    onValueChange = onObservacionGeneralChange,
                    placeholder = { Text("Escriba su veredicto técnico y observaciones generales del perfil...") },
                    isError = state.errorObservacionGeneral != null,
                    supportingText = state.errorObservacionGeneral?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

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
                        text = if (state.guardando) "Guardando..." else "REGISTRAR EVALUACIÓN",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun ItemCriterioEvaluacion(
    criterio: Criterio,
    puntajeActual: Int,
    observacionActual: String,
    errorObservacion: String?,
    onPuntajeSelect: (Int) -> Unit,
    onObservacionChange: (String) -> Unit
) {
    GlassCard(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${criterio.orden}. ${criterio.nombre}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "${criterio.peso}%",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = criterio.descripcion,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                (1..5).forEach { nivel ->
                    FilterChip(
                        selected = (puntajeActual == nivel),
                        onClick = { onPuntajeSelect(nivel) },
                        label = { Text("$nivel") },
                        leadingIcon = if (puntajeActual == nivel) {
                            { Icon(Icons.Default.Check, contentDescription = null) }
                        } else null
                    )
                }
            }

            AnimatedVisibility(visible = puntajeActual <= 2 || observacionActual.isNotBlank()) {
                Column {
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = observacionActual,
                        onValueChange = onObservacionChange,
                        label = { Text(if (puntajeActual <= 2) "Justificación obligatoria (puntuación baja)" else "Observación del criterio (opcional)") },
                        isError = errorObservacion != null,
                        supportingText = errorObservacion?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}