package ar.edu.uade.axelhiga.ejercicios

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import ar.edu.uade.axelhiga.ejercicios.ej1.data.AppDatabase
import ar.edu.uade.axelhiga.ejercicios.ej1.model.NotasEj1Repository
import ar.edu.uade.axelhiga.ejercicios.ej1.view.NotasScreen
import ar.edu.uade.axelhiga.ejercicios.ej1.viewmodel.NotasEj1ViewModelFactory
import ar.edu.uade.axelhiga.ejercicios.ej2.NotasEj2Screen
import ar.edu.uade.axelhiga.ejercicios.ej3.NotasEj3Screen
import ar.edu.uade.axelhiga.ejercicios.ej4.NotasEj4Screen
import ar.edu.uade.axelhiga.ejercicios.ej5.NotasEj5Screen
import ar.edu.uade.axelhiga.ejercicios.ui.theme.EjerciciosTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EjerciciosTheme {
                // NotasScreen(...)
                // NotasEj2Screen()
                // NotasEj3Screen()
                // NotasEj4Screen()

                // Pantalla del Ejercicio 5
                NotasEj5Screen()
            }
        }
    }
}