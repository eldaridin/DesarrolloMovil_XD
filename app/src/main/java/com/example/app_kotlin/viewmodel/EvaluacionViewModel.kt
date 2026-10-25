package com.example.app_kotlin.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_kotlin.data.repository.CandidataRepository
import com.example.app_kotlin.data.repository.EvaluacionDuplicadaException
import com.example.app_kotlin.data.repository.EvaluacionRepository
import com.example.app_kotlin.model.CalculadoraPuntaje
import com.example.app_kotlin.model.Candidata
import com.example.app_kotlin.model.Criterio
import com.example.app_kotlin.model.Sesion
import com.example.app_kotlin.nativo.Notificador
import com.example.app_kotlin.validation.EvaluacionValidator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EvaluacionUiState(
    val candidata: Candidata? = null,
    val criterios: List<Criterio> = emptyList(),
    val mapaPuntajes: Map<Long, Int> = emptyMap(),
    val mapaObservaciones: Map<Long, String> = emptyMap(),
    val mapaErroresObservacion: Map<Long, String> = emptyMap(),
    val observacionGeneral: String = "",
    val errorObservacionGeneral: String? = null,
    val puntajeTotalCalculado: Double = 0.0,
    val guardando: Boolean = false,
    val exitoGuardado: Boolean = false,
    val activadoSacudida: Boolean = false,
    val errorGeneral: String? = null
)

class EvaluacionViewModel(
    val candidataId: Long,
    private val candidataRepository: CandidataRepository,
    private val evaluacionRepository: EvaluacionRepository,
    private val notificador: Notificador
) : ViewModel() {

    private val _uiState = MutableStateFlow(EvaluacionUiState())
    val uiState: StateFlow<EvaluacionUiState> = _uiState.asStateFlow()

    init {
        cargarDatos()
    }

    private fun cargarDatos() {
        viewModelScope.launch {
            val candidata = candidataRepository.obtenerPorId(candidataId)
            evaluacionRepository.criterios.collect { listaCriterios ->
                val inicialPuntajes = listaCriterios.associate { it.id to 3 }
                val puntajeTotal = CalculadoraPuntaje.calcularPuntajeEscala100(
                    listaCriterios.map { c -> Pair(3, c) }
                )
                _uiState.update {
                    it.copy(
                        candidata = candidata,
                        criterios = listaCriterios,
                        mapaPuntajes = inicialPuntajes,
                        puntajeTotalCalculado = puntajeTotal
                    )
                }
            }
        }
    }

    fun onPuntajeChange(criterioId: Long, nuevoPuntaje: Int) {
        _uiState.update { actual ->
            val nuevoMapaPuntajes = actual.mapaPuntajes.toMutableMap().apply { put(criterioId, nuevoPuntaje) }
            val pares = actual.criterios.map { c ->
                Pair(nuevoMapaPuntajes[c.id] ?: 3, c)
            }
            val nuevoTotal = CalculadoraPuntaje.calcularPuntajeEscala100(pares)

            actual.copy(
                mapaPuntajes = nuevoMapaPuntajes,
                puntajeTotalCalculado = nuevoTotal
            )
        }
    }

    fun onObservacionCriterioChange(criterioId: Long, nuevaObservacion: String) {
        _uiState.update { actual ->
            val nuevoMapaObs = actual.mapaObservaciones.toMutableMap().apply { put(criterioId, nuevaObservacion) }
            val puntaje = actual.mapaPuntajes[criterioId] ?: 3
            val res = EvaluacionValidator.validarObservacionCriterio(puntaje, nuevaObservacion)
            val nuevoMapaErrores = actual.mapaErroresObservacion.toMutableMap().apply {
                if (res.esValido) remove(criterioId) else put(criterioId, res.mensajeError ?: "")
            }
            actual.copy(
                mapaObservaciones = nuevoMapaObs,
                mapaErroresObservacion = nuevoMapaErrores
            )
        }
    }

    fun onObservacionGeneralChange(nuevaObservacion: String) {
        val res = EvaluacionValidator.validarObservacionGeneral(nuevaObservacion)
        _uiState.update {
            it.copy(
                observacionGeneral = nuevaObservacion,
                errorObservacionGeneral = res.mensajeError
            )
        }
    }

    fun guardarEvaluacion(onExito: () -> Unit) {
        val st = _uiState.value
        val evaluadoraNombre = if (Sesion.usuarioActivo.isNotBlank()) Sesion.usuarioActivo else "Evaluadora"

        // Validar observaciones
        val mapaErrores = mutableMapOf<Long, String>()
        for (criterio in st.criterios) {
            val puntaje = st.mapaPuntajes[criterio.id] ?: 3
            val obs = st.mapaObservaciones[criterio.id] ?: ""
            val resObs = EvaluacionValidator.validarObservacionCriterio(puntaje, obs)
            if (!resObs.esValido) {
                mapaErrores[criterio.id] = resObs.mensajeError ?: "Justificación requerida"
            }
        }

        val resGeneral = EvaluacionValidator.validarObservacionGeneral(st.observacionGeneral)

        if (mapaErrores.isNotEmpty() || !resGeneral.esValido) {
            _uiState.update {
                it.copy(
                    mapaErroresObservacion = mapaErrores,
                    errorObservacionGeneral = resGeneral.mensajeError,
                    activadoSacudida = !it.activadoSacudida
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(guardando = true, errorGeneral = null) }
            try {
                evaluacionRepository.registrarEvaluacion(
                    candidataId = candidataId,
                    evaluadoraNombre = evaluadoraNombre,
                    observacionGeneral = st.observacionGeneral.trim(),
                    mapaPuntajes = st.mapaPuntajes,
                    mapaObservaciones = st.mapaObservaciones
                )

                // Notificación local nativa
                st.candidata?.let { candidata ->
                    notificador.notificarEvaluacionRegistrada(candidata.nombre, st.puntajeTotalCalculado)
                }

                _uiState.update { it.copy(guardando = false, exitoGuardado = true) }
                onExito()
            } catch (e: EvaluacionDuplicadaException) {
                _uiState.update {
                    it.copy(
                        guardando = false,
                        errorGeneral = e.message,
                        activadoSacudida = !it.activadoSacudida
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        guardando = false,
                        errorGeneral = "Error al guardar la evaluación: ${e.localizedMessage}",
                        activadoSacudida = !it.activadoSacudida
                    )
                }
            }
        }
    }
}