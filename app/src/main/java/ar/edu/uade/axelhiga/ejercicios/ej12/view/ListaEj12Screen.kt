package ar.edu.uade.axelhiga.ejercicios.ej12.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ar.edu.uade.axelhiga.ejercicios.ej12.viewmodel.ListaEj12ViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaEj12Screen(
    viewModel: ListaEj12ViewModel,
    onNavigateToEditor: (String?) -> Unit
) {
    val notas by viewModel.notas.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Ej12: Mis Notas") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { onNavigateToEditor(null) }) { // null = Crear nueva
                Icon(Icons.Default.Add, contentDescription = "Nueva Nota")
            }
        }
    ) { padding ->
        if (notas.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("No hay notas guardadas.", style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize().padding(padding)
            ) {
                items(notas) { nota ->
                    Card(
                        modifier = Modifier.fillMaxWidth().clickable {
                            onNavigateToEditor(nota.id) // Navegamos pasando el ID
                        }
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = nota.titulo.ifBlank { "Sin título" }, style = MaterialTheme.typography.titleMedium)
                                Text(text = nota.contenido, style = MaterialTheme.typography.bodyMedium, maxLines = 1)
                            }
                            IconButton(onClick = { viewModel.onEliminarClick(nota.id) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.Red)
                            }
                        }
                    }
                }
            }
        }
    }
}
