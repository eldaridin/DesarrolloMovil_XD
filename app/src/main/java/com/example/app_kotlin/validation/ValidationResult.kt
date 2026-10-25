package com.example.app_kotlin.validation

sealed interface ValidationResult {
    object Valido : ValidationResult
    data class Error(val mensaje: String) : ValidationResult

    val esValido: Boolean
        get() = this is Valido

    val mensajeError: String?
        get() = (this as? Error)?.mensaje
}