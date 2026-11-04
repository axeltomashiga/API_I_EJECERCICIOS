package ar.edu.uade.axelhiga.ejercicios.ej2

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotasEj2Screen(
    viewModel: NotasEj2ViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Efecto secundario para mostrar el Toast cuando haya mensaje de éxito
    LaunchedEffect(state.mensajeExito) {
        state.mensajeExito?.let { mensaje ->
            Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show()
            viewModel.onEvent(NotasEj2Event.OnMensajeExitoMostrado)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Ej2: Estado vs Pantalla") })
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
                onValueChange = { viewModel.onEvent(NotasEj2Event.OnTituloChange(it)) },
                label = { Text("Título") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.guardando // Bloqueamos input si está guardando
            )

            TextField(
                value = state.contenido,
                onValueChange = { viewModel.onEvent(NotasEj2Event.OnContenidoChange(it)) },
                label = { Text("Contenido") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.guardando // Bloqueamos input si está guardando
            )

            // Mostramos el mensaje de error si existe
            if (state.errorValidacion != null) {
                Text(
                    text = state.errorValidacion!!,
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Button(
                onClick = { viewModel.onEvent(NotasEj2Event.OnGuardarClick) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.guardando // Deshabilitamos botón para evitar doble clic
            ) {
                if (state.guardando) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Guardando...")
                } else {
                    Text("Guardar Nota")
                }
            }
        }
    }
}
