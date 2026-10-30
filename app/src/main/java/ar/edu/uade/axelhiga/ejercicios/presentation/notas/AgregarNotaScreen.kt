package ar.edu.uade.axelhiga.ejercicios.presentation.notas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
    // Escuchando los estados desde el ViewModel
    val notas by viewModel.notas.collectAsState()
    val titulo by viewModel.titulo.collectAsState()
    val contenido by viewModel.contenido.collectAsState()
    val importante by viewModel.importante.collectAsState()
    val mensaje by viewModel.mensaje.collectAsState()
    val guardando by viewModel.guardando.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Nueva Nota (MVVM)") })
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
                Text(if (guardando) "Guardando..." else "Guardar Nota")
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
            Spacer(modifier = Modifier.height(8.dp))

            // Título de la lista
            Text(
                text = "Tus Notas Guardadas",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

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
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = if (nota.importante) MaterialTheme.colorScheme.tertiaryContainer 
                                             else MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
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
                    }
                }
            }
        }
    }
}