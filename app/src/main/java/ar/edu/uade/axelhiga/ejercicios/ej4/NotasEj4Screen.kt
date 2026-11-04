package ar.edu.uade.axelhiga.ejercicios.ej4

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotasEj4Screen(
    viewModel: NotasEj4ViewModel = viewModel()
) {
    // La pantalla recolecta el estado con ciclo de vida seguro (lifecycle-aware)
    // 'by' nos permite leer las propiedades de uiState de forma directa
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Ej4: StateFlow Observable") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TextField(
                value = uiState.titulo,
                // Al tipear, lanzamos una función que internamente usa .copy()
                onValueChange = { viewModel.actualizarTitulo(it) },
                label = { Text("Título") },
                modifier = Modifier.fillMaxWidth()
            )

            TextField(
                value = uiState.contenido,
                // Al tipear, la función no borra el título ya escrito
                onValueChange = { viewModel.actualizarContenido(it) },
                label = { Text("Contenido") },
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = "Nota: Observá cómo al escribir en un campo, el otro campo no pierde su texto. El ViewModel los mantiene aislados usando .copy().",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
