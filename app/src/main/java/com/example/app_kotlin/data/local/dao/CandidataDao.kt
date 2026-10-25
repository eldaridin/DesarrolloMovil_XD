package com.example.app_kotlin.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.app_kotlin.model.Candidata
import com.example.app_kotlin.model.EstadoCandidatura
import kotlinx.coroutines.flow.Flow

@Dao
interface CandidataDao {
    @Query("SELECT * FROM candidatas ORDER BY fechaRegistro DESC")
    fun obtenerTodas(): Flow<List<Candidata>>

    @Query("SELECT * FROM candidatas WHERE id = :id LIMIT 1")
    suspend fun obtenerPorId(id: Long): Candidata?

    @Query("SELECT * FROM candidatas WHERE id = :id LIMIT 1")
    fun obtenerPorIdFlow(id: Long): Flow<Candidata?>

    @Query("SELECT COUNT(*) FROM candidatas WHERE correo = :correo")
    suspend fun contarPorCorreo(correo: String): Int

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertar(candidata: Candidata): Long

    @Update
    suspend fun actualizar(candidata: Candidata)

    @Query("UPDATE candidatas SET estado = :nuevoEstado WHERE id = :id")
    suspend fun actualizarEstado(id: Long, nuevoEstado: EstadoCandidatura)

    @Query("SELECT COUNT(*) FROM candidatas")
    suspend fun contarCandidatas(): Int
}