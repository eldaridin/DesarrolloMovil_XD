package com.example.app_kotlin.model

enum class EstadoCandidatura( val etiqueta: String ){
    POSTULADA("Postulada"),
    EN_EVALUACION("En evaluacion"),
    EVALUADA("Evaluada"),
    SELECCIONADA("Seleccionada"),
    NO_SELECCIONADA("No seleccionada");

    val Resuelto: Boolean
        get() = this == SELECCIONADA || this == NO_SELECCIONADA

}