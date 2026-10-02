package ar.edu.uade.axelhiga.ejercicios

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ar.edu.uade.axelhiga.ejercicios.ui.theme.EjerciciosTheme
import androidx.core.net.toUri
import ar.edu.uade.axelhiga.ejercicios.domain.model.Materia
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
    return "${materia.nombre}|${materia.anio}|${if (materia.aprobada) "Aprobada" else "Reprobada"}"
}
fun textoAMateria(texto: String): Materia? {
    val partes = texto.split("|")
    if (partes.size == 3) {
        val nombre = partes[0]
        val anio = partes[1].toIntOrNull()
        val aprobada = partes[2] == "Aprobada"
        return Materia(nombre, anio, aprobada)
    }
    return null
}

@Composable
fun ApunteRapido(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val archivo = File(context.filesDir, "nota.txt")

    LaunchedEffect(Unit) {
        if (!archivo.exists()) {
            archivo.createNewFile()
        }
    }

    var texto by rememberSaveable { mutableStateOf("") }
    var nota by rememberSaveable { mutableStateOf("") }
    var materia by remember { mutableStateOf(Materia("", null, false))  }

    Column(modifier=modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Titulo: Apunte Rapido")

        TextField(value = texto, onValueChange = { texto = it })

        Text("nota: ${materiaATexto(materia)}")

        Text("Materia Actual: ${materia.nombre} - ${materia.anio} - ${if (materia.aprobada) "Aprobada" else "Reprobada"}")

        Button(onClick = {archivo.writeText (texto)
            nota = archivo.readText()
            materia = textoAMateria(nota) ?: Materia("", null, false)
        }) {
            Text("Guardar")
        }
        Button(onClick = {texto = ""
            nota = ""
            materia = Materia("", null, false)
        }) {
            Text("Limpiar Pantalla")
        }

        Button(onClick = {
            nota = if (archivo.exists()) {
                archivo.readText()
            } else {
                ""
            }
            materia = textoAMateria(nota) ?: Materia("", null, false)
        }) {
            Text("Recuperar")
        }

        Button(onClick = {if (archivo.exists()) {
            archivo.delete()
            nota = ""
            materia = textoAMateria(nota) ?: Materia("", null, false)
        }
        }) {
            Text("Eliminar Guardado")
        }
    }
}