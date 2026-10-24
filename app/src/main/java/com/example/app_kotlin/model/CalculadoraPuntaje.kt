package com.example.app_kotlin.model

import kotlin.math.roundToInt

object CalculadoraPuntaje {

    fun calcularPuntajeEscala100(puntajesYCriterios: List<Pair<Int, Criterio>>): Double {
        if (puntajesYCriterios.isEmpty()) return 0.0
        val pesoTotal = puntajesYCriterios.sumOf { it.second.peso }
        if (pesoTotal <= 0) return 0.0

        val sumaPonderada = puntajesYCriterios.sumOf { (puntaje, criterio) ->
            puntaje * criterio.peso.toDouble()
        }
        val maximoPosible = pesoTotal * 5.0
        val resultado = (sumaPonderada / maximoPosible) * 100.0
        return (resultado * 10.0).roundToInt() / 10.0
    }

    fun obtenerCategoria(puntaje100: Double): String {
        return when {
            puntaje100 >= 85.0 -> "Sobresaliente"
            puntaje100 >= 70.0 -> "Sólida"
            puntaje100 >= 50.0 -> "En desarrollo"
            else -> "No cumple perfil"
        }
    }

    fun normalizarClaveEvaluadora(nombre: String): String {
        return nombre.trim().lowercase().replace("\\s+".toRegex(), "_")
    }
}