package com.example.app_kotlin.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.app_kotlin.data.local.dao.CandidataDao
import com.example.app_kotlin.data.local.dao.CriterioDao
import com.example.app_kotlin.data.local.dao.EvaluacionDao
import com.example.app_kotlin.data.local.dao.HistorialDao
import com.example.app_kotlin.model.Candidata
import com.example.app_kotlin.model.Criterio
import com.example.app_kotlin.model.Evaluacion
import com.example.app_kotlin.model.PuntajeCriterio
import com.example.app_kotlin.model.RegistroHistorial

@Database(
    entities = [
        Candidata::class,
        Criterio::class,
        Evaluacion::class,
        PuntajeCriterio::class,
        RegistroHistorial::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class WimDatabase : RoomDatabase() {

    abstract fun candidataDao(): CandidataDao
    abstract fun criterioDao(): CriterioDao
    abstract fun evaluacionDao(): EvaluacionDao
    abstract fun historialDao(): HistorialDao

    companion object {
        @Volatile
        private var INSTANCE: WimDatabase? = null

        fun obtener(context: Context): WimDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WimDatabase::class.java,
                    "wim100.db"
                )
                    .fallbackToDestructiveMigration(false)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}