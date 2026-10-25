package com.example.app_kotlin.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.app_kotlin.model.CalculadoraPuntaje
import com.example.app_kotlin.model.EstadoCandidatura
import com.example.app_kotlin.ui.theme.DangerRose
import com.example.app_kotlin.ui.theme.NeonBlue
import com.example.app_kotlin.ui.theme.NeonCyan
import com.example.app_kotlin.ui.theme.NeonPurple
import com.example.app_kotlin.ui.theme.ObsidianCardBorder
import com.example.app_kotlin.ui.theme.SuccessEmerald
import com.example.app_kotlin.ui.theme.WarningAmber

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    shape: Shape = RoundedCornerShape(18.dp),
    content: @Composable () -> Unit
) {
    val clickableModifier = if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier

    Surface(
        modifier = modifier
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.45f),
                        ObsidianCardBorder.copy(alpha = 0.35f)
                    )
                ),
                shape = shape
            )
            .then(clickableModifier),
        shape = shape,
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
        tonalElevation = 6.dp,
        shadowElevation = 8.dp
    ) {
        content()
    }
}

@Composable
fun AvatarIniciales(
    nombre: String,
    modifier: Modifier = Modifier,
    size: Dp = 52.dp
) {
    val iniciales = nombre.trim().split(" ")
        .mapNotNull { it.firstOrNull()?.uppercase() }
        .take(2)
        .joinToString("")
        .ifEmpty { "W" }

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(
                Brush.sweepGradient(
                    colors = listOf(
                        NeonCyan,
                        NeonPurple,
                        NeonBlue,
                        NeonCyan
                    )
                )
            )
            .padding(2.5.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surface),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = iniciales,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Composable
fun EstadoChip(
    estado: EstadoCandidatura,
    modifier: Modifier = Modifier
) {
    val (colorFondo, colorTexto, colorPunto) = when (estado) {
        EstadoCandidatura.POSTULADA -> Triple(Color(0xFF1E293B), Color(0xFFCBD5E1), Color(0xFF94A3B8))
        EstadoCandidatura.EN_EVALUACION -> Triple(Color(0xFF3B2503), WarningAmber, WarningAmber)
        EstadoCandidatura.EVALUADA -> Triple(Color(0xFF1E1035), Color(0xFFD8B4FE), NeonPurple)
        EstadoCandidatura.SELECCIONADA -> Triple(Color(0xFF022C1A), SuccessEmerald, SuccessEmerald)
        EstadoCandidatura.NO_SELECCIONADA -> Triple(Color(0xFF3B0A11), DangerRose, DangerRose)
    }

    val colorAnimado by animateColorAsState(targetValue = colorFondo, label = "chipColor")

    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(colorAnimado)
            .border(1.dp, colorPunto.copy(alpha = 0.5f), CircleShape)
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(colorPunto)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = estado.titulo,
                color = colorTexto,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun PuntajeCircular(
    puntaje: Double,
    maximo: Double = 100.0,
    size: Dp = 90.dp,
    grosor: Dp = 8.dp,
    mostrarCategoria: Boolean = false
) {
    val porcentaje = (puntaje / maximo).coerceIn(0.0, 1.0).toFloat()
    val progresoAnimado by animateFloatAsState(
        targetValue = porcentaje,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "progresoPuntaje"
    )

    val colorBarra = when {
        puntaje >= 85.0 -> SuccessEmerald
        puntaje >= 70.0 -> NeonCyan
        puntaje >= 50.0 -> WarningAmber
        else -> DangerRose
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier.size(size),
            contentAlignment = Alignment.Center
        ) {
            val colorFondo = MaterialTheme.colorScheme.surfaceVariant
            Canvas(modifier = Modifier.size(size)) {
                drawCircle(
                    color = colorFondo,
                    style = Stroke(width = grosor.toPx())
                )
                drawArc(
                    brush = Brush.sweepGradient(
                        colors = listOf(colorBarra.copy(alpha = 0.6f), colorBarra)
                    ),
                    startAngle = -90f,
                    sweepAngle = 360f * progresoAnimado,
                    useCenter = false,
                    style = Stroke(width = grosor.toPx(), cap = StrokeCap.Round)
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${puntaje.toInt()}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "pts",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (mostrarCategoria) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = CalculadoraPuntaje.obtenerCategoria(puntaje),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = colorBarra
            )
        }
    }
}

@Composable
fun TituloSeccion(
    titulo: String,
    modifier: Modifier = Modifier,
    subtitulo: String? = null
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(20.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(NeonCyan, NeonPurple)
                        )
                    )
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = titulo,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        subtitulo?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 14.dp, top = 2.dp)
            )
        }
    }
}

@Composable
fun BannerInfo(
    mensaje: String,
    icono: ImageVector = Icons.Default.Info,
    modifier: Modifier = Modifier
) {
    GlassCard(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icono,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = mensaje,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun EstadoVacio(
    mensaje: String,
    subtitulo: String? = null,
    icono: ImageVector = Icons.Default.Inbox
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                modifier = Modifier.size(36.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = mensaje,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold
        )
        subtitulo?.let {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = it,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

fun Modifier.sacudir(activado: Boolean): Modifier = composed {
    val offsetAnimado = remember { Animatable(0f) }

    LaunchedEffect(activado) {
        if (activado) {
            val sec = listOf(-16f, 16f, -12f, 12f, -8f, 8f, -4f, 4f, 0f)
            for (value in sec) {
                offsetAnimado.animateTo(
                    targetValue = value,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessHigh
                    )
                )
            }
        }
    }

    this.graphicsLayer {
        translationX = offsetAnimado.value
    }
}