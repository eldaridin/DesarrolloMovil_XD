package com.example.app_kotlin.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "puntajes_criterio")
data class PuntajeCriterio(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val evaluacionId: Long,
    val criterioId: Long,
    val puntaje: Int,
    val observacion: String = ""
)

data class DetallePuntaje(
    val criterioId: Long,
    val nombreCriterio: String,
    val descripcionCriterio: String,
    val pesoCriterio: Int,
    val puntaje: Int,
    val observacion: String
)