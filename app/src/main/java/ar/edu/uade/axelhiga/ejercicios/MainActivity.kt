package ar.edu.uade.axelhiga.ejercicios

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import ar.edu.uade.axelhiga.ejercicios.data.local.AppDatabase
import ar.edu.uade.axelhiga.ejercicios.data.local.entity.CategoriaEntity
import ar.edu.uade.axelhiga.ejercicios.presentation.notas.AgregarNotaScreen
import ar.edu.uade.axelhiga.ejercicios.ui.theme.EjerciciosTheme
import kotlinx.coroutines.flow.first

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EjerciciosTheme {
                val context = LocalContext.current
                
                // Inicialización de datos (Semillas)
                LaunchedEffect(Unit) {
                    val db = AppDatabase.getInstance(context)
                    val categoriaDao = db.categoriaDao()
                    
                    // Solo las insertamos si la tabla categorías está vacía
                    val categoriasActuales = categoriaDao.obtenerCategoriasConNotas().first()
                    if (categoriasActuales.isEmpty()) {
                        categoriaDao.insert(CategoriaEntity(nombre = "Personal"))
                        categoriaDao.insert(CategoriaEntity(nombre = "Trabajo"))
                    }
                }

                AgregarNotaScreen()
            }
        }
    }
}
