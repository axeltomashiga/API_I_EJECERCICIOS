package ar.edu.uade.axelhiga.ejercicios.ej5

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotasEj5Screen(
    viewModel: NotasEj5ViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(state.mensajeExito) {
        state.mensajeExito?.let { msj ->
            Toast.makeText(context, msj, Toast.LENGTH_SHORT).show()
            viewModel.onEvent(NotasEj5Event.OnMensajeExitoMostrado)
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Ej5: Validación Separada") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Decisión Visual: isError remarca todo el campo de rojo y lo mostramos cerca
            TextField(
                value = state.titulo,
                onValueChange = { viewModel.onEvent(NotasEj5Event.OnTituloChange(it)) },
                label = { Text("Título") },
                isError = state.errorTitulo != null,
                supportingText = {
                    if (state.errorTitulo != null) {
                        Text(text = state.errorTitulo!!, color = MaterialTheme.colorScheme.error)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.guardando
            )

            // Decisión Visual: isError remarca todo el campo de rojo y lo mostramos cerca
            TextField(
                value = state.contenido,
                onValueChange = { viewModel.onEvent(NotasEj5Event.OnContenidoChange(it)) },
                label = { Text("Contenido (min. 10 chars)") },
                isError = state.errorContenido != null,
                supportingText = {
                    if (state.errorContenido != null) {
                        Text(text = state.errorContenido!!, color = MaterialTheme.colorScheme.error)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.guardando
            )

            Button(
                onClick = { viewModel.onEvent(NotasEj5Event.OnGuardarClick) },
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
        }
    }
}
