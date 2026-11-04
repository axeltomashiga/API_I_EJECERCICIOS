package ar.edu.uade.axelhiga.ejercicios.ej6.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ar.edu.uade.axelhiga.ejercicios.ej6.viewmodel.NotasEj6ViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotasEj6Screen(viewModel: NotasEj6ViewModel) {

    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val listaNotas by viewModel.notas.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Ej6: Interfaz Repository") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
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

            if (state.error != null) {
                Text(text = state.error!!, color = Color.Red)
            }

            Button(
                onClick = { viewModel.onGuardarClick() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar")
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(listaNotas) { nota ->
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
                            IconButton(onClick = { viewModel.onEliminarClick(nota.id) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Eliminar nota", tint = Color.Red)
                            }
                        }
                    }
                }
            }
        }
    }
}
