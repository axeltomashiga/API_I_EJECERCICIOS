package ar.edu.uade.axelhiga.ejercicios

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
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
                    ClasificadorDeEdad(Modifier.padding(innerPadding))
                }
            }
        }
    }
}

fun clasificarEdad(edad : String) : String {
    val edadInt = edad.toIntOrNull()

    if (edadInt == null) {
        return "Error: Ingresa una edad válida"
    }else if (edadInt < 0) {
        return "Error: Ingresa una edad válida"
    }else if (edadInt < 18) {
        return "Menor de edad"
    }
    return "Mayor de edad"

}

fun clasificarEdadWhen(edad : String) : String {
    val edadInt = edad.toIntOrNull()

    val res = when {
        edadInt == null -> "Error: Ingresa una edad válida"
        edadInt < 0 -> "Error: Ingresa una edad válida"
        edadInt in 0..17 -> "Menor de edad"
        else -> "Mayor de edad"
    }
    return res
}

@Composable
fun ClasificadorDeEdad(modifier: Modifier = Modifier) {
    var nombre by remember { mutableStateOf("") }
    var edad by remember { mutableStateOf("") }
    var resultado by remember { mutableStateOf("") }
    Column(modifier = modifier.padding(start = 25.dp)) {
        TextField(value = nombre,
            label = { "Nombre" },
            onValueChange = {
            nombre = it
            }
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextField(value = edad,
            label = { "edad" },
            onValueChange = {
                edad = it
            }
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button( onClick = {resultado = clasificarEdadWhen(edad)}) {
            Text("Clasificar")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(resultado)
    }
}


@Preview(showBackground = true)
@Composable
fun ClasificadorDeEdadPreview () {
    EjerciciosTheme() {
        ClasificadorDeEdad()
    }
}