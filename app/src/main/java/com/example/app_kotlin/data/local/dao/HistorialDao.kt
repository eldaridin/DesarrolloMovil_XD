package com.example.app_kotlin.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.app_kotlin.model.RegistroHistorial
import kotlinx.coroutines.flow.Flow

@Dao
interface HistorialDao {
    @Insert
    suspend fun insertar(registro: RegistroHistorial): Long

    @Query("SELECT * FROM historial ORDER BY timestamp DESC")
    fun obtenerTodo(): Flow<List<RegistroHistorial>>

    @Query("SELECT * FROM historial WHERE candidataId = :candidataId ORDER BY timestamp DESC")
    fun obtenerPorCandidata(candidataId: Long): Flow<List<RegistroHistorial>>
}