package com.example.app_kotlin.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_kotlin.data.repository.HistorialRepository
import com.example.app_kotlin.model.RegistroHistorial
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class HistorialUiState(
    val historial: List<RegistroHistorial> = emptyList(),
    val busquedaTexto: String = "",
    val cargando: Boolean = true
)

class HistorialViewModel(
    historialRepository: HistorialRepository
) : ViewModel() {

    private val _busquedaTexto = MutableStateFlow("")

    val uiState: StateFlow<HistorialUiState> = combine(
        historialRepository.historialCompleto,
        _busquedaTexto
    ) { lista, busqueda ->
        val filtrados = if (busqueda.isBlank()) {
            lista
        } else {
            lista.filter { reg ->
                reg.usuario.contains(busqueda, ignoreCase = true) ||
                    reg.accion.contains(busqueda, ignoreCase = true) ||
                    reg.detalle.contains(busqueda, ignoreCase = true)
            }
        }
        HistorialUiState(
            historial = filtrados,
            busquedaTexto = busqueda,
            cargando = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HistorialUiState(cargando = true)
    )

    fun onBusquedaChange(nuevoTexto: String) {
        _busquedaTexto.value = nuevoTexto
    }
}