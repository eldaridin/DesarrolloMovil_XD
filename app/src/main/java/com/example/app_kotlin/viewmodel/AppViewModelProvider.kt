package com.example.app_kotlin.viewmodel

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.app_kotlin.WimApplication

object AppViewModelProvider {

    val Factory = viewModelFactory {
        initializer {
            SesionViewModel(
                historialRepository = wimApplication().container.historialRepository
            )
        }

        initializer {
            CandidatasViewModel(
                candidataRepository = wimApplication().container.candidataRepository
            )
        }

        initializer {
            CandidataFormViewModel(
                candidataRepository = wimApplication().container.candidataRepository
            )
        }

        initializer {
            ResultadosViewModel(
                evaluacionRepository = wimApplication().container.evaluacionRepository
            )
        }

        initializer {
            HistorialViewModel(
                historialRepository = wimApplication().container.historialRepository
            )
        }
    }

    fun detalleViewModelFactory(candidataId: Long) = viewModelFactory {
        initializer {
            DetalleCandidataViewModel(
                candidataId = candidataId,
                candidataRepository = wimApplication().container.candidataRepository,
                evaluacionRepository = wimApplication().container.evaluacionRepository,
                historialRepository = wimApplication().container.historialRepository
            )
        }
    }

    fun evaluacionViewModelFactory(candidataId: Long) = viewModelFactory {
        initializer {
            EvaluacionViewModel(
                candidataId = candidataId,
                candidataRepository = wimApplication().container.candidataRepository,
                evaluacionRepository = wimApplication().container.evaluacionRepository,
                notificador = wimApplication().container.notificador
            )
        }
    }
}

fun CreationExtras.wimApplication(): WimApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as WimApplication)