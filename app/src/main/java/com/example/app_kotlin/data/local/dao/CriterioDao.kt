package com.example.app_kotlin.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.app_kotlin.model.Criterio
import kotlinx.coroutines.flow.Flow

@Dao
interface CriterioDao {
    @Query("SELECT * FROM criterios ORDER BY orden ASC")
    fun obtenerCriterios(): Flow<List<Criterio>>

    @Query("SELECT * FROM criterios ORDER BY orden ASC")
    suspend fun obtenerCriteriosLista(): List<Criterio>

    @Query("SELECT COUNT(*) FROM criterios")
    suspend fun contarCriterios(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarCriterios(criterios: List<Criterio>)
}