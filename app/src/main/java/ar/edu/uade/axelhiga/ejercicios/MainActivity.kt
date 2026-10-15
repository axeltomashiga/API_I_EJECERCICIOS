package ar.edu.uade.axelhiga.ejercicios

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ar.edu.uade.axelhiga.ejercicios.domain.model.Materia
import ar.edu.uade.axelhiga.ejercicios.domain.repository.MateriaArchivoRepository
import ar.edu.uade.axelhiga.ejercicios.ui.theme.EjerciciosTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EjerciciosTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ApunteRapido(Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun MateriaItem(materia: Materia, onDelete: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = materia.nombre,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Año: ${materia.anio ?: "No especificado"}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Text(
                text = if (materia.aprobada) "Aprobada" else "No aprobada",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(end = 16.dp)
            )
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar materia",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun ApunteRapido(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    
    // Instanciamos el repositorio. Notar cómo ahora ya no conocemos "materias.txt" ni "filesDir"
    val repository = remember { MateriaArchivoRepository(context) }

    var nombreInput by rememberSaveable { mutableStateOf("") }
    var anioInput by rememberSaveable { mutableStateOf("") }
    var aprobadaInput by rememberSaveable { mutableStateOf(false) }

    var listaMaterias by remember { mutableStateOf(emptyList<Materia?>()) }

    LaunchedEffect(Unit) {
        // Le pedimos al repositorio que cargue las materias
        listaMaterias = repository.cargar()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Gestión de Materias",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(12.dp))

        TextField(
            value = nombreInput,
            onValueChange = { nombreInput = it },
            label = { Text("Nombre de la materia") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = anioInput,
            onValueChange = { anioInput = it },
            label = { Text("Año (ej. 1, 2, 3)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("¿Aprobada?")
            Spacer(modifier = Modifier.width(8.dp))
            Checkbox(
                checked = aprobadaInput,
                onCheckedChange = { aprobadaInput = it }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Column {
            Button(onClick = {
                if (nombreInput.isNotBlank()) {
                    val nuevaMateria = Materia(
                        nombre = nombreInput.trim(),
                        anio = anioInput.toIntOrNull(),
                        aprobada = aprobadaInput
                    )
                    val nuevaLista = listaMaterias + nuevaMateria
                    
                    // Delegamos el guardado al repositorio
                    repository.guardar(nuevaLista)
                    listaMaterias = nuevaLista

                    // Limpiar formulario
                    nombreInput = ""
                    anioInput = ""
                    aprobadaInput = false
                }
            }) {
                Text("Guardar")
            }

            Button(onClick = {
                // Delegamos el borrado al repositorio
                repository.borrarTodo()
                listaMaterias = emptyList()
            }) {
                Text("Eliminar Todo")
            }

            Button(onClick = {
                listaMaterias = emptyList()
            }) {
                Text("Limpiar pantalla")
            }

            Button(onClick = {
                // Volvemos a pedirle al repositorio que cargue
                listaMaterias = repository.cargar()
            }) {
                Text("Recuperar")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        val materiasValidasCount = listaMaterias.count { it != null }

        Text(
            text = "Materias guardadas ($materiasValidasCount):",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(listaMaterias) { materiaNullable ->
                materiaNullable?.let { materia ->
                    MateriaItem(
                        materia = materia,
                        onDelete = {
                            val nuevaLista = listaMaterias.toMutableList().apply { remove(materiaNullable) }
                            
                            // Guardamos mediante el repositorio
                            repository.guardar(nuevaLista)
                            listaMaterias = nuevaLista
                        }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ApunteRapidoPreview() {
    EjerciciosTheme {
        ApunteRapido()
    }
}