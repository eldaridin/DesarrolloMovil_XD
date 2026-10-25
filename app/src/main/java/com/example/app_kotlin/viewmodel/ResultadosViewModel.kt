package com.example.app_kotlin.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_kotlin.data.repository.EvaluacionRepository
import com.example.app_kotlin.model.ResultadoConsolidado
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class ResultadosUiState(
    val ranking: List<ResultadoConsolidado> = emptyList(),
    val totalCandidatas: Int = 0,
    val totalEvaluadas: Int = 0,
    val totalSobresalientes: Int = 0,
    val promedioGeneral: Double = 0.0,
    val cargando: Boolean = true
)

class ResultadosViewModel(
    evaluacionRepository: EvaluacionRepository
) : ViewModel() {

    val uiState: StateFlow<ResultadosUiState> = evaluacionRepository.rankingConsolidado.map { lista ->
        val total = lista.size
        val evaluadas = lista.count { it.cantidadEvaluaciones >= 2 }
        val sobresalientes = lista.count { it.promedioPonderado >= 85.0 }
        val promGen = if (lista.isNotEmpty()) lista.map { it.promedioPonderado }.average() else 0.0

        ResultadosUiState(
            ranking = lista,
            totalCandidatas = total,
            totalEvaluadas = evaluadas,
            totalSobresalientes = sobresalientes,
            promedioGeneral = promGen,
            cargando = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ResultadosUiState(cargando = true)
    )

    fun generarTextoRankingCompartir(): String {
        val st = uiState.value
        val sb = StringBuilder()
        sb.appendLine("🏆 Ranking Consolidado WIM 100")
        sb.appendLine("Postulantes registradas: ${st.totalCandidatas}")
        sb.appendLine("Evaluaciones consolidadas: ${st.totalEvaluadas}")
        sb.appendLine("--- Top Postulantes ---")
        st.ranking.take(5).forEachIndexed { index, res ->
            sb.appendLine("${index + 1}. ${res.nombreCandidata} (${res.area}) - ${String.format("%.1f", res.promedioPonderado)} pts - ${res.categoria}")
        }
        return sb.toString()
    }
}