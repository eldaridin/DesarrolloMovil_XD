package com.example.app_kotlin.di

import android.content.Context
import com.example.app_kotlin.data.local.WimDatabase
import com.example.app_kotlin.data.repository.CandidataRepository
import com.example.app_kotlin.data.repository.EvaluacionRepository
import com.example.app_kotlin.data.repository.HistorialRepository
import com.example.app_kotlin.nativo.Notificador
import com.example.app_kotlin.nativo.NotificadorAndroid

class AppContainer(private val context: Context) {

    val database: WimDatabase by lazy {
        WimDatabase.obtener(context)
    }

    val candidataRepository: CandidataRepository by lazy {
        CandidataRepository(database.candidataDao(), database.historialDao())
    }

    val evaluacionRepository: EvaluacionRepository by lazy {
        EvaluacionRepository(database)
    }

    val historialRepository: HistorialRepository by lazy {
        HistorialRepository(database.historialDao())
    }

    val notificador: Notificador by lazy {
        NotificadorAndroid(context)
    }
}