package ar.edu.uade.axelhiga.ejercicios.ej10.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ar.edu.uade.axelhiga.ejercicios.ej10.viewmodel.NotasEj10ViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotasEj10Screen(viewModel: NotasEj10ViewModel) {

    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Ej10: Persistencia de Lista JSON") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Text("Alta o Edición de Nota", style = MaterialTheme.typography.titleMedium)

            TextField(
                value = state.titulo,
                onValueChange = { viewModel.onTituloChange(it) },
                label = { Text("Título") },
                modifier = Modifier.fillMaxWidth()
            )

            TextField(
                value = state.contenido,
                onValueChange = { viewModel.onContenidoChange(it) },
                label = { Text("Contenido") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = { viewModel.onGuardarClick() },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading
            ) {
                Text(if (state.idEditando == null) "Agregar Nota" else "Guardar Cambios")
            }

            Divider()

            Text("Notas Persistidas (JSON):", style = MaterialTheme.typography.titleMedium)

            if (state.isLoading) {
                CircularProgressIndicator()
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(state.listaPersistida) { nota ->
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = nota.titulo, style = MaterialTheme.typography.titleMedium)
                                    Text(text = nota.contenido, style = MaterialTheme.typography.bodyMedium)
                                }
                                Row {
                                    IconButton(onClick = { viewModel.onEditarClick(nota) }) {
                                        Icon(Icons.Default.Edit, contentDescription = "Editar")
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
    }
}
