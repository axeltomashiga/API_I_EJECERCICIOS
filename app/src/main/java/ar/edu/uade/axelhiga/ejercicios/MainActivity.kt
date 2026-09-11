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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ar.edu.uade.axelhiga.ejercicios.ui.theme.EjerciciosTheme
import androidx.core.net.toUri

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EjerciciosTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    CompartirRecomendacion(Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun CompartirRecomendacion(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    Column(modifier) {
        Button(onClick = {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, "¡Estoy aprendiendo Android con Compose!")
            }
            // Esta línea es la que realmente abre el menú del sistema
            //context.startActivity(intent)
            val shareIntent = Intent.createChooser(intent, "Compartir vía...")
            context.startActivity(shareIntent)
        }) {
            Text("Compartir mi progreso")
        }
        Spacer(Modifier.height(25.dp))
        Button(onClick = {
            val intentView = Intent(Intent.ACTION_VIEW).apply {
                data = "https://www.google.com".toUri()
            }
            context.startActivity(intentView)
        }) {
            Text("Ir a Google")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CompartirRecomendacionPreview () {
    EjerciciosTheme() {
        CompartirRecomendacion()
    }
}