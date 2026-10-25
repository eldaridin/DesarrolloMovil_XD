package com.example.app_kotlin.data.repository

import com.example.app_kotlin.data.local.dao.CandidataDao
import com.example.app_kotlin.data.local.dao.HistorialDao
import com.example.app_kotlin.model.Acciones
import com.example.app_kotlin.model.Candidata
import com.example.app_kotlin.model.EstadoCandidatura
import com.example.app_kotlin.model.RegistroHistorial
import kotlinx.coroutines.flow.Flow

class CandidataRepository(
    private val candidataDao: CandidataDao,
    private val historialDao: HistorialDao
) {
    val todasCandidatas: Flow<List<Candidata>> = candidataDao.obtenerTodas()

    fun obtenerPorIdFlow(id: Long): Flow<Candidata?> = candidataDao.obtenerPorIdFlow(id)

    suspend fun obtenerPorId(id: Long): Candidata? = candidataDao.obtenerPorId(id)

    suspend fun existeCorreo(correo: String): Boolean {
        return candidataDao.contarPorCorreo(correo.trim().lowercase()) > 0
    }

    suspend fun registrarCandidata(candidata: Candidata): Long {
        val id = candidataDao.insertar(candidata)
        historialDao.insertar(
            RegistroHistorial(
                usuario = candidata.registrador,
                accion = Acciones.REGISTRO_CANDIDATA,
                detalle = "Candidata ${candidata.nombre} registrada exitosamente en área ${candidata.area}",
                candidataId = id
            )
        )
        return id
    }

    suspend fun resolverCandidatura(candidataId: Long, nuevoEstado: EstadoCandidatura, usuarioDireccion: String) {
        val candidata = candidataDao.obtenerPorId(candidataId) ?: return
        candidataDao.actualizarEstado(candidataId, nuevoEstado)
        historialDao.insertar(
            RegistroHistorial(
                usuario = usuarioDireccion,
                accion = Acciones.CANDIDATURA_RESUELTA,
                detalle = "Postulación de ${candidata.nombre} resuelta como: ${nuevoEstado.titulo}",
                candidataId = candidataId
            )
        )
    }
}