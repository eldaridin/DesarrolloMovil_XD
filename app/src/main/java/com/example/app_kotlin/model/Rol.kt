package com.example.app_kotlin.model

enum class Rol(val titulo: String, val descripcion: String) {
    EVALUADORA("Evaluadora", "Registra candidaturas y evalúa según la matriz WIM"),
    DIRECCION("Dirección", "Consulta resultados globales, ranking y resuelve postulaciones")
}

object Sesion {
    var usuarioActivo: String = ""
    var rolActivo: Rol = Rol.EVALUADORA

    val estaAutenticado: Boolean
        get() = usuarioActivo.isNotBlank()

    fun cerrar() {
        usuarioActivo = ""
        rolActivo = Rol.EVALUADORA
    }
}