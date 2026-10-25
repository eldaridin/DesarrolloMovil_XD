package com.example.app_kotlin.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.PersonSearch
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.app_kotlin.model.Candidata
import com.example.app_kotlin.model.EstadoCandidatura
import com.example.app_kotlin.model.Sesion
import com.example.app_kotlin.ui.components.AvatarIniciales
import com.example.app_kotlin.ui.components.EstadoChip
import com.example.app_kotlin.ui.components.EstadoVacio
import com.example.app_kotlin.ui.components.GlassCard
import com.example.app_kotlin.viewmodel.CandidatasUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CandidatasScreen(
    state: CandidatasUiState,
    onBusquedaChange: (String) -> Unit,
    onFiltroEstadoChange: (EstadoCandidatura?) -> Unit,
    onCandidataClick: (Long) -> Unit,
    onNuevaCandidataClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Postulantes WIM 100",
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "Perfil Activo: ${Sesion.rolActivo.titulo} • ${state.candidatas.size} registros",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNuevaCandidataClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Registrar candidata")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Buscador con botón para limpiar
            OutlinedTextField(
                value = state.busquedaTexto,
                onValueChange = onBusquedaChange,
                placeholder = { Text("Buscar por nombre, área o correo...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                trailingIcon = if (state.busquedaTexto.isNotBlank()) {
                    {
                        IconButton(onClick = { onBusquedaChange("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Limpiar")
                        }
                    }
                } else null,
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            )

            // Chips de filtro horizontal con contadores
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 10.dp)
            ) {
                item {
                    ElevatedFilterChip(
                        selected = (state.filtroEstado == null),
                        onClick = { onFiltroEstadoChange(null) },
                        label = { Text("Todas") },
                        leadingIcon = if (state.filtroEstado == null) {
                            { Icon(Icons.Default.FilterList, contentDescription = null) }
                        } else null
                    )
                }

                items(EstadoCandidatura.entries) { estado ->
                    ElevatedFilterChip(
                        selected = (state.filtroEstado == estado),
                        onClick = { onFiltroEstadoChange(estado) },
                        label = { Text(estado.titulo) }
                    )
                }
            }

            if (state.cargando) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (state.candidatas.isEmpty()) {
                EstadoVacio(
                    mensaje = "No se encontraron postulantes",
                    subtitulo = "Intente ajustar los parámetros de búsqueda o registre una nueva candidatura.",
                    icono = Icons.Default.PersonSearch
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(
                        items = state.candidatas,
                        key = { it.id }
                    ) { candidata ->
                        CandidataItemCard(
                            candidata = candidata,
                            onClick = { onCandidataClick(candidata.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CandidataItemCard(
    candidata: Candidata,
    onClick: () -> Unit
) {
    GlassCard(
        shape = RoundedCornerShape(18.dp),
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AvatarIniciales(nombre = candidata.nombre, size = 52.dp)

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = candidata.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(2.dp))

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                    modifier = Modifier.padding(vertical = 2.dp)
                ) {
                    Text(
                        text = candidata.area,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Text(
                    text = "${candidata.annosExp} años exp. • ${candidata.correo}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                EstadoChip(estado = candidata.estado)
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Ver expediente",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}