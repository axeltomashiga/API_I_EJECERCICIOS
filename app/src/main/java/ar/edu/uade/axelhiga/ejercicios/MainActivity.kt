package ar.edu.uade.axelhiga.ejercicios

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import ar.edu.uade.axelhiga.ejercicios.data.local.BibliotecaDatabase
import ar.edu.uade.axelhiga.ejercicios.data.repository.BibliotecaRepositoryImpl
import ar.edu.uade.axelhiga.ejercicios.presentation.biblioteca.BibliotecaScreen
import ar.edu.uade.axelhiga.ejercicios.presentation.biblioteca.BibliotecaViewModel
import ar.edu.uade.axelhiga.ejercicios.presentation.biblioteca.BibliotecaViewModelFactory
import ar.edu.uade.axelhiga.ejercicios.ui.theme.EjerciciosTheme

class MainActivity : ComponentActivity() {

    private val viewModel: BibliotecaViewModel by viewModels {
        val database = BibliotecaDatabase.getInstance(applicationContext)
        val repository = BibliotecaRepositoryImpl(database.libroDao())
        BibliotecaViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EjerciciosTheme {
                BibliotecaScreen(viewModel = viewModel)
            }
        }
    }
}
