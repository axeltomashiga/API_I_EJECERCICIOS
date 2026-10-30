package ar.edu.uade.axelhiga.ejercicios.presentation.notas

import androidx.compose.foundation.clickable
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ar.edu.uade.axelhiga.ejercicios.data.local.entity.NotaEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgregarNotaScreen(
    viewModel: NotasViewModel = viewModel(
        factory = NotasViewModelFactory(LocalContext.current.applicationContext)
    )
) {
    // Estados del formulario y lista
    val notas by viewModel.notas.collectAsState()
    val titulo by viewModel.titulo.collectAsState()
    val contenido by viewModel.contenido.collectAsState()
    val importante by viewModel.importante.collectAsState()
    val mensaje by viewModel.mensaje.collectAsState()
    val guardando by viewModel.guardando.collectAsState()
    val notaIdEnEdicion by viewModel.notaIdEnEdicion.collectAsState()
    val ultimaNotaEliminada by viewModel.ultimaNotaEliminada.collectAsState()

    // Estados de filtros y estadísticas
    val searchQuery by viewModel.searchQuery.collectAsState()
    val soloImportantesFilter by viewModel.soloImportantesFilter.collectAsState()
    val ordenAscendente by viewModel.ordenAscendente.collectAsState()
    val totalNotas by viewModel.totalNotas.collectAsState()
    val totalImportantes by viewModel.totalImportantes.collectAsState()
    
    // Estado para el cuadro de diálogo de eliminación
    var notaAEliminar by remember { mutableStateOf<NotaEntity?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (notaIdEnEdicion != null) "Editar Nota" else "Nueva Nota (MVVM)") },
                actions = {
                    if (notaIdEnEdicion != null) {
                        TextButton(onClick = { viewModel.cancelarEdicion() }) {
                            Text("Cancelar", color = MaterialTheme.colorScheme.onPrimaryContainer)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        snackbarHost = {
            // Un Snackbar sencillo que se muestra cuando hay una nota eliminada temporalmente
            if (ultimaNotaEliminada != null) {
                Snackbar(
                    action = {
                        TextButton(onClick = { viewModel.deshacerEliminacion() }) {
                            Text("DESHACER", color = MaterialTheme.colorScheme.inversePrimary)
                        }
                    },
                    modifier = Modifier.padding(16.dp),
                    dismissAction = {
                        IconButton(onClick = { viewModel.descartarDeshacer() }) {
                            Icon(Icons.Default.Delete, contentDescription = "Descartar", tint = MaterialTheme.colorScheme.inverseOnSurface)
                        }
                    }
                ) {
                    Text("Se eliminó '${ultimaNotaEliminada?.titulo}'")
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Campo de Título
            OutlinedTextField(
                value = titulo,
                onValueChange = { viewModel.onTituloChange(it) },
                label = { Text("Título") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Campo de Contenido
            OutlinedTextField(
                value = contenido,
                onValueChange = { viewModel.onContenidoChange(it) },
                label = { Text("Contenido") },
                modifier = Modifier.fillMaxWidth().height(120.dp)
            )

            // Opción para marcar como importante
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = importante,
                    onCheckedChange = { viewModel.onImportanteChange(it) }
                )
                Text("Marcar como Importante")
            }

            // Botón Guardar
            Button(
                onClick = { viewModel.guardarNota() },
                modifier = Modifier.fillMaxWidth(),
                enabled = !guardando
            ) {
                Text(if (guardando) "Guardando..." else if (notaIdEnEdicion != null) "Actualizar Nota" else "Guardar Nota")
            }

            // Mensaje corto al terminar la operación
            if (mensaje.isNotEmpty()) {
                Text(
                    text = mensaje,
                    color = if (mensaje.startsWith("Error")) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()
            
            // --- SECCIÓN DE BÚSQUEDA Y FILTROS ---
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.onSearchQueryChange(it) },
                label = { Text("Buscar por título...") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = MaterialTheme.shapes.large
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilterChip(
                    selected = soloImportantesFilter,
                    onClick = { viewModel.toggleImportantesFilter() },
                    label = { Text("Solo Importantes") }
                )
                
                FilterChip(
                    selected = ordenAscendente,
                    onClick = { viewModel.toggleOrden() },
                    label = { Text(if (ordenAscendente) "Más antiguas primero" else "Más recientes primero") }
                )
            }

            // Estadísticas
            Text(
                text = "Total: $totalNotas | Importantes: $totalImportantes",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(8.dp))

            // LazyColumn que reacciona a los cambios en el flujo del ViewModel
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = notas,
                    key = { nota -> nota.id } // Clave única para optimización
                ) { nota ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.cargarNotaParaEdicion(nota) },
                        colors = CardDefaults.cardColors(
                            containerColor = if (nota.importante) MaterialTheme.colorScheme.tertiaryContainer 
                                             else MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = nota.titulo,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = nota.contenido,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            
                            // Botón de eliminar
                            IconButton(onClick = { notaAEliminar = nota }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Eliminar nota",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Cuadro de diálogo para confirmar la eliminación
    notaAEliminar?.let { nota ->
        AlertDialog(
            onDismissRequest = { notaAEliminar = null },
            title = { Text("Eliminar nota") },
            text = { Text("¿Estás seguro de que deseas eliminar la nota '${nota.titulo}'?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.eliminarNota(nota)
                        notaAEliminar = null
                    }
                ) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { notaAEliminar = null }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}