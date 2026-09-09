package ar.edu.uade.axelhiga.ejercicios

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.runtime.mutableDoubleStateOf
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
                    Calculadora(Modifier.padding(innerPadding))
                }
            }
        }
    }
}

fun calcular(a:Double, b:Double, operacion:String) : String {
    return when (operacion) {
        "+" -> (a + b).toString()
        "-" -> (a - b).toString()
        "*" -> (a * b).toString()
        "/" -> (a / b).toString()
        else -> "Operación no válida"
    }
}

@Composable
fun ButtonOperation(operation: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.padding(8.dp)
    ) {
        Text(operation)
    }
}

@Composable
fun Calculadora(modifier: Modifier = Modifier) {
    Log.d("Calculadora", "Calculadora iniciando")
    var numero1 by remember { mutableStateOf("") }
    var numero2 by remember { mutableStateOf("") }
    var operacion by remember { mutableStateOf("") }
    var resultado by remember { mutableStateOf("") }
    Log.d("Calculadora", "Calculadora iniciada")
    Column(modifier = modifier) {
        TextField(
            value = numero1,
            onValueChange = { numero1 = it },
            label = { Text("Número 1") },
            modifier = Modifier.padding(8.dp)
        )
        TextField(
            value = numero2,
            onValueChange = { numero2 = it },
            label = { Text("Número 2") },
            modifier = Modifier.padding(8.dp)
        )
        Row(modifier = Modifier.padding(8.dp)) {
            ButtonOperation("+", onClick = { operacion = "+" })
            ButtonOperation("-", onClick = { operacion = "-" })
            ButtonOperation("*", onClick = { operacion = "*" })
            ButtonOperation("/", onClick = { operacion = "/" })
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                resultado = calcular(numero1.toDouble(), numero2.toDouble(), operacion)
            },
            modifier = Modifier.padding(8.dp)
        ) {
            Text("Resultado")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Resultado: $resultado",
            modifier = Modifier.padding(8.dp)
        )
    }
}


@Preview(showBackground = true)
@Composable
fun CalculadoraPreview () {
    EjerciciosTheme() {
        Calculadora()
    }
}