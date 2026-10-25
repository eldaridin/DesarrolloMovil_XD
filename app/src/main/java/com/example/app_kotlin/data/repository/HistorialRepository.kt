package com.example.app_kotlin.data.repository

import com.example.app_kotlin.data.local.dao.HistorialDao
import com.example.app_kotlin.model.RegistroHistorial
import kotlinx.coroutines.flow.Flow

class HistorialRepository(
    private val historialDao: HistorialDao
) {
    val historialCompleto: Flow<List<RegistroHistorial>> = historialDao.obtenerTodo()

    fun obtenerPorCandidata(candidataId: Long): Flow<List<RegistroHistorial>> {
        return historialDao.obtenerPorCandidata(candidataId)
    }

    suspend fun registrarAccion(registro: RegistroHistorial) {
        historialDao.insertar(registro)
    }
}