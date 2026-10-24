package com.example.app_kotlin.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "criterios")
data class Criterio(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nombre: String,
    val descripcion: String,
    val peso: Int,
    val orden: Int
)