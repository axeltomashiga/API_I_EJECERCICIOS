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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
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
import ar.edu.uade.axelhiga.ejercicios.ui.theme.EjerciciosTheme
import java.io.File

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

fun materiaATexto(materia: Materia): String {
    return "${materia.nombre}|${materia.anio ?: ""}|${if (materia.aprobada) "Aprobada" else "Reprobada"}"
}

fun textoAMateria(linea: String): Materia? {
    val partes = linea.split("|")
    if (partes.size == 3) {
        val nombre = partes[0].trim()
        val anio = partes[1].trim().toIntOrNull()
        val aprobada = partes[2].trim() == "Aprobada"
        if (nombre.isNotEmpty()) {
            return Materia(nombre, anio, aprobada)
        }
    }
    return null
}

fun guardarMaterias(archivo: File, lista: List<Materia>) {
    val contenido = lista.joinToString("\n") { materiaATexto(it) }
    archivo.writeText(contenido)
}

fun cargarMaterias(archivo: File): List<Materia> {
    if (!archivo.exists()) return emptyList()
    return archivo.readLines().mapNotNull { textoAMateria(it) }
}

@Composable
fun MateriaItem(materia: Materia, modifier: Modifier = Modifier) {
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
            Column {
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
                text = if (materia.aprobada) "Aprobada" else "Reprobada",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun ApunteRapido(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val archivo = remember { File(context.filesDir, "materias.txt") }

    var nombreInput by rememberSaveable { mutableStateOf("") }
    var anioInput by rememberSaveable { mutableStateOf("") }
    var aprobadaInput by rememberSaveable { mutableStateOf(false) }

    var listaMaterias by remember { mutableStateOf(emptyList<Materia>()) }

    LaunchedEffect(Unit) {
        if (!archivo.exists()) {
            archivo.createNewFile()
        }
        listaMaterias = cargarMaterias(archivo)
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

        Column (

        ) {
            Button(onClick = {
                if (nombreInput.isNotBlank()) {
                    val nuevaMateria = Materia(
                        nombre = nombreInput.trim(),
                        anio = anioInput.toIntOrNull(),
                        aprobada = aprobadaInput
                    )
                    val nuevaLista = listaMaterias + nuevaMateria
                    guardarMaterias(archivo, nuevaLista)
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
                if (archivo.exists()) {
                    archivo.delete()
                }
                listaMaterias = emptyList()
            }) {
                Text("Eliminar Todo")
            }

            Button(onClick = {
                listaMaterias=emptyList()
            }) {
                Text("Limpiar pantalla")
            }

            Button(onClick = {
                listaMaterias= cargarMaterias(archivo)
            }) {
                Text("recuperar")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Materias guardadas (${listaMaterias.size}):",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(listaMaterias) { materia ->
                MateriaItem(materia = materia)
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
