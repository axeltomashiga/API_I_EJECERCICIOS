package ar.edu.uade.axelhiga.ejercicios.presentation.actividadlistscreen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ar.edu.uade.axelhiga.ejercicios.domain.model.actividad.Actividad

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActividadListScreen(
    viewModel: ActividadListScreenViewModel = viewModel(
        factory = ActividadViewModelFactory(LocalContext.current.applicationContext)
    )
) {
    // Observamos los estados desde el ViewModel
    val actividades by viewModel.actividades.collectAsState()
    val pendientes by viewModel.pendientes.collectAsState()

    // Estados locales para los campos del formulario
    var titulo by remember { mutableStateOf("") }
    var materia by remember { mutableStateOf("") }
    
    // Para simplificar la prioridad en esta etapa usaremos un número del 1 al 3
    // donde 1 es Alta, 2 es Media, 3 es Baja
    var prioridadText by remember { mutableStateOf("") }
    
    // Variables de control de error en inputs
    var showError by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestor de Actividades") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // --- SECCIÓN DE INGRESO (FORMULARIO) ---
            Text(text = "Nueva Actividad", style = MaterialTheme.typography.titleMedium)
            
            OutlinedTextField(
                value = titulo,
                onValueChange = { titulo = it },
                label = { Text("Título (Ej: Leer Capítulo 4)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            OutlinedTextField(
                value = materia,
                onValueChange = { materia = it },
                label = { Text("Materia (Ej: Matemáticas)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = prioridadText,
                onValueChange = { prioridadText = it },
                label = { Text("Prioridad (1=Alta, 2=Media, 3=Baja)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            if (showError) {
                Text(
                    text = "Por favor, completa todos los campos correctamente.",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val prioridadNum = prioridadText.toIntOrNull()
                    if (titulo.isNotBlank() && materia.isNotBlank()) {
                        viewModel.agregarActividad(titulo, materia, prioridadNum)
                        // Limpiamos los campos
                        titulo = ""
                        materia = ""
                        prioridadText = ""
                        showError = false
                    } else {
                        showError = true
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Agregar Actividad")
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- CONTADOR DE PENDIENTES ---
            Text(
                text = "Tareas pendientes: $pendientes",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(8.dp))

            // --- SECCIÓN DE LISTA ---
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = actividades,
                    key = { actividad -> actividad.id } // Clave única para optimizar Compose
                ) { actividad ->
                    ActividadItem(
                        actividad = actividad,
                        onCheckedChange = { isChecked ->
                            viewModel.cambiarEstadoCompletada(actividad.id, isChecked)
                        },
                        onDeleteClick = {
                            viewModel.eliminarActividad(actividad.id)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ActividadItem(
    actividad: Actividad,
    onCheckedChange: (Boolean) -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Checkbox para marcar completada
            Checkbox(
                checked = actividad.completada,
                onCheckedChange = onCheckedChange
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            // Textos descriptivos
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = actividad.titulo,
                    style = MaterialTheme.typography.titleMedium,
                    textDecoration = if (actividad.completada) TextDecoration.LineThrough else null,
                    color = if (actividad.completada) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${actividad.materia} • Prioridad: ${actividad.prioridad ?: "Sin definir"}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // Botón de eliminar
            IconButton(onClick = onDeleteClick) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar Actividad",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}