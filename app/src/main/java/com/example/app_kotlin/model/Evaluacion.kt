package com.example.app_kotlin.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "evaluaciones",
    indices = [Index(value = ["candidataId", "evaluadoraClave"], unique = true)]
)
data class Evaluacion(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val candidataId: Long,
    val evaluadoraNombre: String,
    val evaluadoraClave: String,
    val observacionGeneral: String,
    val fecha: Long = System.currentTimeMillis(),
    val puntajeTotal: Double = 0.0
) {
    companion object {
        const val MINIMO_PARA_CONSOLIDAR = 2
    }
}