package ar.edu.uade.axelhiga.ejercicios

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ar.edu.uade.axelhiga.ejercicios.ui.theme.EjerciciosTheme
import ar.edu.uade.axelhiga.ejercicios.presentation.organizadorcursada.OrganizadorCursadaScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EjerciciosTheme {
                OrganizadorCursadaScreen()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CompartirRecomendacionPreview () {
    EjerciciosTheme() {
        OrganizadorCursadaScreen()
    }
}