package com.example.app_kotlin.validation

object EvaluacionValidator {

    fun validarPuntaje(puntaje: Int): ValidationResult {
        if (puntaje !in 1..5) {
            return ValidationResult.Error("El puntaje debe estar entre 1 y 5")
        }
        return ValidationResult.Valido
    }

    fun validarObservacionCriterio(puntaje: Int, observacion: String): ValidationResult {
        val limpio = observacion.trim()
        if (puntaje <= 2) {
            if (limpio.length < 10) {
                return ValidationResult.Error("Al asignar puntaje 1 o 2, la justificación es obligatoria (mín. 10 caracteres)")
            }
        }
        return ValidationResult.Valido
    }

    fun validarObservacionGeneral(observacion: String): ValidationResult {
        val limpio = observacion.trim()
        if (limpio.length < 20) {
            return ValidationResult.Error("La observación general debe tener al menos 20 caracteres")
        }
        if (limpio.length > 400) {
            return ValidationResult.Error("La observación general no puede superar los 400 caracteres")
        }
        return ValidationResult.Valido
    }
}