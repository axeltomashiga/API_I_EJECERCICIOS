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
import ar.edu.uade.axelhiga.ejercicios.ej6.model.NotaRepositoryInMemoryImpl
import ar.edu.uade.axelhiga.ejercicios.ej6.view.NotasEj6Screen
import ar.edu.uade.axelhiga.ejercicios.ej6.viewmodel.NotasEj6ViewModelFactory
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ar.edu.uade.axelhiga.ejercicios.ej12.EditorEj12ViewModelFactory
import ar.edu.uade.axelhiga.ejercicios.ej12.ListaEj12ViewModelFactory
import ar.edu.uade.axelhiga.ejercicios.ej12.view.EditorEj12Screen
import ar.edu.uade.axelhiga.ejercicios.ej12.view.ListaEj12Screen
import ar.edu.uade.axelhiga.ejercicios.ui.theme.EjerciciosTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Obtenemos el AppContainer de la Application
        val appContainer = (application as MyApplication).container
        val repository = appContainer.notaEj12Repository
        
        setContent {
            EjerciciosTheme {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "lista") {
                    
                    composable("lista") {
                        val factory = ListaEj12ViewModelFactory(repository)
                        ListaEj12Screen(
                            viewModel = viewModel(factory = factory),
                            onNavigateToEditor = { id -> 
                                val ruta = if (id == null) "editor" else "editor?id=$id"
                                navController.navigate(ruta) 
                            }
                        )
                    }

                    // Declaramos el parámetro opcional `?id={id}`
                    composable("editor?id={id}") { backStackEntry ->
                        val idParam = backStackEntry.arguments?.getString("id")
                        val factory = EditorEj12ViewModelFactory(repository, notaId = idParam)
                        
                        EditorEj12Screen(
                            viewModel = viewModel(factory = factory),
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}