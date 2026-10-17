package com.example.app_kotlin.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "Candidatas",
    indices = [Index( value = ["correo"], unique = true)]
)
data class Candidata(
    @PrimaryKey ( autoGenerate = true)
    val id: Long = 0,
    val nombre: String,
    val correo: String,
    val telefono: String,
    val area: String,
    val annosExp: Int,
    val resumenExp: String,
    val linkedinUrl: String,
    val cvUri: String?,
    val cvNombre: String?,
    val estado: EstadoCandidatura = EstadoCandidatura.POSTULADA,
    val fechaRegistro: Long = System.currentTimeMillis(),
    val registrador: String
){
    companion object{
        val AREAS = listOf(
            "Seguridad ofensiva (Red Team)",
            "Seguridad defensiva (Blue Team)",
            "Gobierno, riesgo y cumplimiento",
            "Ciberseguridad industrial (OT)",
            "Forense y respuesta a incidentes",
            "Investigación y academia"
        )
    }
}