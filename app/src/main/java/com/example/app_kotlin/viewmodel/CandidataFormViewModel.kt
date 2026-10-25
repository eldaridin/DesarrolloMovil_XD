package com.example.app_kotlin.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_kotlin.data.repository.CandidataRepository
import com.example.app_kotlin.model.Candidata
import com.example.app_kotlin.model.Sesion
import com.example.app_kotlin.nativo.ArchivoHelper
import com.example.app_kotlin.nativo.InfoArchivo
import com.example.app_kotlin.validation.CandidataValidator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CandidataFormUiState(
    val nombre: CampoState = CampoState(),
    val correo: CampoState = CampoState(),
    val telefono: CampoState = CampoState(),
    val area: CampoState = CampoState(),
    val annosExp: CampoState = CampoState(),
    val resumenExp: CampoState = CampoState(),
    val linkedinUrl: CampoState = CampoState(),
    val infoArchivoPdf: InfoArchivo? = null,
    val errorCvPdf: String? = null,
    val guardando: Boolean = false,
    val exitoGuardado: Boolean = false,
    val activadoSacudida: Boolean = false,
    val errorGeneral: String? = null
)

class CandidataFormViewModel(
    private val candidataRepository: CandidataRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CandidataFormUiState())
    val uiState: StateFlow<CandidataFormUiState> = _uiState.asStateFlow()

    fun onNombreChange(v: String) {
        _uiState.update { it.copy(nombre = it.nombre.con(v, CandidataValidator.validarNombre(v))) }
    }

    fun onCorreoChange(v: String) {
        _uiState.update { it.copy(correo = it.correo.con(v, CandidataValidator.validarCorreo(v))) }
    }

    fun onTelefonoChange(v: String) {
        _uiState.update { it.copy(telefono = it.telefono.con(v, CandidataValidator.validarTelefono(v))) }
    }

    fun onAreaChange(v: String) {
        _uiState.update { it.copy(area = it.area.con(v, CandidataValidator.validarArea(v))) }
    }

    fun onAnnosExpChange(v: String) {
        _uiState.update { it.copy(annosExp = it.annosExp.con(v, CandidataValidator.validarAnnosExp(v))) }
    }

    fun onResumenExpChange(v: String) {
        _uiState.update { it.copy(resumenExp = it.resumenExp.con(v, CandidataValidator.validarResumenExp(v))) }
    }

    fun onLinkedinUrlChange(v: String) {
        _uiState.update { it.copy(linkedinUrl = it.linkedinUrl.con(v, CandidataValidator.validarLinkedinUrl(v))) }
    }

    fun onArchivoPdfSeleccionado(context: Context, uri: Uri?) {
        if (uri == null) return
        val info = ArchivoHelper.obtenerInfoArchivo(context, uri)
        if (info == null) {
            _uiState.update { it.copy(errorCvPdf = "No se pudo leer el archivo seleccionado") }
            return
        }
        if (!info.nombre.lowercase().endsWith(".pdf")) {
            _uiState.update { it.copy(errorCvPdf = "El archivo debe ser un documento PDF") }
            return
        }
        _uiState.update { it.copy(infoArchivoPdf = info, errorCvPdf = null) }
    }

    fun guardarCandidata(context: Context, onExito: () -> Unit) {
        val st = _uiState.value

        // Validar todos los campos antes de guardar
        val resNombre = CandidataValidator.validarNombre(st.nombre.valor)
        val resCorreo = CandidataValidator.validarCorreo(st.correo.valor)
        val resTelefono = CandidataValidator.validarTelefono(st.telefono.valor)
        val resArea = CandidataValidator.validarArea(st.area.valor)
        val resAnnos = CandidataValidator.validarAnnosExp(st.annosExp.valor)
        val resResumen = CandidataValidator.validarResumenExp(st.resumenExp.valor)
        val resLinkedin = CandidataValidator.validarLinkedinUrl(st.linkedinUrl.valor)
        val resCv = CandidataValidator.validarCv(st.infoArchivoPdf?.uri?.toString())

        val tieneErrores = !resNombre.esValido || !resCorreo.esValido || !resTelefono.esValido ||
            !resArea.esValido || !resAnnos.esValido || !resResumen.esValido ||
            !resLinkedin.esValido || !resCv.esValido

        if (tieneErrores) {
            _uiState.update {
                it.copy(
                    nombre = it.nombre.con(it.nombre.valor, resNombre),
                    correo = it.correo.con(it.correo.valor, resCorreo),
                    telefono = it.telefono.con(it.telefono.valor, resTelefono),
                    area = it.area.con(it.area.valor, resArea),
                    annosExp = it.annosExp.con(it.annosExp.valor, resAnnos),
                    resumenExp = it.resumenExp.con(it.resumenExp.valor, resResumen),
                    linkedinUrl = it.linkedinUrl.con(it.linkedinUrl.valor, resLinkedin),
                    errorCvPdf = resCv.mensajeError,
                    activadoSacudida = !it.activadoSacudida
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(guardando = true, errorGeneral = null) }

            // Verificar correo duplicado
            if (candidataRepository.existeCorreo(st.correo.valor)) {
                _uiState.update {
                    it.copy(
                        guardando = false,
                        correo = it.correo.copy(error = "Este correo electrónico ya fue registrado previamente"),
                        activadoSacudida = !it.activadoSacudida
                    )
                }
                return@launch
            }

            // Copiar PDF si existe
            var pathGuardado: String? = null
            var nombrePdf: String? = null
            if (st.infoArchivoPdf != null) {
                nombrePdf = "${System.currentTimeMillis()}_${st.infoArchivoPdf.nombre}"
                pathGuardado = ArchivoHelper.copiarArchivoAAlmacenamientoInterno(
                    context,
                    st.infoArchivoPdf.uri,
                    nombrePdf
                )
            }

            val nuevaCandidata = Candidata(
                nombre = st.nombre.valor.trim(),
                correo = st.correo.valor.trim().lowercase(),
                telefono = CandidataValidator.normalizarTelefono(st.telefono.valor),
                area = st.area.valor,
                annosExp = st.annosExp.valor.trim().toInt(),
                resumenExp = st.resumenExp.valor.trim(),
                linkedinUrl = st.linkedinUrl.valor.trim(),
                cvUri = pathGuardado,
                cvNombre = st.infoArchivoPdf?.nombre ?: nombrePdf,
                registrador = if (Sesion.usuarioActivo.isNotBlank()) Sesion.usuarioActivo else "Evaluadora"
            )

            candidataRepository.registrarCandidata(nuevaCandidata)
            _uiState.update { it.copy(guardando = false, exitoGuardado = true) }
            onExito()
        }
    }
}