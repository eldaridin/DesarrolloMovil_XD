package com.example.app_kotlin.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.app_kotlin.model.ResultadoConsolidado
import com.example.app_kotlin.nativo.IntentsHelper
import com.example.app_kotlin.ui.components.EstadoChip
import com.example.app_kotlin.ui.components.EstadoVacio
import com.example.app_kotlin.ui.components.GlassCard
import com.example.app_kotlin.ui.components.TituloSeccion
import com.example.app_kotlin.viewmodel.ResultadosUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultadosScreen(
    state: ResultadosUiState,
    onCandidataClick: (Long) -> Unit
) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Resultados y Ranking WIM", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = {
                        val sb = StringBuilder()
                        sb.appendLine("🏆 Ranking WIM 100")
                        sb.appendLine("Total postulantes: ${state.totalCandidatas}")
                        sb.appendLine("Evaluadas consolidadas: ${state.totalEvaluadas}")
                        state.ranking.take(5).forEachIndexed { i, r ->
                            sb.appendLine("${i + 1}. ${r.nombreCandidata} - ${r.promedioPonderado.toInt()} pts (${r.categoria})")
                        }
                        IntentsHelper.compartirTexto(context, "Ranking Consolidado WIM 100", sb.toString())
                    }) {
                        Icon(Icons.Default.Share, contentDescription = "Compartir ranking")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { innerPadding ->
        if (state.cargando) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
            ) {
                GlassCard(
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Métricas del Programa WIM 100",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            MetricBox("Postulantes", "${state.totalCandidatas}")
                            MetricBox("Consolidadas", "${state.totalEvaluadas}")
                            MetricBox("Sobresalientes", "${state.totalSobresalientes}")
                            MetricBox("Promedio", "${state.promedioGeneral.toInt()}")
                        }
                    }
                }

                TituloSeccion(
                    titulo = "Tabla Posicional del Ranking",
                    subtitulo = "Ordenado por promedio ponderado de evaluación"
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (state.ranking.isEmpty()) {
                    EstadoVacio(
                        mensaje = "Sin datos consolidados",
                        subtitulo = "Registre postulantes y evaluaciones para visualizar el ranking.",
                        icono = Icons.Default.Leaderboard
                    )
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        itemsIndexed(state.ranking) { index, resultado ->
                            RankingRowCard(
                                posicion = index + 1,
                                resultado = resultado,
                                onClick = { onCandidataClick(resultado.candidataId) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MetricBox(titulo: String, valor: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = valor,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = titulo,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun RankingRowCard(
    posicion: Int,
    resultado: ResultadoConsolidado,
    onClick: () -> Unit
) {
    GlassCard(
        shape = RoundedCornerShape(16.dp),
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val (colorMedalla, colorTextoMedalla) = when (posicion) {
                1 -> Color(0xFFFFD700) to Color(0xFF5A4300) // Oro
                2 -> Color(0xFFC0C0C0) to Color(0xFF333333) // Plata
                3 -> Color(0xFFCD7F32) to Color(0xFF3D1F00) // Bronce
                else -> MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
            }

            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(colorMedalla),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$posicion",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = colorTextoMedalla
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = resultado.nombreCandidata,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${resultado.area} • ${resultado.cantidadEvaluaciones} evaluaci${if (resultado.cantidadEvaluaciones == 1) "ón" else "ones"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(6.dp))

                LinearProgressIndicator(
                    progress = { (resultado.promedioPonderado / 100.0).toFloat().coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(CircleShape),
                    color = if (resultado.promedioPonderado >= 85.0) {
                        com.example.app_kotlin.ui.theme.SuccessEmerald
                    } else MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${resultado.promedioPonderado.toInt()} pts",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                EstadoChip(estado = resultado.estado)
            }

            Spacer(modifier = Modifier.width(4.dp))

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}