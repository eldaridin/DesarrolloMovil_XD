package com.example.app_kotlin.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.app_kotlin.navigation.DestinoPrincipal
import com.example.app_kotlin.navigation.WimNavHost
import com.example.app_kotlin.nativo.SolicitudPermisoNotificaciones
import com.example.app_kotlin.viewmodel.AppViewModelProvider
import com.example.app_kotlin.viewmodel.SesionViewModel

@Composable
fun WimApp(
    sesionViewModel: SesionViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    val sesionState by sesionViewModel.uiState.collectAsState()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val rutaActual = navBackStackEntry?.destination?.route

    val mostrarBottomBar = sesionState.estaAutenticado &&
            DestinoPrincipal.entries.any { it.ruta == rutaActual }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            AnimatedVisibility(
                visible = mostrarBottomBar,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it })
            ) {
                Surface(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .clip(RoundedCornerShape(24.dp)),
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp,
                    shadowElevation = 12.dp
                ) {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                        tonalElevation = 0.dp
                    ) {
                        DestinoPrincipal.entries.forEach { destino ->
                            val seleccionado = rutaActual == destino.ruta
                            NavigationBarItem(
                                selected = seleccionado,
                                onClick = {
                                    navController.navigate(destino.ruta) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                icon = {
                                    Icon(
                                        imageVector = destino.icono,
                                        contentDescription = destino.titulo
                                    )
                                },
                                label = {
                                    Text(
                                        text = destino.titulo,
                                        fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MaterialTheme.colorScheme.primary,
                                    selectedTextColor = MaterialTheme.colorScheme.primary,
                                    indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                                )
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        WimNavHost(
            navController = navController,
            sesionViewModel = sesionViewModel,
            modifier = Modifier.padding(innerPadding)
        )

        if (sesionState.estaAutenticado) {
            SolicitudPermisoNotificaciones()
        }
    }
}