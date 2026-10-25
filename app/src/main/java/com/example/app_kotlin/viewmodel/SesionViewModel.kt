package com.example.app_kotlin.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_kotlin.data.repository.HistorialRepository
import com.example.app_kotlin.model.Acciones
import com.example.app_kotlin.model.RegistroHistorial
import com.example.app_kotlin.model.Rol
import com.example.app_kotlin.model.Sesion
import com.example.app_kotlin.validation.ValidationResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SesionUiState(
    val nombreUsuario: String = "",
    val rolSeleccionado: Rol = Rol.EVALUADORA,
    val errorNombre: String? = null,
    val estaAutenticado: Boolean = false,
    val mensajeErrorGeneral: String? = null
)

class SesionViewModel(
    private val historialRepository: HistorialRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SesionUiState())
    val uiState: StateFlow<SesionUiState> = _uiState.asStateFlow()

    fun onNombreChange(nuevoNombre: String) {
        _uiState.update {
            it.copy(
                nombreUsuario = nuevoNombre,
                errorNombre = if (nuevoNombre.trim().length < 3 && nuevoNombre.isNotBlank()) "El nombre debe tener al menos 3 caracteres" else null,
                mensajeErrorGeneral = null
            )
        }
    }

    fun onRolSelected(rol: Rol) {
        _uiState.update { it.copy(rolSeleccionado = rol, mensajeErrorGeneral = null) }
    }

    fun iniciarSesion(onExito: () -> Unit) {
        val nombre = _uiState.value.nombreUsuario.trim()
        if (nombre.length < 3) {
            _uiState.update { it.copy(errorNombre = "Ingrese su nombre para continuar (mín. 3 caracteres)") }
            return
        }

        val rol = _uiState.value.rolSeleccionado
        Sesion.usuarioActivo = nombre
        Sesion.rolActivo = rol

        _uiState.update {
            it.copy(
                estaAutenticado = true,
                errorNombre = null,
                mensajeErrorGeneral = null
            )
        }

        viewModelScope.launch {
            historialRepository.registrarAccion(
                RegistroHistorial(
                    usuario = nombre,
                    accion = Acciones.INICIO_SESION,
                    detalle = "Sesión iniciada con rol de ${rol.titulo}"
                )
            )
        }
        onExito()
    }

    fun cerrarSesion(onSesionCerrada: () -> Unit) {
        val usuarioAnterior = Sesion.usuarioActivo
        viewModelScope.launch {
            if (usuarioAnterior.isNotBlank()) {
                historialRepository.registrarAccion(
                    RegistroHistorial(
                        usuario = usuarioAnterior,
                        accion = Acciones.CIERRE_SESION,
                        detalle = "Sesión cerrada correctamente"
                    )
                )
            }
            Sesion.cerrar()
            _uiState.value = SesionUiState()
            onSesionCerrada()
        }
    }
}