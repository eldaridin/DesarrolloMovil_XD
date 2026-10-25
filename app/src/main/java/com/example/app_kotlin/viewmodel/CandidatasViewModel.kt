package com.example.app_kotlin.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_kotlin.data.repository.CandidataRepository
import com.example.app_kotlin.model.Candidata
import com.example.app_kotlin.model.EstadoCandidatura
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class CandidatasUiState(
    val candidatas: List<Candidata> = emptyList(),
    val busquedaTexto: String = "",
    val filtroEstado: EstadoCandidatura? = null,
    val cargando: Boolean = false
)

class CandidatasViewModel(
    candidataRepository: CandidataRepository
) : ViewModel() {

    private val _busquedaTexto = MutableStateFlow("")
    private val _filtroEstado = MutableStateFlow<EstadoCandidatura?>(null)

    val uiState: StateFlow<CandidatasUiState> = combine(
        candidataRepository.todasCandidatas,
        _busquedaTexto,
        _filtroEstado
    ) { lista, busqueda, estado ->
        val filtradas = lista.filter { candidata ->
            val coincideTexto = busqueda.isBlank() ||
                candidata.nombre.contains(busqueda, ignoreCase = true) ||
                candidata.area.contains(busqueda, ignoreCase = true) ||
                candidata.correo.contains(busqueda, ignoreCase = true)

            val coincideEstado = estado == null || candidata.estado == estado

            coincideTexto && coincideEstado
        }

        CandidatasUiState(
            candidatas = filtradas,
            busquedaTexto = busqueda,
            filtroEstado = estado,
            cargando = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CandidatasUiState(cargando = true)
    )

    fun onBusquedaChange(nuevoTexto: String) {
        _busquedaTexto.value = nuevoTexto
    }

    fun onFiltroEstadoChange(nuevoEstado: EstadoCandidatura?) {
        _filtroEstado.value = nuevoEstado
    }
}