package com.example.app_kotlin.viewmodel

import com.example.app_kotlin.validation.ValidationResult

data class CampoState(
    val valor: String = "",
    val error: String? = null
) {
    val esValido: Boolean
        get() = error == null && valor.isNotBlank()

    fun con(nuevoValor: String, resultado: ValidationResult): CampoState {
        return copy(
            valor = nuevoValor,
            error = resultado.mensajeError
        )
    }
}