package com.example.app_kotlin.data.local

import androidx.room.TypeConverter
import com.example.app_kotlin.model.EstadoCandidatura

class Converters {
    @TypeConverter
    fun fromEstado(estado: EstadoCandidatura): String = estado.name

    @TypeConverter
    fun toEstado(value: String): EstadoCandidatura {
        return try {
            EstadoCandidatura.valueOf(value)
        } catch (e: Exception) {
            EstadoCandidatura.POSTULADA
        }
    }
}