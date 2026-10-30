package ar.edu.uade.axelhiga.ejercicios.presentation.notas

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import ar.edu.uade.axelhiga.ejercicios.data.local.AppDatabase
import ar.edu.uade.axelhiga.ejercicios.data.local.entity.NotaEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgregarNotaScreen() {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    
    // Estados de la UI
    var titulo by remember { mutableStateOf("") }
    var contenido by remember { mutableStateOf("") }
    var importante by remember { mutableStateOf(false) }
    
    // Estados de control
    var mensaje by remember { mutableStateOf("") }
    var guardando by remember { mutableStateOf(false) } // Para evitar doble tap

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Nueva Nota (Room)") })
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
                onValueChange = { titulo = it },
                label = { Text("Título") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Campo de Contenido
            OutlinedTextField(
                value = contenido,
                onValueChange = { contenido = it },
                label = { Text("Contenido") },
                modifier = Modifier.fillMaxWidth().height(120.dp)
            )

            // Opción para marcar como importante
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = importante,
                    onCheckedChange = { importante = it }
                )
                Text("Marcar como Importante")
            }

            // Botón Guardar
            Button(
                onClick = {
                    // 1. Recortar espacios al comienzo y al final (trim)
                    val tituloLimpio = titulo.trim()
                    val contenidoLimpio = contenido.trim()

                    // 2. Validación de título vacío
                    if (tituloLimpio.isEmpty()) {
                        mensaje = "Error: El título no puede estar vacío"
                        return@Button
                    }

                    // 3. No permitir dos toques rápidos (evitar duplicados)
                    if (guardando) return@Button

                    guardando = true
                    mensaje = "Guardando..."

                    // Lanzar corrutina para insertar fuera del hilo principal (Dispatchers.IO)
                    coroutineScope.launch {
                        try {
                            val db = AppDatabase.getInstance(context)
                            val dao = db.notaDao()

                            val nuevaNota = NotaEntity(
                                titulo = tituloLimpio,
                                contenido = contenidoLimpio,
                                importante = importante
                            )

                            // Hacemos el insert en IO
                            withContext(Dispatchers.IO) {
                                dao.insert(nuevaNota)
                            }

                            // Volvemos al hilo principal para actualizar UI (Mensaje y limpieza)
                            mensaje = "¡Nota guardada con éxito!"
                            titulo = ""
                            contenido = ""
                            importante = false
                        } catch (e: Exception) {
                            mensaje = "Error al guardar: ${e.message}"
                        } finally {
                            // Rehabilitar botón
                            guardando = false
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !guardando // Si está guardando, deshabilitamos el botón
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
        }
    }
}