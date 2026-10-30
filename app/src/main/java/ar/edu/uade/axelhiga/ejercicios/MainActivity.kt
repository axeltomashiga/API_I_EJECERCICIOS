package ar.edu.uade.axelhiga.ejercicios

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import ar.edu.uade.axelhiga.ejercicios.data.local.AppDatabase
import ar.edu.uade.axelhiga.ejercicios.data.local.entity.NotaEntity
import ar.edu.uade.axelhiga.ejercicios.presentation.stringlist.SomeListScreen
import ar.edu.uade.axelhiga.ejercicios.ui.theme.EjerciciosTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EjerciciosTheme {
                val context = LocalContext.current
                
                // Código controlado temporal para insertar 3 notas automáticamente
                LaunchedEffect(Unit) {
                    val db = AppDatabase.getInstance(context)
                    val dao = db.notaDao()
                    
                    // Solo insertamos si la base de datos está vacía para no duplicar datos cada vez que gires la pantalla
                    if (dao.obtenerTodasDeUnaVez().isEmpty()) {
                        dao.insert(NotaEntity(titulo = "Primera Nota", contenido = "Esta es mi primera nota con Room.", importante = true))
                        dao.insert(NotaEntity(titulo = "Comprar viveres", contenido = "Pan, Leche, Huevos y Café.", importante = false))
                        dao.insert(NotaEntity(titulo = "Estudiar Jetpack Compose", contenido = "Revisar los modificadores y el manejo de estados.", importante = true))
                    }
                }

                SomeListScreen()
            }
        }
    }
}
