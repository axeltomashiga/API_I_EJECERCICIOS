package ar.edu.uade.axelhiga.ejercicios.ej7.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ar.edu.uade.axelhiga.ejercicios.ej7.viewmodel.NotasEj7ViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotasEj7Screen(viewModel: NotasEj7ViewModel) {

    val state by viewModel.uiState.collectAsStateWithLifecycle()

    // Solo para pruebas manuales dentro del emulador
    var textoPrueba by remember { mutableStateOf("Hola desde Archivo de Texto") }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Ej7: Lectura FilesDir") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = { viewModel.cargarBorrador() },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.estaCargando
            ) {
                Text(if (state.estaCargando) "Leyendo disco..." else "Intentar Leer Archivo borrador.txt")
            }

            Text(
                text = "Estado: ${state.estadoLecturaInfo}",
                fontStyle = FontStyle.Italic,
                color = MaterialTheme.colorScheme.primary
            )

            OutlinedTextField(
                value = state.textoBorrador,
                onValueChange = {}, // ReadOnly
                readOnly = true,
                label = { Text("Contenido leído de disco") },
                modifier = Modifier.fillMaxWidth().height(150.dp)
            )

            Divider(modifier = Modifier.padding(vertical = 16.dp))

            // --- HERRAMIENTAS DE PRUEBA MANUAL ---
            Text("Testing Manual:", style = MaterialTheme.typography.titleSmall)
            TextField(
                value = textoPrueba,
                onValueChange = { textoPrueba = it },
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = { viewModel.guardarBorradorDePrueba(textoPrueba) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Escribir eso en borrador.txt")
            }
        }
    }
}
