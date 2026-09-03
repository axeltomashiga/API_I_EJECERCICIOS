package ar.edu.uade.axelhiga.ejercicios

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalProvider
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
                    Ficha()
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
fun Titulo(titulo: String) {
    Text(titulo,
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom=16.dp)
    )
}

@Composable
fun Ficha() {
    Column(modifier = Modifier.padding(top = 50.dp, start = 15.dp, end = 15.dp, bottom = 15.dp)) {
        Titulo("Ficha del estudiante")
        Estudiante()
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Button(onClick = { }, modifier = Modifier.padding(end = 10.dp, start = 20.dp).background(color = Color.Black)) {
                Text("Volver")
            }
            Button(onClick = { } ) {
                Text("Editar")
            }
        }
        Row(modifier = Modifier.background(color = Color.Black).fillMaxWidth()) {
            Button(onClick = { }) {
                Text("Volver")
            }
            Button(onClick = { } ) {
                Text("Editar")
            }
        }
        Button(onClick = { }, Modifier.fillMaxWidth() ) {
                Text("Nada")
        }
    }
}


@Composable
fun Estudiante() {
    Column() {
        DatoEstudiante("Nombre", "Ana")
        DatoEstudiante("Carrera", "Sistemas")
        DatoEstudiante("Anio", "1")
        DatoEstudiante("Edad", descripcionEdad(20))
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