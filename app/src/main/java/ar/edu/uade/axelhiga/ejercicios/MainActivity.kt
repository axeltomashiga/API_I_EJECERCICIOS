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
import ar.edu.uade.axelhiga.ejercicios.ui.theme.EjerciciosTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EjerciciosTheme {
                val context = LocalContext.current
                val db = AppDatabase.getInstance(context)
                val repository = NotasEj1Repository(db.notaDao())
                val factory = NotasEj1ViewModelFactory(repository)
                
                NotasScreen(viewModel = viewModel(factory = factory))
            }
        }
    }
}
