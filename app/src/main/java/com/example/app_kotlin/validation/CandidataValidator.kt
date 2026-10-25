package com.example.app_kotlin.validation

import com.example.app_kotlin.model.Candidata

object CandidataValidator {

    private val DOMINIOS_PERMITIDOS = listOf("ejemplo.cl", "example.com", "test.cl")

    fun validarNombre(nombre: String): ValidationResult {
        val limpio = nombre.trim()
        if (limpio.length < 3) return ValidationResult.Error("El nombre debe tener al menos 3 caracteres")
        if (limpio.length > 60) return ValidationResult.Error("El nombre no puede superar los 60 caracteres")
        if (!limpio.contains(" ")) return ValidationResult.Error("Debe ingresar nombre y apellido separados por espacio")
        if (!limpio.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s.'-]+$".toRegex())) {
            return ValidationResult.Error("El nombre solo puede contener letras y espacios")
        }
        return ValidationResult.Valido
    }

    fun validarCorreo(correo: String): ValidationResult {
        val limpio = correo.trim().lowercase()
        if (limpio.isBlank()) return ValidationResult.Error("El correo electrónico es obligatorio")
        val regexCorreo = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$".toRegex()
        if (!limpio.matches(regexCorreo)) return ValidationResult.Error("Formato de correo electrónico inválido")

        val partes = limpio.split("@")
        if (partes.size == 2) {
            val dominio = partes[1]
            if (!DOMINIOS_PERMITIDOS.contains(dominio)) {
                return ValidationResult.Error("Dominio ficticio no permitido. Use: ejemplo.cl, example.com o test.cl")
            }
        }
        return ValidationResult.Valido
    }

    fun validarTelefono(telefono: String): ValidationResult {
        val limpio = telefono.trim()
        if (limpio.isBlank()) return ValidationResult.Error("El teléfono es obligatorio")

        // Formatos aceptados: +56 9 1234 5678, +56912345678, 912345678
        val soloDigitos = limpio.replace("[^0-9]".toRegex(), "")
        if (soloDigitos.length == 9 && soloDigitos.startsWith("9")) {
            return ValidationResult.Valido
        }
        if (soloDigitos.length == 11 && soloDigitos.startsWith("569")) {
            return ValidationResult.Valido
        }
        return ValidationResult.Error("Ingrese un teléfono chileno válido (+56 9 XXXX XXXX)")
    }

    fun normalizarTelefono(telefono: String): String {
        val soloDigitos = telefono.trim().replace("[^0-9]".toRegex(), "")
        return if (soloDigitos.length == 9) {
            "+56 9 ${soloDigitos.substring(1, 5)} ${soloDigitos.substring(5)}"
        } else if (soloDigitos.length == 11 && soloDigitos.startsWith("569")) {
            "+56 9 ${soloDigitos.substring(3, 7)} ${soloDigitos.substring(7)}"
        } else {
            telefono.trim()
        }
    }

    fun validarArea(area: String): ValidationResult {
        if (area.isBlank() || !Candidata.AREAS.contains(area)) {
            return ValidationResult.Error("Debe seleccionar un área de especialización válida")
        }
        return ValidationResult.Valido
    }

    fun validarAnnosExp(annos: String): ValidationResult {
        val valor = annos.trim().toIntOrNull()
            ?: return ValidationResult.Error("Los años de experiencia deben ser un número entero")
        if (valor < 0 || valor > 50) {
            return ValidationResult.Error("Los años deben estar en el rango de 0 a 50")
        }
        return ValidationResult.Valido
    }

    fun validarResumenExp(resumen: String): ValidationResult {
        val limpio = resumen.trim()
        if (limpio.length < 30) return ValidationResult.Error("El resumen debe tener al menos 30 caracteres")
        if (limpio.length > 500) return ValidationResult.Error("El resumen no puede superar los 500 caracteres")
        return ValidationResult.Valido
    }

    fun validarLinkedinUrl(url: String): ValidationResult {
        val limpio = url.trim()
        if (limpio.isBlank()) return ValidationResult.Valido // Opcional
        if (!limpio.startsWith("https://www.linkedin.com/in/")) {
            return ValidationResult.Error("El enlace debe comenzar con https://www.linkedin.com/in/")
        }
        return ValidationResult.Valido
    }

    fun validarCv(cvUri: String?): ValidationResult {
        if (cvUri.isNull_or_blank()) {
            return ValidationResult.Error("Es necesario adjuntar el CV en formato PDF")
        }
        return ValidationResult.Valido
    }

    private fun String?.isNull_or_blank(): Boolean = this == null || this.trim().isEmpty()
}