package com.example.app_kotlin.data.local

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import com.example.app_kotlin.model.Acciones
import com.example.app_kotlin.model.CalculadoraPuntaje
import com.example.app_kotlin.model.Candidata
import com.example.app_kotlin.model.Criterio
import com.example.app_kotlin.model.EstadoCandidatura
import com.example.app_kotlin.model.Evaluacion
import com.example.app_kotlin.model.PuntajeCriterio
import com.example.app_kotlin.model.RegistroHistorial
import java.io.File
import java.io.FileOutputStream

object DatosSemilla {

    suspend fun poblarSiVacia(db: WimDatabase, context: Context) {
        if (db.criterioDao().contarCriterios() == 0) {
            val criterios = listOf(
                Criterio(1, "Experiencia técnica en ciberseguridad", "Dominio de herramientas, metodologías y estándares de seguridad", 25, 1),
                Criterio(2, "Liderazgo y gestión de equipos", "Capacidad para dirigir proyectos y mentoría a otros profesionales", 20, 2),
                Criterio(3, "Innovación y proyectos destacados", "Contribuciones relevantes, desarrollo de herramientas o investigaciones", 20, 3),
                Criterio(4, "Impacto en la comunidad e industria", "Participación en conferencias, comunidades WIM o difusión técnica", 20, 4),
                Criterio(5, "Formación y certificaciones", "Grados académicos y certificaciones vigentes (CISSP, CISM, CEH, etc.)", 15, 5)
            )
            db.criterioDao().insertarCriterios(criterios)
        }

        if (db.candidataDao().contarCandidatas() == 0) {
            // Generar CVs de prueba en formato PDF
            val cvPath1 = generarPdfCvEjemplo(context, "Valentina_Silva_CV.pdf", "Valentina Silva", "Especialista Red Team")
            val cvPath2 = generarPdfCvEjemplo(context, "Camila_Rojas_CV.pdf", "Camila Rojas", "Líder SOC Blue Team")

            val candidatas = listOf(
                Candidata(
                    id = 1,
                    nombre = "Valentina Silva M.",
                    correo = "valentina.silva@ejemplo.cl",
                    telefono = "+56 9 8765 4321",
                    area = "Seguridad ofensiva (Red Team)",
                    annosExp = 7,
                    resumenExp = "Especialista en pentesting Web/Mobile y simulación de adversarios. Certificada OSCP y eWPTX.",
                    linkedinUrl = "https://www.linkedin.com/in/valentina-silva-cyber",
                    cvUri = cvPath1,
                    cvNombre = "Valentina_Silva_CV.pdf",
                    estado = EstadoCandidatura.EVALUADA,
                    registrador = "Evaluadora Demo Uno"
                ),
                Candidata(
                    id = 2,
                    nombre = "Camila Rojas P.",
                    correo = "camila.rojas@example.com",
                    telefono = "+56 9 7654 3210",
                    area = "Seguridad defensiva (Blue Team)",
                    annosExp = 9,
                    resumenExp = "Líder de SOC con amplia experiencia en detección de amenazas, SIEM/EDR y respuesta a incidentes críticos.",
                    linkedinUrl = "https://www.linkedin.com/in/camila-rojas-soc",
                    cvUri = cvPath2,
                    cvNombre = "Camila_Rojas_CV.pdf",
                    estado = EstadoCandidatura.EN_EVALUACION,
                    registrador = "Evaluadora Demo Dos"
                ),
                Candidata(
                    id = 3,
                    nombre = "Isadora Morales V.",
                    correo = "isadora.morales@test.cl",
                    telefono = "+56 9 6543 2109",
                    area = "Gobierno, riesgo y cumplimiento",
                    annosExp = 5,
                    resumenExp = "Consultora Senior de cumplimiento ISO 27001, NIST y gestión de riesgos tecnológicos.",
                    linkedinUrl = "https://www.linkedin.com/in/isadora-morales-grc",
                    cvUri = null,
                    cvNombre = null,
                    estado = EstadoCandidatura.POSTULADA,
                    registrador = "Evaluadora Demo Uno"
                ),
                Candidata(
                    id = 4,
                    nombre = "Fernanda Castro G.",
                    correo = "fernanda.castro@ejemplo.cl",
                    telefono = "+56 9 5432 1098",
                    area = "Ciberseguridad industrial (OT)",
                    annosExp = 12,
                    resumenExp = "Ingeniera especialista en seguridad SCADA/ICS en sector energético y minería.",
                    linkedinUrl = "https://www.linkedin.com/in/fernanda-castro-ot",
                    cvUri = null,
                    cvNombre = null,
                    estado = EstadoCandidatura.POSTULADA,
                    registrador = "Evaluadora Demo Dos"
                )
            )

            for (c in candidatas) {
                db.candidataDao().insertar(c)
                db.historialDao().insertar(
                    RegistroHistorial(
                        usuario = c.registrador,
                        accion = Acciones.REGISTRO_CANDIDATA,
                        detalle = "Postulación registrada para ${c.nombre} en ${c.area}",
                        candidataId = c.id
                    )
                )
            }

            // Inserción de Evaluaciones Semilla
            val criteriosList = db.criterioDao().obtenerCriteriosLista()

            // Eval 1 para Valentina
            val ev1Puntajes = listOf(5, 4, 5, 5, 4)
            val puntajeTotal1 = CalculadoraPuntaje.calcularPuntajeEscala100(ev1Puntajes.zip(criteriosList))
            val ev1Id = db.evaluacionDao().insertarEvaluacion(
                Evaluacion(
                    candidataId = 1,
                    evaluadoraNombre = "Evaluadora Demo Uno",
                    evaluadoraClave = CalculadoraPuntaje.normalizarClaveEvaluadora("Evaluadora Demo Uno"),
                    observacionGeneral = "Excelente perfil técnico con gran proyección de liderazgo.",
                    puntajeTotal = puntajeTotal1
                )
            )
            db.evaluacionDao().insertarPuntajes(
                ev1Puntajes.mapIndexed { index, p ->
                    PuntajeCriterio(evaluacionId = ev1Id, criterioId = criteriosList[index].id, puntaje = p, observacion = "Sólido desempeño")
                }
            )

            // Eval 2 para Valentina (hace que pase a EVALUADA)
            val ev2Puntajes = listOf(5, 5, 4, 4, 5)
            val puntajeTotal2 = CalculadoraPuntaje.calcularPuntajeEscala100(ev2Puntajes.zip(criteriosList))
            val ev2Id = db.evaluacionDao().insertarEvaluacion(
                Evaluacion(
                    candidataId = 1,
                    evaluadoraNombre = "Evaluadora Demo Dos",
                    evaluadoraClave = CalculadoraPuntaje.normalizarClaveEvaluadora("Evaluadora Demo Dos"),
                    observacionGeneral = "Perfil muy completo y con destacadas certificaciones internacionales.",
                    puntajeTotal = puntajeTotal2
                )
            )
            db.evaluacionDao().insertarPuntajes(
                ev2Puntajes.mapIndexed { index, p ->
                    PuntajeCriterio(evaluacionId = ev2Id, criterioId = criteriosList[index].id, puntaje = p, observacion = "Conocimientos sobresalientes")
                }
            )

            // Eval 1 para Camila (queda EN_EVALUACION con 1 evaluación)
            val ev3Puntajes = listOf(4, 5, 4, 3, 4)
            val puntajeTotal3 = CalculadoraPuntaje.calcularPuntajeEscala100(ev3Puntajes.zip(criteriosList))
            val ev3Id = db.evaluacionDao().insertarEvaluacion(
                Evaluacion(
                    candidataId = 2,
                    evaluadoraNombre = "Evaluadora Demo Uno",
                    evaluadoraClave = CalculadoraPuntaje.normalizarClaveEvaluadora("Evaluadora Demo Uno"),
                    observacionGeneral = "Gran capacidad en liderazgo de incidentes SOC.",
                    puntajeTotal = puntajeTotal3
                )
            )
            db.evaluacionDao().insertarPuntajes(
                ev3Puntajes.mapIndexed { index, p ->
                    PuntajeCriterio(evaluacionId = ev3Id, criterioId = criteriosList[index].id, puntaje = p, observacion = "Buen desempeño")
                }
            )
        }
    }

    private fun generarPdfCvEjemplo(context: Context, nombreArchivo: String, nombreCandidata: String, cargo: String): String? {
        return try {
            val folder = File(context.filesDir, "cvs")
            if (!folder.exists()) folder.mkdirs()
            val file = File(folder, nombreArchivo)
            if (file.exists()) return file.absolutePath

            val pdfDocument = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
            val page = pdfDocument.startPage(pageInfo)
            val canvas: Canvas = page.canvas
            val paint = Paint()

            paint.color = Color.rgb(30, 27, 75)
            paint.textSize = 22f
            paint.isFakeBoldText = true
            canvas.drawText("Currículum Vitae - WIM 100", 40f, 50f, paint)

            paint.color = Color.rgb(109, 40, 217)
            paint.textSize = 18f
            canvas.drawText(nombreCandidata, 40f, 90f, paint)

            paint.color = Color.DKGRAY
            paint.textSize = 14f
            paint.isFakeBoldText = false
            canvas.drawText(cargo, 40f, 115f, paint)

            paint.textSize = 12f
            canvas.drawText("Documento adjunto oficial cargado en la plataforma de postulación WIM 100.", 40f, 160f, paint)
            canvas.drawText("Verificación de antecedentes y trayectoria en ciberseguridad.", 40f, 180f, paint)

            pdfDocument.finishPage(page)
            val fos = FileOutputStream(file)
            pdfDocument.writeTo(fos)
            fos.close()
            pdfDocument.close()

            file.absolutePath
        } catch (e: Exception) {
            null
        }
    }
}