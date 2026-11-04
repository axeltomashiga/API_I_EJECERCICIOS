package ar.edu.uade.axelhiga.ejercicios.ej1.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ar.edu.uade.axelhiga.ejercicios.ej1.viewmodel.NotasEj1ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

// 3. VIEW (Capa de UI - Compose)
// Solo se encarga de dibujar la pantalla basándose en el estado y notificar eventos de usuario al ViewModel.
@Composable
fun NotasScreen(viewModel: NotasEj1ViewModel) {

    // Observamos el estado expuesto por el ViewModel (recomendado usar collectAsStateWithLifecycle)
    val formState by viewModel.formState.collectAsStateWithLifecycle()
    val listaNotas by viewModel.notas.collectAsStateWithLifecycle()

    // DÓNDE SE DIBUJA LA INTERFAZ (UI)
    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("App de Notas (MVVM + Room)", style = MaterialTheme.typography.headlineSmall)

            // FORMULARIO: Eventos del usuario viajan hacia arriba (al ViewModel)
            TextField(
                value = formState.titulo,
                onValueChange = { viewModel.onTituloChange(it) },
                label = { Text("Título") },
                modifier = Modifier.fillMaxWidth()
            )

            TextField(
                value = formState.contenido,
                onValueChange = { viewModel.onContenidoChange(it) },
                label = { Text("Contenido") },
                modifier = Modifier.fillMaxWidth()
            )

            if (formState.mensajeError.isNotEmpty()) {
                Text(text = formState.mensajeError, color = Color.Red)
            }

            // ACCIÓN DEL USUARIO: Clic al botón
            Button(
                onClick = { viewModel.onGuardarClick() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar")
            }

            // DÓNDE SE DIBUJA LA INTERFAZ: Lista de notas guardadas desde Room
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(listaNotas) { nota ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = nota.titulo, style = MaterialTheme.typography.titleMedium)
                            Text(text = nota.contenido, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }
    }
}
