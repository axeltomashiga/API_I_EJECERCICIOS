package ar.edu.uade.axelhiga.ejercicios

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ar.edu.uade.axelhiga.ejercicios.ui.theme.EjerciciosTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EjerciciosTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    FichaDelEstudiante ()
                }
            }
        }
    }
}


@Composable
fun FichaDelEstudiante() {
    val nombre = "Ana"
    val edad = 20
    val promedio = 8.25
    val cursaProgramacion = true
    val proximoAnio = edad+1
    val materias: Int = 5
    val ciudad: String = "Cordoba"

    Column(modifier = Modifier.padding(start = 20.dp, top = 50.dp, end = 20.dp, bottom = 20.dp)) {
        Text("Nombre: $nombre")
        Text("Edad : $edad")
        Text("Promedio: $promedio")
        Text("Curso: $cursaProgramacion")
        Text("El proximo anio $nombre cumple $proximoAnio, ahora tiene $edad y esta estudiando programacion con un promedio de $promedio")
        Text("Materias: $materias")
        Text("Ciudad: $ciudad")
    }
}

@Preview(showBackground = true)
@Composable
fun FichaDelEstudiantePreview () {
    EjerciciosTheme() {
        FichaDelEstudiante()
    }
}