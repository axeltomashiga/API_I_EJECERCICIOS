package ar.edu.uade.axelhiga.ejercicios.ej8.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ar.edu.uade.axelhiga.ejercicios.ej8.viewmodel.NotasEj8ViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotasEj8Screen(viewModel: NotasEj8ViewModel) {

    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Ej8: Supervivencia de I/O") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Text(
                text = "Status: ${state.mensajeStatus}",
                fontStyle = FontStyle.Italic,
                color = MaterialTheme.colorScheme.primary
            )

            OutlinedTextField(
                value = state.textoFormulario,
                onValueChange = { viewModel.onTextoChange(it) },
                label = { Text("Área del borrador (con saltos de línea)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                enabled = !state.estaGuardando
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { viewModel.onGuardarClick() },
                    modifier = Modifier.weight(1f),
                    enabled = !state.estaGuardando
                ) {
                    Text(if (state.estaGuardando) "..." else "Guardar Físico")
                }

                // DESAFÍO: Botón Descartar Cambios
                OutlinedButton(
                    onClick = { viewModel.onDescartarClick() },
                    modifier = Modifier.weight(1f),
                    enabled = !state.estaGuardando
                ) {
                    Text("Descartar", color = Color.Red)
                }
            }

            Text(
                text = "Prueba escribir (incluyendo ñ, acentos y saltos de línea). Guarda, cierra la app (Swipe desde Recientes) y vuelve a abrirla.",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}
