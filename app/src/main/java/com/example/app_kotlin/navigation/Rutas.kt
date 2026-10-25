package com.example.app_kotlin.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

object Rutas {
    const val LOGIN = "login"
    const val CANDIDATAS = "candidatas"
    const val RESULTADOS = "resultados"
    const val HISTORIAL = "historial"
    const val PERFIL = "perfil"
    const val NUEVA_CANDIDATA = "nueva-candidata"
    const val DETALLE_CANDIDATA = "candidata/{candidataId}"
    const val EVALUAR = "evaluar/{candidataId}"

    fun detalleCandidata(id: Long) = "candidata/$id"
    fun evaluar(id: Long) = "evaluar/$id"
}

enum class DestinoPrincipal(
    val ruta: String,
    val titulo: String,
    val icono: ImageVector
) {
    CANDIDATAS(Rutas.CANDIDATAS, "Candidatas", Icons.Default.People),
    RESULTADOS(Rutas.RESULTADOS, "Resultados", Icons.Default.Assessment),
    HISTORIAL(Rutas.HISTORIAL, "Historial", Icons.Default.History),
    PERFIL(Rutas.PERFIL, "Perfil", Icons.Default.Person)
}