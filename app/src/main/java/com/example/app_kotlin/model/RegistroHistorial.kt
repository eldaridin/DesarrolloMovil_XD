package com.example.app_kotlin.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "historial")
data class RegistroHistorial(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val usuario: String,
    val accion: String,
    val detalle: String,
    val candidataId: Long? = null
)

object Acciones {
    const val INICIO_SESION = "Inicio de sesión"
    const val CIERRE_SESION = "Cierre de sesión"
    const val REGISTRO_CANDIDATA = "Registro de candidata"
    const val EVALUACION_REGISTRADA = "Evaluación registrada"
    const val EVALUACION_DUPLICADA_INTENTO = "Intento de evaluación duplicada"
    const val CANDIDATURA_RESUELTA = "Candidatura resuelta"
}