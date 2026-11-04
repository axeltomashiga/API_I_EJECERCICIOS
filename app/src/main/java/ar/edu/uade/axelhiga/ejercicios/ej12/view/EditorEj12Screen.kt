package ar.edu.uade.axelhiga.ejercicios.ej12.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ar.edu.uade.axelhiga.ejercicios.ej12.viewmodel.EditorEj12ViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorEj12Screen(
    viewModel: EditorEj12ViewModel,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    // Cuando el ViewModel termina de guardar y levanta la bandera, disparamos la navegación
    LaunchedEffect(state.guardadoConExito) {
        if (state.guardadoConExito) {
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (state.id == null) "Nueva Nota" else "Editar Nota") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
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
                value = state.titulo,
                onValueChange = { viewModel.onTituloChange(it) },
                label = { Text("Título") },
                modifier = Modifier.fillMaxWidth()
            )

            TextField(
                value = state.contenido,
                onValueChange = { viewModel.onContenidoChange(it) },
                label = { Text("Contenido") },
                modifier = Modifier.fillMaxWidth().height(200.dp)
            )

            Button(
                onClick = { viewModel.onGuardarClick() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar Nota")
            }
        }
    }
}
