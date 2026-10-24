package com.example.app_kotlin.model

enum class EstadoCandidatura(val titulo: String) {
    POSTULADA("Postulada"),
    EN_EVALUACION("En evaluación"),
    EVALUADA("Evaluada"),
    SELECCIONADA("Seleccionada"),
    NO_SELECCIONADA("No seleccionada");

    val estaResuelta: Boolean
        get() = this == SELECCIONADA || this == NO_SELECCIONADA
}