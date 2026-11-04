package ar.edu.uade.axelhiga.ejercicios.ej11

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ar.edu.uade.axelhiga.ejercicios.ej10.model.ListaBorradorRepository
import ar.edu.uade.axelhiga.ejercicios.ej10.viewmodel.NotasEj10ViewModel

// Reutilizamos el ViewModel del Ejercicio 10 porque la lógica es idéntica,
// pero definimos una fábrica limpia para el inyector del Ej11.
class NotasEj11ViewModelFactory(
    private val repository: ListaBorradorRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // En un caso real crearíamos un NotasEj11ViewModel,
        // acá reusamos el NotasEj10ViewModel para no duplicar código
        if (modelClass.isAssignableFrom(NotasEj10ViewModel::class.java)) {
            return NotasEj10ViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
