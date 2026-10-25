package com.example.app_kotlin.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.app_kotlin.model.DetallePuntaje
import com.example.app_kotlin.model.Evaluacion
import com.example.app_kotlin.model.PuntajeCriterio
import com.example.app_kotlin.model.ResultadoConsolidado
import kotlinx.coroutines.flow.Flow

@Dao
interface EvaluacionDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertarEvaluacion(evaluacion: Evaluacion): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarPuntajes(puntajes: List<PuntajeCriterio>)

    @Query("SELECT COUNT(*) FROM evaluaciones WHERE candidataId = :candidataId AND evaluadoraClave = :evaluadoraClave")
    suspend fun existeEvaluacionDeEvaluadora(candidataId: Long, evaluadoraClave: String): Int

    @Query("SELECT COUNT(*) FROM evaluaciones WHERE candidataId = :candidataId")
    suspend fun contarEvaluacionesDeCandidata(candidataId: Long): Int

    @Query("SELECT * FROM evaluaciones WHERE candidataId = :candidataId ORDER BY fecha DESC")
    fun obtenerEvaluacionesDeCandidata(candidataId: Long): Flow<List<Evaluacion>>

    @Query("""
        SELECT p.criterioId AS criterioId, c.nombre AS nombreCriterio, c.descripcion AS descripcionCriterio, 
               c.peso AS pesoCriterio, p.puntaje AS puntaje, p.observacion AS observacion
        FROM puntajes_criterio p
        INNER JOIN criterios c ON p.criterioId = c.id
        WHERE p.evaluacionId = :evaluacionId
        ORDER BY c.orden ASC
    """)
    suspend fun obtenerDetallePuntajes(evaluacionId: Long): List<DetallePuntaje>

    @Query("""
        SELECT c.id AS candidataId, c.nombre AS nombreCandidata, c.area AS area, c.estado AS estado,
               COUNT(e.id) AS cantidadEvaluaciones,
               IFNULL(AVG(e.puntajeTotal), 0.0) AS promedioPonderado
        FROM candidatas c
        LEFT JOIN evaluaciones e ON c.id = e.candidataId
        GROUP BY c.id
        ORDER BY promedioPonderado DESC, cantidadEvaluaciones DESC
    """)
    fun obtenerRankingConsolidado(): Flow<List<ResultadoConsolidado>>
}