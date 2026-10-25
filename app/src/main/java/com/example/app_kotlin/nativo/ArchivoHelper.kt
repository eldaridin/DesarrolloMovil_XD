package com.example.app_kotlin.nativo

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.provider.OpenableColumns
import java.io.File
import java.io.FileOutputStream

data class InfoArchivo(
    val nombre: String,
    val tamanoBytes: Long,
    val uri: Uri
)

sealed interface ResultadoPdf {
    object Cargando : ResultadoPdf
    data class Ok(val bitmapPage: Bitmap, val totalPaginas: Int, val paginaActual: Int) : ResultadoPdf
    data class Error(val mensaje: String) : ResultadoPdf
}

object ArchivoHelper {

    fun obtenerInfoArchivo(context: Context, uri: Uri): InfoArchivo? {
        return try {
            var nombre = "CV_Candidata.pdf"
            var tamano = 0L
            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val indexNombre = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    val indexTamano = cursor.getColumnIndex(OpenableColumns.SIZE)
                    if (indexNombre != -1) nombre = cursor.getString(indexNombre) ?: nombre
                    if (indexTamano != -1) tamano = cursor.getLong(indexTamano)
                }
            }
            InfoArchivo(nombre, tamano, uri)
        } catch (e: Exception) {
            null
        }
    }

    fun copiarArchivoAAlmacenamientoInterno(context: Context, uri: Uri, nombreDestino: String): String? {
        return try {
            val folder = File(context.filesDir, "cvs")
            if (!folder.exists()) folder.mkdirs()
            val fileDestino = File(folder, nombreDestino)

            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(fileDestino).use { output ->
                    input.copyTo(output)
                }
            }
            fileDestino.absolutePath
        } catch (e: Exception) {
            null
        }
    }

    fun renderizarPaginaPdf(pathArchivo: String, numeroPagina: Int = 0): ResultadoPdf {
        val file = File(pathArchivo)
        if (!file.exists()) return ResultadoPdf.Error("El archivo de CV no se encuentra disponible")

        return try {
            val pfd = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
            val renderer = PdfRenderer(pfd)

            val count = renderer.pageCount
            if (count == 0) {
                renderer.close()
                pfd.close()
                return ResultadoPdf.Error("El PDF no contiene páginas")
            }

            val pageIndex = numeroPagina.coerceIn(0, count - 1)
            val page = renderer.openPage(pageIndex)

            val width = page.width * 2
            val height = page.height * 2
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

            page.close()
            renderer.close()
            pfd.close()

            ResultadoPdf.Ok(bitmap, count, pageIndex + 1)
        } catch (e: Exception) {
            ResultadoPdf.Error("No se pudo visualizar el PDF: ${e.localizedMessage}")
        }
    }
}