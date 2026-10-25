package com.example.app_kotlin.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.app_kotlin.nativo.ArchivoHelper
import com.example.app_kotlin.nativo.ResultadoPdf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun VisorCvDialog(
    pathArchivoPdf: String,
    nombreArchivo: String,
    onDismiss: () -> Unit
) {
    var paginaActual by remember { mutableStateOf(0) }
    var resultadoState by remember { mutableStateOf<ResultadoPdf>(ResultadoPdf.Cargando) }

    LaunchedEffect(pathArchivoPdf, paginaActual) {
        resultadoState = ResultadoPdf.Cargando
        withContext(Dispatchers.IO) {
            resultadoState = ArchivoHelper.renderizarPaginaPdf(pathArchivoPdf, paginaActual)
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar visor")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.PictureAsPdf,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = nombreArchivo,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.weight(1f)
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    when (val res = resultadoState) {
                        is ResultadoPdf.Cargando -> {
                            CircularProgressIndicator()
                        }
                        is ResultadoPdf.Ok -> {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Image(
                                    bitmap = res.bitmapPage.asImageBitmap(),
                                    contentDescription = "Página PDF",
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    IconButton(
                                        onClick = { if (paginaActual > 0) paginaActual-- },
                                        enabled = paginaActual > 0
                                    ) {
                                        Icon(Icons.Default.ArrowBack, contentDescription = "Anterior")
                                    }
                                    Text(
                                        text = "Página ${res.paginaActual} de ${res.totalPaginas}",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    IconButton(
                                        onClick = { if (paginaActual < res.totalPaginas - 1) paginaActual++ },
                                        enabled = paginaActual < res.totalPaginas - 1
                                    ) {
                                        Icon(Icons.Default.ArrowForward, contentDescription = "Siguiente")
                                    }
                                }
                            }
                        }
                        is ResultadoPdf.Error -> {
                            Card(modifier = Modifier.padding(24.dp)) {
                                Text(
                                    text = res.mensaje,
                                    color = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}