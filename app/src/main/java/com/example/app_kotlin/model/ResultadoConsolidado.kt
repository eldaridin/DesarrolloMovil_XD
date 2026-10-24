package com.example.app_kotlin.model

data class ResultadoConsolidado(
    val candidataId: Long,
    val nombreCandidata: String,
    val area: String,
    val estado: EstadoCandidatura,
    val cantidadEvaluaciones: Int,
    val promedioPonderado: Double
) {
    val categoria: String
        get() = CalculadoraPuntaje.obtenerCategoria(promedioPonderado)
}