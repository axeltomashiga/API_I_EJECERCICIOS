package ar.edu.uade.axelhiga.ejercicios.ej9.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ar.edu.uade.axelhiga.ejercicios.ej9.viewmodel.EstadoCarga
import ar.edu.uade.axelhiga.ejercicios.ej9.viewmodel.NotasEj9ViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotasEj9Screen(viewModel: NotasEj9ViewModel) {

    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Ej9: Cargando, Listo, Falló") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // INDICADOR LECTURA
            when (val estLec = state.estadoLectura) {
                is EstadoCarga.Cargando -> Text("Recuperando nota del disco...", color = Color.Gray)
                is EstadoCarga.Fallo -> Text("❌ Error Leyendo: ${estLec.mensaje}", color = Color.Red, fontWeight = FontWeight.Bold)
                is EstadoCarga.Listo -> {
                    // Muestra el form sólo cuando termine de cargar
                    OutlinedTextField(
                        value = state.textoFormulario,
                        onValueChange = { viewModel.onTextoChange(it) },
                        label = { Text("Borrador") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        enabled = state.estadoEscritura != EstadoCarga.Cargando
                    )
                }
            }

            // INDICADOR ESCRITURA & BOTON
            if (state.estadoLectura == EstadoCarga.Listo) {
                Button(
                    onClick = { viewModel.onGuardarClick() },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = state.estadoEscritura != EstadoCarga.Cargando
                ) {
                    if (state.estadoEscritura == EstadoCarga.Cargando) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                        Spacer(Modifier.width(8.dp))
                        Text("Guardando...")
                    } else {
                        Text("Guardar en Disco")
                    }
                }

                // STATUS DE ERROR / EXITO DE ESCRITURA
                when (val estEsc = state.estadoEscritura) {
                    is EstadoCarga.Fallo -> {
                        Text("❌ Falló el guardado: ${estEsc.mensaje}", color = Color.Red, fontWeight = FontWeight.Bold)
                    }
                    is EstadoCarga.Listo -> {
                        if (state.mensajeExito != null) {
                            Text("✅ ${state.mensajeExito}", color = Color(0xFF007500), fontWeight = FontWeight.Bold)
                        }
                    }
                    else -> {}
                }
            }
        }
    }
}
