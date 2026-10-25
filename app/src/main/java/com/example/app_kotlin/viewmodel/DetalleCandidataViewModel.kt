package com.example.app_kotlin.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_kotlin.data.repository.CandidataRepository
import com.example.app_kotlin.data.repository.EvaluacionRepository
import com.example.app_kotlin.data.repository.HistorialRepository
import com.example.app_kotlin.model.Candidata
import com.example.app_kotlin.model.DetallePuntaje
import com.example.app_kotlin.model.EstadoCandidatura
import com.example.app_kotlin.model.Evaluacion
import com.example.app_kotlin.model.RegistroHistorial
import com.example.app_kotlin.model.Rol
import com.example.app_kotlin.model.Sesion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EvaluacionConDetalle(
    val evaluacion: Evaluacion,
    val detalles: List<DetallePuntaje>
)

data class DetalleCandidataUiState(
    val candidata: Candidata? = null,
    val evaluaciones: List<EvaluacionConDetalle> = emptyList(),
    val historial: List<RegistroHistorial> = emptyList(),
    val cargando: Boolean = true,
    val mensajeExitoRespuesta: String? = null,
    val errorGeneral: String? = null
)

class DetalleCandidataViewModel(
    val candidataId: Long,
    private val candidataRepository: CandidataRepository,
    private val evaluacionRepository: EvaluacionRepository,
    private val historialRepository: HistorialRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetalleCandidataUiState())
    val uiState: StateFlow<DetalleCandidataUiState> = _uiState.asStateFlow()

    init {
        cargarDetalles()
    }

    private fun cargarDetalles() {
        viewModelScope.launch {
            combine(
                candidataRepository.obtenerPorIdFlow(candidataId),
                evaluacionRepository.obtenerEvaluacionesDeCandidata(candidataId),
                historialRepository.obtenerPorCandidata(candidataId)
            ) { candidata, listaEvaluaciones, listaHistorial ->
                val evConDetalles = listaEvaluaciones.map { ev ->
                    val detalles = evaluacionRepository.obtenerDetallePuntajes(ev.id)
                    EvaluacionConDetalle(ev, detalles)
                }

                DetalleCandidataUiState(
                    candidata = candidata,
                    evaluaciones = evConDetalles,
                    historial = listaHistorial,
                    cargando = false
                )
            }.collect { estado ->
                _uiState.value = estado
            }
        }
    }

    fun resolverCandidatura(nuevoEstado: EstadoCandidatura) {
        if (!Sesion.estaAutenticado || Sesion.rolActivo != Rol.DIRECCION) return

        viewModelScope.launch {
            try {
                candidataRepository.resolverCandidatura(
                    candidataId = candidataId,
                    nuevoEstado = nuevoEstado,
                    usuarioDireccion = Sesion.usuarioActivo
                )
                _uiState.update {
                    it.copy(mensajeExitoRespuesta = "Postulación marcada como: ${nuevoEstado.titulo}")
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorGeneral = "Error al resolver la postulación: ${e.localizedMessage}") }
            }
        }
    }

    fun generarTextoCompartir(): String {
        val c = _uiState.value.candidata ?: return ""
        val evs = _uiState.value.evaluaciones
        val sb = StringBuilder()
        sb.appendLine("📌 Expediente WIM 100")
        sb.appendLine("Candidata: ${c.nombre}")
        sb.appendLine("Área: ${c.area}")
        sb.appendLine("Experiencia: ${c.annosExp} años")
        sb.appendLine("Estado actual: ${c.estado.titulo}")
        if (evs.isNotEmpty()) {
            val prom = evs.map { it.evaluacion.puntajeTotal }.average()
            sb.appendLine("Evaluaciones realizadas: ${evs.size}")
            sb.appendLine("Promedio ponderado: ${String.format("%.1f", prom)}/100")
        }
        return sb.toString()
    }
}