package com.example.app_kotlin.data.repository

import androidx.room.withTransaction
import com.example.app_kotlin.data.local.WimDatabase
import com.example.app_kotlin.model.Acciones
import com.example.app_kotlin.model.CalculadoraPuntaje
import com.example.app_kotlin.model.Criterio
import com.example.app_kotlin.model.DetallePuntaje
import com.example.app_kotlin.model.EstadoCandidatura
import com.example.app_kotlin.model.Evaluacion
import com.example.app_kotlin.model.PuntajeCriterio
import com.example.app_kotlin.model.RegistroHistorial
import com.example.app_kotlin.model.ResultadoConsolidado
import kotlinx.coroutines.flow.Flow

class EvaluacionDuplicadaException(mensaje: String) : Exception(mensaje)

class EvaluacionRepository(
    private val database: WimDatabase
) {
    private val candidataDao = database.candidataDao()
    private val criterioDao = database.criterioDao()
    private val evaluacionDao = database.evaluacionDao()
    private val historialDao = database.historialDao()

    val criterios: Flow<List<Criterio>> = criterioDao.obtenerCriterios()
    val rankingConsolidado: Flow<List<ResultadoConsolidado>> = evaluacionDao.obtenerRankingConsolidado()

    fun obtenerEvaluacionesDeCandidata(candidataId: Long): Flow<List<Evaluacion>> {
        return evaluacionDao.obtenerEvaluacionesDeCandidata(candidataId)
    }

    suspend fun obtenerDetallePuntajes(evaluacionId: Long): List<DetallePuntaje> {
        return evaluacionDao.obtenerDetallePuntajes(evaluacionId)
    }

    suspend fun registrarEvaluacion(
        candidataId: Long,
        evaluadoraNombre: String,
        observacionGeneral: String,
        mapaPuntajes: Map<Long, Int>,
        mapaObservaciones: Map<Long, String>
    ) {
        val evaluadoraClave = CalculadoraPuntaje.normalizarClaveEvaluadora(evaluadoraNombre)

        database.withTransaction {
            val candidata = candidataDao.obtenerPorId(candidataId)
                ?: throw IllegalArgumentException("La candidata no existe")

            if (candidata.estado.estaResuelta) {
                throw IllegalStateException("La candidatura ya fue resuelta por Dirección")
            }

            if (evaluacionDao.existeEvaluacionDeEvaluadora(candidataId, evaluadoraClave) > 0) {
                historialDao.insertar(
                    RegistroHistorial(
                        usuario = evaluadoraNombre,
                        accion = Acciones.EVALUACION_DUPLICADA_INTENTO,
                        detalle = "Intento de re-evaluar a la candidata ${candidata.nombre}",
                        candidataId = candidataId
                    )
                )
                throw EvaluacionDuplicadaException("Ya has registrado una evaluación para la candidata ${candidata.nombre}")
            }

            val criteriosLista = criterioDao.obtenerCriteriosLista()
            val paresPuntajeCriterio = criteriosLista.map { criterio ->
                val puntaje = mapaPuntajes[criterio.id] ?: 1
                Pair(puntaje, criterio)
            }
            val puntajeTotal100 = CalculadoraPuntaje.calcularPuntajeEscala100(paresPuntajeCriterio)

            val evaluacionId = evaluacionDao.insertarEvaluacion(
                Evaluacion(
                    candidataId = candidataId,
                    evaluadoraNombre = evaluadoraNombre,
                    evaluadoraClave = evaluadoraClave,
                    observacionGeneral = observacionGeneral,
                    puntajeTotal = puntajeTotal100
                )
            )

            val puntajesCriterio = criteriosLista.map { criterio ->
                PuntajeCriterio(
                    evaluacionId = evaluacionId,
                    criterioId = criterio.id,
                    puntaje = mapaPuntajes[criterio.id] ?: 1,
                    observacion = mapaObservaciones[criterio.id] ?: ""
                )
            }
            evaluacionDao.insertarPuntajes(puntajesCriterio)

            val totalEvaluaciones = evaluacionDao.contarEvaluacionesDeCandidata(candidataId)
            val nuevoEstado = if (totalEvaluaciones >= Evaluacion.MINIMO_PARA_CONSOLIDAR) {
                EstadoCandidatura.EVALUADA
            } else {
                EstadoCandidatura.EN_EVALUACION
            }
            candidataDao.actualizarEstado(candidataId, nuevoEstado)

            historialDao.insertar(
                RegistroHistorial(
                    usuario = evaluadoraNombre,
                    accion = Acciones.EVALUACION_REGISTRADA,
                    detalle = "Evaluación registrada para ${candidata.nombre} con puntaje ${puntajeTotal100}/100. Estado: ${nuevoEstado.titulo}",
                    candidataId = candidataId
                )
            )
        }
    }
}