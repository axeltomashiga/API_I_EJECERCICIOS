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
import java.io.File

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EjerciciosTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NotaPersistente(Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun NotaPersistente(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val archivo = File(context.filesDir, "nota.txt")

    if (!archivo.exists()) {
        archivo.createNewFile() // Esto crea el archivo vacío en el almacenamiento físico
    }

    var texto by rememberSaveable { mutableStateOf("") }
    var nota by rememberSaveable { mutableStateOf("") }

    nota = archivo.readText()

    Column(modifier=modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        TextField(value = texto, onValueChange = { texto = it })

        Text("nota: $nota")

        Button(onClick = {archivo.writeText (texto) }) {
            Text("Guardar")
        }
    }
}