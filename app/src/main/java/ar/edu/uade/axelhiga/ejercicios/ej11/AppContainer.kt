package ar.edu.uade.axelhiga.ejercicios.ej11

import android.content.Context
import ar.edu.uade.axelhiga.ejercicios.ej10.model.ListaBorradorRepository

/**
 * Fábrica manual de dependencias (AppContainer).
 * Centraliza la creación del Repositorio para que sea una instancia compartida
 * durante toda la vida de la app, y provee el Factory del ViewModel.
 */
class AppContainer(private val context: Context) {

    // Instancia única (Singleton manual) del repositorio que comparte toda la app
    // Modificado para usar el Repositorio del Ejercicio 12 (Dos Pantallas, SSOT)
    val notaEj12Repository: ar.edu.uade.axelhiga.ejercicios.ej12.model.NotaEj12Repository by lazy {
        ar.edu.uade.axelhiga.ejercicios.ej12.model.NotaEj12Repository(context)
    }

    // Ya no usamos este en Ej12, pero se preserva por compatibilidad
    val listaBorradorRepository: ListaBorradorRepository by lazy {
        ListaBorradorRepository(context)
    }

    // Provee el factory inyectando la dependencia pre-creada, en lugar de
    // pedirle a la Vista o al ViewModel que busquen un Context y la fabriquen.
    val viewModelFactory: NotasEj11ViewModelFactory by lazy {
        NotasEj11ViewModelFactory(listaBorradorRepository)
    }
}
