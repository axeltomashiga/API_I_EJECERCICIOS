package ar.edu.uade.axelhiga.ejercicios.ej3

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotasEj3Screen(
    // Obtención del ViewModel mediante viewModel().
    // Compose se encarga de que, si rotas la pantalla, te devuelva la MISMA instancia
    // y no cree una nueva.
    viewModel: NotasEj3ViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(state.mensajeExito) {
        state.mensajeExito?.let { mensaje ->
            Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show()
            viewModel.onEvent(NotasEj3Event.OnMensajeExitoMostrado)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Ej3: ViewModel (Intentos: ${state.intentosGuardado})") })
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
                onValueChange = { viewModel.onEvent(NotasEj3Event.OnTituloChange(it)) },
                label = { Text("Título") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.guardando
            )

            TextField(
                value = state.contenido,
                onValueChange = { viewModel.onEvent(NotasEj3Event.OnContenidoChange(it)) },
                label = { Text("Contenido") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.guardando
            )

            if (state.errorValidacion != null) {
                Text(
                    text = state.errorValidacion!!,
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Button(
                onClick = { viewModel.onEvent(NotasEj3Event.OnGuardarClick) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.guardando
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

            // Texto instructivo para pruebas de rotación
            Text(
                text = "Escribe un título y rota el emulador. ¡Verás que el texto no se pierde gracias al ViewModel!",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}
