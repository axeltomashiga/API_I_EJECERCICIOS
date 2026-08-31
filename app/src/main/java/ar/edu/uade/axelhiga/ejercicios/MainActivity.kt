package ar.edu.uade.axelhiga.ejercicios

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
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ar.edu.uade.axelhiga.ejercicios.ui.theme.EjerciciosTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EjerciciosTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Estudiante()
                }
            }
        }
    }
}

fun descripcionEdad(edad: Int): String {
    return if (edad > 18) {
        "$edad anios, es mayor de edad"
    } else {
        "$edad anios, es menor de edad"
    }
}


@Composable
fun Estudiante() {
    Column(modifier = Modifier.padding(top = 40.dp, start = 15.dp, end = 15.dp, bottom = 15.dp)) {
        DatoEstudiante("Nombre", "Ana")
        DatoEstudiante("Carrera", "Sistemas")
        DatoEstudiante("Anio", "1")
        Text("Tiene ${descripcionEdad(20)}")
    }
}
@Composable
fun DatoEstudiante(etiqueta: String, valor: String) {
    Column() {
        Text("$etiqueta: $valor")
    }
}


@Preview(showBackground = true)
@Composable
fun DatoEstudiantePreview () {
    EjerciciosTheme() {
        DatoEstudiante("nombre", "Axel")
    }
}