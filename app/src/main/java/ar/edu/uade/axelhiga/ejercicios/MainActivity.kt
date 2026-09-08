package ar.edu.uade.axelhiga.ejercicios

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
                    Contador()
                }
            }
        }
    }
}

@Composable
fun Contador() {
    var contador by remember { mutableIntStateOf(0) }
    Column(modifier = Modifier.padding(50.dp)) {
        Text("Valor: $contador", modifier = Modifier.align(Alignment.CenterHorizontally), fontSize = 30.sp, fontWeight = FontWeight.Bold)

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Button(modifier= Modifier.background(Color.Red).padding(16.dp).background(Color.Green),
                onClick = { contador++ }) {
                Text("+1")
            }

            Button(modifier= Modifier.background(Color.Green).padding(16.dp),
                onClick = { if (contador>0) contador-- }) {
                Text("-1")
            }

            Button (modifier= Modifier.background(Color.Green).padding(16.dp),
                onClick = { contador = 0 }) {
                Text("Reset")
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun DatoEstudiantePreview () {
    EjerciciosTheme() {
        Contador()
    }
}